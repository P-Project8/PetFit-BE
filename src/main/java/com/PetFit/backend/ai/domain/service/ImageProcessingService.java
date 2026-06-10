package com.PetFit.backend.ai.domain.service;

import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.AiErrorStatus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

/**
 * AI 스타일링 결과 이미지의 다운사이징 + 워터마크 처리.
 *
 * 구독 등급별 차별화:
 * - FREE 사용자: 512px 리사이즈 + "PetFit Free" 워터마크
 * - PREMIUM 사용자: 원본 그대로
 */
@Slf4j
@Service
public class ImageProcessingService {

    public static final int FREE_MAX_WIDTH = 512;
    private static final String WATERMARK_TEXT = "PetFit Free";
    private static final float WATERMARK_OPACITY = 0.45f;
    private static final int CONNECT_TIMEOUT_MS = 5_000;
    private static final int READ_TIMEOUT_MS = 15_000;

    /**
     * URL에서 이미지를 다운로드하여 byte 배열로 반환.
     */
    public byte[] fetch(String imageUrl) {
        try {
            URL url = URI.create(imageUrl).toURL();
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);

            try (InputStream in = connection.getInputStream();
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                in.transferTo(out);
                return out.toByteArray();
            }
        } catch (IOException e) {
            log.error("이미지 다운로드 실패: url={} message={}", imageUrl, e.getMessage());
            throw new RestApiException(AiErrorStatus.AI_SERVICE_ERROR);
        }
    }

    /**
     * FREE 사용자용 처리 — maxWidth로 다운사이징 + 워터마크 합성.
     */
    public byte[] applyFreeTierProcessing(byte[] originalBytes) {
        return applyFreeTierProcessing(originalBytes, FREE_MAX_WIDTH);
    }

    public byte[] applyFreeTierProcessing(byte[] originalBytes, int maxWidth) {
        try {
            BufferedImage source = ImageIO.read(new ByteArrayInputStream(originalBytes));
            if (source == null) {
                throw new RestApiException(AiErrorStatus.AI_RESPONSE_PARSE_ERROR);
            }

            BufferedImage resized = resize(source, maxWidth);
            BufferedImage watermarked = watermark(resized, WATERMARK_TEXT);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(watermarked, "png", out);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("이미지 처리 실패: {}", e.getMessage());
            throw new RestApiException(AiErrorStatus.AI_RESPONSE_PARSE_ERROR);
        }
    }

    private BufferedImage resize(BufferedImage source, int maxWidth) {
        int srcWidth = source.getWidth();
        int srcHeight = source.getHeight();

        if (srcWidth <= maxWidth) {
            return source; // 이미 작으면 그대로 사용
        }

        double ratio = (double) maxWidth / srcWidth;
        int targetWidth = maxWidth;
        int targetHeight = (int) Math.round(srcHeight * ratio);

        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resized.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(source, 0, 0, targetWidth, targetHeight, null);
        } finally {
            g.dispose();
        }
        return resized;
    }

    private BufferedImage watermark(BufferedImage image, String text) {
        BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        try {
            g.drawImage(image, 0, 0, null);

            // 폰트 크기를 이미지 너비에 비례
            int fontSize = Math.max(14, image.getWidth() / 18);
            Font font = new Font("SansSerif", Font.BOLD, fontSize);
            g.setFont(font);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            FontRenderContext frc = g.getFontRenderContext();
            Rectangle2D textBounds = font.getStringBounds(text, frc);
            int padding = Math.max(8, fontSize / 2);
            int x = image.getWidth() - (int) textBounds.getWidth() - padding;
            int y = image.getHeight() - padding;

            // 반투명 배경 박스
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, WATERMARK_OPACITY));
            g.setColor(Color.BLACK);
            g.fillRect(
                    x - padding / 2,
                    y - (int) textBounds.getHeight() + (int) textBounds.getY() / 4 - 2,
                    (int) textBounds.getWidth() + padding,
                    (int) textBounds.getHeight() + 4);

            // 워터마크 텍스트 (불투명)
            g.setComposite(AlphaComposite.SrcOver);
            g.setColor(Color.WHITE);
            g.drawString(text, x, y);
        } finally {
            g.dispose();
        }
        return out;
    }
}
