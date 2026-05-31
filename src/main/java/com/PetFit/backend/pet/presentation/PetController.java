package com.PetFit.backend.pet.presentation;

import com.PetFit.backend.global.annotation.CurrentUser;
import com.PetFit.backend.global.common.BaseResponse;
import com.PetFit.backend.global.swagger.PetApi;
import com.PetFit.backend.pet.application.usecase.PetUseCase;
import com.PetFit.backend.pet.presentation.dto.request.CreatePetRequest;
import com.PetFit.backend.pet.presentation.dto.request.UpdatePetRequest;
import com.PetFit.backend.pet.presentation.dto.response.PetResponse;
import com.PetFit.backend.pet.presentation.dto.response.SimilarPetCurationResponse;
import com.PetFit.backend.pet.presentation.dto.response.SizeRecommendationResponse;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pets")
public class PetController implements PetApi {

    private final PetUseCase petUseCase;

    @PostMapping
    @Override
    public BaseResponse<PetResponse> createPet(
            @Parameter(hidden = true) @CurrentUser String userId,
            @Valid @RequestBody CreatePetRequest request) {
        return BaseResponse.onSuccess(petUseCase.create(userId, request));
    }

    @GetMapping
    @Override
    public BaseResponse<List<PetResponse>> getMyPets(
            @Parameter(hidden = true) @CurrentUser String userId) {
        return BaseResponse.onSuccess(petUseCase.findMyPets(userId));
    }

    @GetMapping("/{petId}")
    @Override
    public BaseResponse<PetResponse> getPet(
            @Parameter(hidden = true) @CurrentUser String userId,
            @PathVariable Long petId) {
        return BaseResponse.onSuccess(petUseCase.findOne(userId, petId));
    }

    @PatchMapping("/{petId}")
    @Override
    public BaseResponse<PetResponse> updatePet(
            @Parameter(hidden = true) @CurrentUser String userId,
            @PathVariable Long petId,
            @Valid @RequestBody UpdatePetRequest request) {
        return BaseResponse.onSuccess(petUseCase.update(userId, petId, request));
    }

    @DeleteMapping("/{petId}")
    @Override
    public BaseResponse<Void> deletePet(
            @Parameter(hidden = true) @CurrentUser String userId,
            @PathVariable Long petId) {
        petUseCase.delete(userId, petId);
        return BaseResponse.onSuccess(null);
    }

    @GetMapping("/{petId}/size-recommendation")
    @Override
    public BaseResponse<SizeRecommendationResponse> recommendSize(
            @Parameter(hidden = true) @CurrentUser String userId,
            @PathVariable Long petId,
            @RequestParam Long productId) {
        return BaseResponse.onSuccess(petUseCase.recommendSize(userId, petId, productId));
    }

    @GetMapping("/{petId}/similar-products")
    @Override
    public BaseResponse<SimilarPetCurationResponse> curateSimilarProducts(
            @Parameter(hidden = true) @CurrentUser String userId,
            @PathVariable Long petId) {
        return BaseResponse.onSuccess(petUseCase.curateSimilarProducts(userId, petId));
    }
}
