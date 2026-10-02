package com.matrimony.backend.repository;

import com.matrimony.backend.entity.ProfilePhoto;
import com.matrimony.backend.enums.ModerationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfilePhotoRepository extends JpaRepository<ProfilePhoto, Long> {
    List<ProfilePhoto> findByProfileIdOrderByDisplayOrderAsc(Long profileId);

    long countByProfileId(Long profileId);

    Optional<ProfilePhoto> findFirstByProfileIdAndPrimaryPhotoTrueAndModerationStatusNot(Long profileId, ModerationStatus moderationStatus);

    Optional<ProfilePhoto> findFirstByProfileIdAndModerationStatusNotOrderByDisplayOrderAsc(Long profileId, ModerationStatus moderationStatus);

    Optional<ProfilePhoto> findFirstByProfileIdAndPrimaryPhotoTrueAndModerationStatus(Long profileId, ModerationStatus status);

    default Optional<ProfilePhoto> findDisplayPhoto(Long profileId) {
        return findFirstByProfileIdAndPrimaryPhotoTrueAndModerationStatusNot(profileId, ModerationStatus.REJECTED)
                .or(() -> findFirstByProfileIdAndModerationStatusNotOrderByDisplayOrderAsc(profileId, ModerationStatus.REJECTED));
    }

    Page<ProfilePhoto> findByModerationStatus(ModerationStatus moderationStatus, Pageable pageable);
}
