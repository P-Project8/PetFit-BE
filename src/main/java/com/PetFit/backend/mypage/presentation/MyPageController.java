package com.PetFit.backend.mypage.presentation;

import com.PetFit.backend.global.annotation.CurrentUser;
import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.global.swagger.MyPageApi;
import com.PetFit.backend.mypage.presentation.dto.response.MyPageResponse;
import com.PetFit.backend.mypage.application.usecase.MyPageUseCase;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController implements MyPageApi {

    private final MyPageUseCase myPageUseCase;

    @GetMapping
    @Override
    public BaseResponse<MyPageResponse> getMyPage(
            @Parameter(hidden = true) @CurrentUser String userId) {
        return BaseResponse.onSuccess(myPageUseCase.getMyPage(userId));
    }
}
