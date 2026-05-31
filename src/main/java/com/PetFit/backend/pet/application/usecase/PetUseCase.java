package com.PetFit.backend.pet.application.usecase;

import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.PetErrorStatus;
import com.PetFit.backend.pet.domain.entity.PetProfile;
import com.PetFit.backend.pet.domain.service.PetService;
import com.PetFit.backend.pet.presentation.dto.request.CreatePetRequest;
import com.PetFit.backend.pet.presentation.dto.request.UpdatePetRequest;
import com.PetFit.backend.pet.presentation.dto.response.PetResponse;
import com.PetFit.backend.pet.presentation.dto.response.SizeRecommendationResponse;
import com.PetFit.backend.pet.presentation.dto.response.SizeRecommendationResponse.OptionFit;
import com.PetFit.backend.product.domain.entity.Product;
import com.PetFit.backend.product.domain.entity.ProductOption;
import com.PetFit.backend.product.domain.service.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PetUseCase {

    private final PetService petService;
    private final ProductService productService;

    public PetResponse create(String userId, CreatePetRequest request) {
        petService.checkLimit(userId);

        PetProfile pet = PetProfile.builder()
                .userId(userId)
                .name(request.name())
                .breed(request.breed())
                .age(request.age())
                .weight(request.weight())
                .neckSize(request.neckSize())
                .chestSize(request.chestSize())
                .backLength(request.backLength())
                .imageUrl(request.imageUrl())
                .build();
        return PetResponse.from(petService.save(pet));
    }

    @Transactional(readOnly = true)
    public List<PetResponse> findMyPets(String userId) {
        return petService.findAllByUserId(userId).stream()
                .map(PetResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PetResponse findOne(String userId, Long petId) {
        return PetResponse.from(petService.findOwnedOrThrow(petId, userId));
    }

    public PetResponse update(String userId, Long petId, UpdatePetRequest request) {
        PetProfile pet = petService.findOwnedOrThrow(petId, userId);
        pet.update(request.name(), request.breed(), request.age(),
                request.weight(), request.neckSize(), request.chestSize(), request.backLength(),
                request.imageUrl());
        return PetResponse.from(petService.save(pet));
    }

    public void delete(String userId, Long petId) {
        PetProfile pet = petService.findOwnedOrThrow(petId, userId);
        petService.softDelete(pet);
    }

    /**
     * 반려견 가슴둘레 기반 사이즈 추천.
     * 각 옵션의 size 문자열(XS/S/M/L/XL/2XL/3XL...)을 표준 가슴둘레 범위에 매핑하여 추천.
     */
    @Transactional(readOnly = true)
    public SizeRecommendationResponse recommendSize(String userId, Long petId, Long productId) {
        PetProfile pet = petService.findOwnedOrThrow(petId, userId);
        Product product = productService.findById(productId);

        List<ProductOption> options = product.getOptions().stream()
                .filter(o -> !o.isDeleted())
                .filter(o -> o.getSize() != null && !o.getSize().isBlank())
                .distinct()
                .sorted(Comparator.comparingInt(o -> sizeOrder(o.getSize())))
                .toList();

        if (options.isEmpty()) {
            throw new RestApiException(PetErrorStatus.PET_SIZE_RECOMMENDATION_UNAVAILABLE);
        }

        // 각 사이즈의 적합도 계산
        List<OptionFit> fits = options.stream()
                .map(o -> evaluateFit(o.getSize(), pet.getChestSize()))
                .distinct()
                .toList();

        // 추천: "FIT" → "LOOSE" → "TOO_SMALL" → "TOO_LARGE" 우선순위
        String recommended = fits.stream()
                .filter(f -> "FIT".equals(f.fit()))
                .findFirst()
                .map(OptionFit::size)
                .orElseGet(() -> fits.stream()
                        .filter(f -> "LOOSE".equals(f.fit()))
                        .findFirst()
                        .map(OptionFit::size)
                        .orElse(fits.get(0).size()));

        String reasoning = String.format(
                "%s(%s)의 가슴둘레 %.1fcm를 기준으로 %s 사이즈가 가장 적합합니다.",
                pet.getName(), pet.getBreed(), pet.getChestSize(), recommended);

        return new SizeRecommendationResponse(
                pet.getId(), pet.getName(),
                product.getId(), product.getName(),
                recommended, reasoning, fits
        );
    }

    // ===== Private helpers =====

    /**
     * 사이즈를 가슴둘레(cm) 범위에 매핑하여 적합도 평가.
     * 표준 강아지 의류 사이즈 가이드 기준.
     */
    private OptionFit evaluateFit(String size, double chestCm) {
        SizeRange range = SizeRange.of(size);
        if (range == null) {
            return new OptionFit(size, "UNKNOWN", "사이즈 정보를 인식할 수 없습니다.");
        }

        if (chestCm < range.min - 4) {
            return new OptionFit(size, "TOO_LARGE",
                    String.format("권장 %.0f~%.0fcm 보다 큽니다.", range.min, range.max));
        }
        if (chestCm < range.min) {
            return new OptionFit(size, "LOOSE",
                    String.format("권장 %.0f~%.0fcm 보다 약간 큽니다.", range.min, range.max));
        }
        if (chestCm <= range.max) {
            return new OptionFit(size, "FIT",
                    String.format("권장 %.0f~%.0fcm 범위에 맞습니다.", range.min, range.max));
        }
        if (chestCm <= range.max + 4) {
            return new OptionFit(size, "TOO_SMALL",
                    String.format("권장 %.0f~%.0fcm 보다 약간 작습니다.", range.min, range.max));
        }
        return new OptionFit(size, "TOO_SMALL",
                String.format("권장 %.0f~%.0fcm 보다 많이 작습니다.", range.min, range.max));
    }

    private int sizeOrder(String size) {
        SizeRange range = SizeRange.of(size);
        return range != null ? range.order : 99;
    }

    /**
     * 표준 강아지 의류 사이즈 가이드 (가슴둘레 기준).
     * 출처: 일반적인 펫 의류 브랜드 사이즈 표 평균.
     */
    private static class SizeRange {
        final double min;
        final double max;
        final int order;

        SizeRange(double min, double max, int order) {
            this.min = min;
            this.max = max;
            this.order = order;
        }

        static SizeRange of(String rawSize) {
            String s = rawSize.trim().toUpperCase().replaceAll("\\s+", "");
            return switch (s) {
                case "XXS", "2XS" -> new SizeRange(20, 26, 0);
                case "XS" -> new SizeRange(26, 32, 1);
                case "S" -> new SizeRange(32, 38, 2);
                case "M" -> new SizeRange(38, 45, 3);
                case "L" -> new SizeRange(45, 53, 4);
                case "XL" -> new SizeRange(53, 60, 5);
                case "XXL", "2XL" -> new SizeRange(60, 68, 6);
                case "XXXL", "3XL" -> new SizeRange(68, 78, 7);
                case "4XL" -> new SizeRange(78, 88, 8);
                case "5XL" -> new SizeRange(88, 100, 9);
                default -> null;
            };
        }
    }
}
