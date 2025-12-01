package com.PetFit.backend.auth.domain.email.ui;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.PetFit.backend.auth.domain.email.application.dto.request.SendVerificationRequest;
import com.PetFit.backend.auth.domain.email.application.dto.request.VerifyEmailRequest;
import com.PetFit.backend.auth.domain.email.application.dto.response.EmailVerificationResponse;
import com.PetFit.backend.auth.domain.email.application.usecase.SendEmailVerificationUseCase;
import com.PetFit.backend.auth.domain.email.application.usecase.VerifyEmailUseCase;
import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.global.swagger.EmailApi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailController implements EmailApi {

    private final SendEmailVerificationUseCase sendEmailVerificationUseCase;
    private final VerifyEmailUseCase verifyEmailUseCase;

    @PostMapping("/verification/send")
    @Override
    public BaseResponse<Void> sendVerification(@Valid @RequestBody SendVerificationRequest request) {
        sendEmailVerificationUseCase.sendVerification(request);
        return BaseResponse.onSuccess();
    }

    @PostMapping("/verification/verify")
    @Override
    public BaseResponse<EmailVerificationResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        EmailVerificationResponse response = verifyEmailUseCase.verifyEmail(request);
        return BaseResponse.onSuccess(response);
    }
}
