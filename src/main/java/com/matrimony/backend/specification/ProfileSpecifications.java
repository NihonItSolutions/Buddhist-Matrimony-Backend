package com.matrimony.backend.specification;

import com.matrimony.backend.dto.request.ProfileSearchRequest;
import com.matrimony.backend.entity.MatrimonyProfile;
import com.matrimony.backend.enums.ProfileStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class ProfileSpecifications {
    private ProfileSpecifications() {
    }

    public static Specification<MatrimonyProfile> search(ProfileSearchRequest request, Long excludedProfileId) {
        return Specification.where(active())
                .and(excludeSelf(excludedProfileId))
                .and((root, query, cb) -> request.matrimonyId() == null ? null : cb.equal(root.get("user").get("matrimonyId"), request.matrimonyId()))
                .and((root, query, cb) -> request.gender() == null ? null : cb.equal(root.get("gender"), request.gender()))
                .and((root, query, cb) -> request.minimumAge() == null ? null : cb.lessThanOrEqualTo(root.get("dateOfBirth"), LocalDate.now().minusYears(request.minimumAge())))
                .and((root, query, cb) -> request.maximumAge() == null ? null : cb.greaterThanOrEqualTo(root.get("dateOfBirth"), LocalDate.now().minusYears(request.maximumAge() + 1L).plusDays(1)))
                .and((root, query, cb) -> request.minimumHeight() == null ? null : cb.greaterThanOrEqualTo(root.get("heightInCm"), request.minimumHeight()))
                .and((root, query, cb) -> request.maximumHeight() == null ? null : cb.lessThanOrEqualTo(root.get("heightInCm"), request.maximumHeight()))
                .and((root, query, cb) -> request.maritalStatus() == null ? null : cb.equal(root.get("maritalStatus"), request.maritalStatus()))
                .and(like("religion", request.religion()))
                .and(like("community", request.community()))
                .and(like("subCommunity", request.subCommunity()))
                .and(like("motherTongue", request.motherTongue()))
                .and(like("country", request.country()))
                .and(like("state", request.state()))
                .and(like("district", request.district()))
                .and(like("city", request.city()))
                .and(like("physicalStatus", request.physicalStatus()))
                .and(like("manglikStatus", request.manglikStatus()))
                .and((root, query, cb) -> request.lastActiveWithinDays() == null ? null : cb.greaterThanOrEqualTo(root.get("lastActiveAt"), LocalDateTime.now().minusDays(request.lastActiveWithinDays())))
                .and(keyword(request.keyword()));
    }

    private static Specification<MatrimonyProfile> active() {
        return (root, query, cb) -> cb.equal(root.get("profileStatus"), ProfileStatus.ACTIVE);
    }

    private static Specification<MatrimonyProfile> excludeSelf(Long profileId) {
        return (root, query, cb) -> profileId == null ? null : cb.notEqual(root.get("id"), profileId);
    }

    private static Specification<MatrimonyProfile> like(String field, String value) {
        return (root, query, cb) -> !StringUtils.hasText(value)
                ? null
                : cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
    }

    private static Specification<MatrimonyProfile> keyword(String keyword) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(keyword)) {
                return null;
            }
            String value = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("firstName")), value),
                    cb.like(cb.lower(root.get("lastName")), value),
                    cb.like(cb.lower(root.get("aboutMe")), value),
                    cb.like(cb.lower(root.get("city")), value),
                    cb.like(cb.lower(root.get("community")), value)
            );
        };
    }
}
