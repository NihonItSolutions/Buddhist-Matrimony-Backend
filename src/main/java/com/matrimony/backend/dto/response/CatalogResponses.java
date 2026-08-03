package com.matrimony.backend.dto.response;

import java.math.BigDecimal;

public final class CatalogResponses {
    private CatalogResponses() {
    }

    public record MembershipPlanResponse(Long id, String name, String code, String description, BigDecimal price, String currency, int durationDays, int dailyInterestLimit, int contactViewLimit, int messageLimit, boolean profileBoostAllowed, boolean assistedService, boolean active) {
    }

    public record MasterDataResponse(Long id, String type, String name, String code, Long parentId, boolean active, int displayOrder) {
    }
}
