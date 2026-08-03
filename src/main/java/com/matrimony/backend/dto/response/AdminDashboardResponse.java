package com.matrimony.backend.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record AdminDashboardResponse(
        long totalUsers,
        long activeProfiles,
        long pendingProfiles,
        long pendingPhotos,
        long maleProfiles,
        long femaleProfiles,
        long interestsSentToday,
        long acceptedInterests,
        long activeSubscriptions,
        long openReports,
        long openSupportTickets,
        BigDecimal todayRevenue,
        BigDecimal monthlyRevenue,
        List<?> recentRegistrations,
        List<?> recentPayments
) {
}
