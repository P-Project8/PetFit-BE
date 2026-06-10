package com.PetFit.backend.pet.application.usecase;

import com.PetFit.backend.global.exception.RestApiException;
import com.PetFit.backend.global.exception.code.status.PetErrorStatus;
import com.PetFit.backend.order.domain.repository.OrderItemRepository;
import com.PetFit.backend.pet.domain.entity.PetProfile;
import com.PetFit.backend.pet.domain.repository.PetProfileRepository;
import com.PetFit.backend.pet.domain.service.PetService;
import com.PetFit.backend.pet.presentation.dto.request.CreatePetRequest;
import com.PetFit.backend.pet.presentation.dto.request.UpdatePetRequest;
import com.PetFit.backend.pet.presentation.dto.response.PetResponse;
import com.PetFit.backend.pet.presentation.dto.response.SimilarPetCurationResponse;
import com.PetFit.backend.pet.presentation.dto.response.SimilarPetCurationResponse.RecommendedProduct;
import com.PetFit.backend.pet.presentation.dto.response.SizeRecommendationResponse;
import com.PetFit.backend.pet.presentation.dto.response.SizeRecommendationResponse.OptionFit;
import com.PetFit.backend.pet.presentation.dto.response.SizeStatisticsResponse;
import com.PetFit.backend.pet.presentation.dto.response.SizeStatisticsResponse.SizeDistribution;
import com.PetFit.backend.product.domain.entity.Product;
import com.PetFit.backend.product.domain.entity.ProductOption;
import com.PetFit.backend.product.domain.service.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PetUseCase {

    private static final double SIMILAR_CHEST_RATIO = 0.20; // ±20%
    private static final int CURATION_LIMIT = 10;

    private final PetService petService;
    private final ProductService productService;
    private final PetProfileRepository petProfileRepository;
    private final OrderItemRepository orderItemRepository;

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

    /**
     * 유사 체형 큐레이션.
     * 본인 반려견과 가슴둘레 ±20% 범위의 다른 사용자들의 구매 데이터에서 인기 상품 추천.
     */
    @Transactional(readOnly = true)
    public SimilarPetCurationResponse curateSimilarProducts(String userId, Long petId) {
        PetProfile pet = petService.findOwnedOrThrow(petId, userId);

        double minChest = pet.getChestSize() * (1 - SIMILAR_CHEST_RATIO);
        double maxChest = pet.getChestSize() * (1 + SIMILAR_CHEST_RATIO);

        List<String> similarUserIds = petProfileRepository.findUserIdsWithSimilarChestSize(
                userId, minChest, maxChest);

        if (similarUserIds.isEmpty()) {
            return new SimilarPetCurationResponse(
                    pet.getId(), pet.getName(), pet.getChestSize(),
                    0, List.of());
        }

        List<Object[]> aggregates = orderItemRepository.findTopOrderedProductsByUserIds(similarUserIds);
        int totalOrders = aggregates.stream()
                .mapToInt(row -> ((Long) row[1]).intValue())
                .sum();

        List<RecommendedProduct> products = new ArrayList<>();
        for (Object[] row : aggregates) {
            if (products.size() >= CURATION_LIMIT) break;
            Long productId = (Long) row[0];
            Long orderCount = (Long) row[1];
            try {
                Product product = productService.findById(productId);
                products.add(RecommendedProduct.of(product, orderCount, totalOrders));
            } catch (RestApiException ignored) {
                // 상품이 삭제된 경우는 건너뜀
            }
        }

        return new SimilarPetCurationResponse(
                pet.getId(), pet.getName(), pet.getChestSize(),
                similarUserIds.size(), products);
    }

    /**
     * 유사 체형 사용자 그룹의 사이즈 선택 통계.
     * PDF 1순위 핵심 예시: "이 강아지와 체형이 비슷한 사용자 78%가 L 사이즈를 선택했습니다."
     *
     * @param productId null이면 전체 상품 통계, 값 있으면 해당 상품 한정 통계
     */
    @Transactional(readOnly = true)
    public SizeStatisticsResponse getSizeStatistics(String userId, Long petId, Long productId) {
        PetProfile pet = petService.findOwnedOrThrow(petId, userId);

        double minChest = pet.getChestSize() * (1 - SIMILAR_CHEST_RATIO);
        double maxChest = pet.getChestSize() * (1 + SIMILAR_CHEST_RATIO);

        List<String> similarUserIds = petProfileRepository.findUserIdsWithSimilarChestSize(
                userId, minChest, maxChest);

        String productName = null;
        if (productId != null) {
            try {
                productName = productService.findById(productId).getName();
            } catch (RestApiException ignored) {
                // 상품 없으면 productName 없이 진행
            }
        }

        if (similarUserIds.isEmpty()) {
            return new SizeStatisticsResponse(
                    pet.getId(), pet.getName(), pet.getChestSize(),
                    productId, productName,
                    0, 0L, null, 0,
                    "비슷한 체형의 다른 사용자가 아직 없어 사이즈 통계를 제공할 수 없습니다.",
                    List.of());
        }

        List<Object[]> rows = productId != null
                ? orderItemRepository.findSizeDistributionByUserIdsAndProduct(similarUserIds, productId)
                : orderItemRepository.findSizeDistributionByUserIds(similarUserIds);

        long total = rows.stream().mapToLong(r -> (Long) r[1]).sum();

        if (total == 0) {
            return new SizeStatisticsResponse(
                    pet.getId(), pet.getName(), pet.getChestSize(),
                    productId, productName,
                    similarUserIds.size(), 0L, null, 0,
                    "비슷한 체형 사용자의 사이즈 선택 데이터가 부족합니다.",
                    List.of());
        }

        List<SizeDistribution> distributions = rows.stream()
                .map(r -> {
                    String size = (String) r[0];
                    long count = (Long) r[1];
                    double percent = Math.round(100.0 * count / total * 10) / 10.0;
                    return new SizeDistribution(size, count, percent);
                })
                .toList();

        SizeDistribution top = distributions.get(0);
        int topPercent = (int) Math.round(top.percent());

        String summary = String.format(
                "이 강아지와 체형이 비슷한 사용자 %d%%가 %s 사이즈를 선택했습니다.",
                topPercent, top.size());

        return new SizeStatisticsResponse(
                pet.getId(), pet.getName(), pet.getChestSize(),
                productId, productName,
                similarUserIds.size(), total,
                top.size(), topPercent,
                summary,
                distributions);
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
