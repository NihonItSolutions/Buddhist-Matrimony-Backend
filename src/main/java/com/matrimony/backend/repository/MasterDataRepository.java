package com.matrimony.backend.repository;

import com.matrimony.backend.entity.MasterData;
import com.matrimony.backend.enums.MasterDataType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MasterDataRepository extends JpaRepository<MasterData, Long> {
    List<MasterData> findByTypeAndActiveTrueOrderByDisplayOrderAscNameAsc(MasterDataType type);

    List<MasterData> findByTypeAndParentIdAndActiveTrueOrderByDisplayOrderAscNameAsc(MasterDataType type, Long parentId);

    boolean existsByTypeAndCode(MasterDataType type, String code);
}
