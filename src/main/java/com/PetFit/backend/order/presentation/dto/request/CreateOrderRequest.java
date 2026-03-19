package com.PetFit.backend.order.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateOrderRequest(
        @NotBlank(message = "배송 주소는 필수입니다.")
        String address,

        @NotBlank(message = "연락처는 필수입니다.")
        String phone,

        @NotBlank(message = "수령인 이름은 필수입니다.")
        String recipientName
) {
}
