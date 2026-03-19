package com.PetFit.backend.auth.domain.email.domain.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.PetFit.backend.global.security.TokenProvider;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TokenProvider tokenProvider;
    private final EmailVerificationService emailVerificationService;

    @Value("${email.from}")
    private String fromEmail;

    // 인메모리 rate limiting
    private final Map<String, Long> cooldownMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> dailyAttemptMap = new ConcurrentHashMap<>();

    private static final int MAX_DAILY_ATTEMPTS = 5;
    private static final long COOLDOWN_MILLIS = 60_000;

    public void sendVerificationCode(String email) {
        if (isInCooldown(email)) {
            throw new RuntimeException("이메일 발송 쿨다운 중입니다. 잠시 후 다시 시도해주세요.");
        }

        if (isDailyLimitExceeded(email)) {
            throw new RuntimeException("일일 이메일 발송 횟수를 초과했습니다.");
        }

        String verificationCode = emailVerificationService.generateVerificationCode();
        emailVerificationService.saveVerificationCode(email, verificationCode);

        String subject = "[PetFit] 회원가입 이메일 인증";
        String html = ""
                + "<div style=\"font-family:Arial,sans-serif;color:#333;padding:20px;max-width:600px;margin:auto;\">"
                + "  <div style=\"text-align:center;margin-bottom:20px;\">"
                + "    <h1 style=\"margin:0;font-size:24px;color:#0064FF;\">PetFit</h1>"
                + "  </div>"
                + "  <p style=\"font-size:16px;\">안녕하세요!</p>"
                + "  <p style=\"font-size:16px;\">회원가입 인증을 위해 아래 인증 코드를 입력해주세요.</p>"
                + "  <div style=\"background:#f5f5f5;padding:20px;text-align:center;margin:20px 0;border-radius:8px;\">"
                + "    <div style=\"font-size:32px;font-weight:bold;color:#0064FF;letter-spacing:4px;margin:10px 0;\">"
                + verificationCode
                + "    </div>"
                + "  </div>"
                + "  <p style=\"font-size:14px;color:#888;\">이 인증 코드는 5분 후 만료됩니다.</p>"
                + "  <p style=\"font-size:14px;\">요청하지 않으셨다면 고객지원으로 문의해주세요.</p>"
                + "  <hr style=\"border:none;border-top:1px solid #eee;margin:30px 0;\"/>"
                + "  <div style=\"font-size:12px;color:#aaa;text-align:center;\">PetFit Inc, Seoul, Korea</div>"
                + "</div>";

        sendHtmlMail(email, subject, html);
        setCooldown(email);
        incrementAttemptCount(email);
    }

    public boolean verifySignupCode(String email, String code) {
        try {
            return emailVerificationService.verifyCode(email, code);
        } catch (Exception ex) {
            throw new IllegalArgumentException("인증 코드가 만료되었거나 유효하지 않습니다.");
        }
    }

    public boolean verifySignupToken(String token) {
        try {
            boolean isValid = tokenProvider.validateEmailVerificationToken(token, "signup");
            if (!isValid) {
                throw new IllegalArgumentException("유효하지 않은 인증 토큰입니다.");
            }
            return true;
        } catch (Exception ex) {
            throw new IllegalArgumentException("인증 링크가 만료되었거나 유효하지 않습니다.");
        }
    }

    public String getEmailFromToken(String token) {
        return tokenProvider.getEmailFromVerificationToken(token).orElse(null);
    }

    private void sendHtmlMail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setFrom(fromEmail, "PetFit");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("이메일 전송 성공: {}", to);
        } catch (MessagingException ex) {
            log.error("HTML 메일 전송 실패 to={}: {}", to, ex.getMessage(), ex);
            throw new RuntimeException("이메일 전송에 실패했습니다.", ex);
        } catch (Exception ex) {
            log.error("HTML 메일 전송 실패 to={}: {}", to, ex.getMessage(), ex);
            throw new RuntimeException("이메일 전송에 실패했습니다.", ex);
        }
    }

    private boolean isInCooldown(String email) {
        Long until = cooldownMap.get(email);
        if (until == null) return false;
        if (System.currentTimeMillis() > until) {
            cooldownMap.remove(email);
            return false;
        }
        return true;
    }

    private void setCooldown(String email) {
        cooldownMap.put(email, System.currentTimeMillis() + COOLDOWN_MILLIS);
    }

    private String todayKey(String email) {
        return email + ":" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private void incrementAttemptCount(String email) {
        dailyAttemptMap.merge(todayKey(email), 1, Integer::sum);
    }

    private boolean isDailyLimitExceeded(String email) {
        return dailyAttemptMap.getOrDefault(todayKey(email), 0) >= MAX_DAILY_ATTEMPTS;
    }
}
