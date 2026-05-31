package com.PetFit.backend.subscription.presentation;

import com.PetFit.backend.global.annotation.CurrentUser;
import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.global.swagger.SubscriptionApi;
import com.PetFit.backend.subscription.application.usecase.SubscriptionUseCase;
import com.PetFit.backend.subscription.presentation.dto.response.CreditStatusResponse;
import com.PetFit.backend.subscription.presentation.dto.response.SubscriptionResponse;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription")
public class SubscriptionController implements SubscriptionApi {

    private final SubscriptionUseCase subscriptionUseCase;

    @GetMapping
    @Override
    public BaseResponse<SubscriptionResponse> getMy(
            @Parameter(hidden = true) @CurrentUser String userId) {
        return BaseResponse.onSuccess(subscriptionUseCase.getMy(userId));
    }

    @PostMapping("/upgrade")
    @Override
    public BaseResponse<SubscriptionResponse> upgrade(
            @Parameter(hidden = true) @CurrentUser String userId) {
        return BaseResponse.onSuccess(subscriptionUseCase.upgrade(userId));
    }

    @DeleteMapping
    @Override
    public BaseResponse<SubscriptionResponse> cancel(
            @Parameter(hidden = true) @CurrentUser String userId) {
        return BaseResponse.onSuccess(subscriptionUseCase.cancel(userId));
    }

    @GetMapping("/credits")
    @Override
    public BaseResponse<CreditStatusResponse> getCredits(
            @Parameter(hidden = true) @CurrentUser String userId) {
        return BaseResponse.onSuccess(subscriptionUseCase.getCredits(userId));
    }
}
