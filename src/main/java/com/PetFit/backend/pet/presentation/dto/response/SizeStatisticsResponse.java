package com.PetFit.backend.pet.presentation.dto.response;

import java.util.List;

/**
 * 유사 체형 사용자 그룹의 사이즈 선택 통계.
 * PDF 1순위 요구사항: "이 강아지와 체형이 비슷한 사용자 78%가 L 사이즈를 선택했습니다."
 */
public record SizeStatisticsResponse(
        Long petId,
        String petName,
        Double chestSize,
        Long productId,              // null 가능 (전체 상품 통계 시)
        String productName,          // null 가능
        int similarUserCount,        // 유사 체형 사용자 수
        long totalOrderCount,        // 그룹의 총 주문 건수 (size 있는 것만)
        String topSize,              // 가장 많이 선택된 사이즈 (예: "L")
        int topSizePercent,          // top 사이즈 비율 (예: 78)
        String summary,              // 사용자에게 보여줄 한 줄 요약
        List<SizeDistribution> distributions  // 전체 분포
) {
    public record SizeDistribution(
            String size,             // "XS", "S", "M", "L", "XL", ...
            long count,              // 해당 사이즈 주문 건수
            double percent           // 비율 (소수점 1자리, 예: 78.0)
    ) {}
}
