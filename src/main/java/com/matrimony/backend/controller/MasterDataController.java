package com.matrimony.backend.controller;

import com.matrimony.backend.dto.ApiResponse;
import com.matrimony.backend.dto.response.CatalogResponses.MasterDataResponse;
import com.matrimony.backend.enums.MasterDataType;
import com.matrimony.backend.service.CatalogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/master")
@RequiredArgsConstructor
@Tag(name = "Master Data")
public class MasterDataController {
    private final CatalogService service;

    @GetMapping("/countries")
    ApiResponse<List<MasterDataResponse>> countries() { return data(MasterDataType.COUNTRY, null); }
    @GetMapping("/states")
    ApiResponse<List<MasterDataResponse>> states(@RequestParam Long countryId) { return data(MasterDataType.STATE, countryId); }
    @GetMapping("/districts")
    ApiResponse<List<MasterDataResponse>> districts(@RequestParam Long stateId) { return data(MasterDataType.DISTRICT, stateId); }
    @GetMapping("/cities")
    ApiResponse<List<MasterDataResponse>> cities(@RequestParam Long districtId) { return data(MasterDataType.CITY, districtId); }
    @GetMapping("/religions")
    ApiResponse<List<MasterDataResponse>> religions() { return data(MasterDataType.RELIGION, null); }
    @GetMapping("/communities")
    ApiResponse<List<MasterDataResponse>> communities(@RequestParam Long religionId) { return data(MasterDataType.COMMUNITY, religionId); }
    @GetMapping("/sub-communities")
    ApiResponse<List<MasterDataResponse>> subCommunities(@RequestParam Long communityId) { return data(MasterDataType.SUB_COMMUNITY, communityId); }
    @GetMapping("/mother-tongues")
    ApiResponse<List<MasterDataResponse>> motherTongues() { return data(MasterDataType.MOTHER_TONGUE, null); }
    @GetMapping("/education-levels")
    ApiResponse<List<MasterDataResponse>> educationLevels() { return data(MasterDataType.EDUCATION_LEVEL, null); }
    @GetMapping("/occupations")
    ApiResponse<List<MasterDataResponse>> occupations() { return data(MasterDataType.OCCUPATION, null); }
    @GetMapping("/hobbies")
    ApiResponse<List<MasterDataResponse>> hobbies() { return data(MasterDataType.HOBBY, null); }
    @GetMapping("/interests")
    ApiResponse<List<MasterDataResponse>> interests() { return data(MasterDataType.INTEREST, null); }
    @GetMapping("/rashis")
    ApiResponse<List<MasterDataResponse>> rashis() { return data(MasterDataType.RASHI, null); }
    @GetMapping("/nakshatras")
    ApiResponse<List<MasterDataResponse>> nakshatras() { return data(MasterDataType.NAKSHATRA, null); }

    private ApiResponse<List<MasterDataResponse>> data(MasterDataType type, Long parentId) {
        return ApiResponse.ok("Master data", service.master(type, parentId));
    }
}
