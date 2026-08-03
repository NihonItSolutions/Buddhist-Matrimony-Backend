package com.matrimony.backend.dto.request;

public record ReligiousDetailsRequest(
        String motherTongue,
        String religion,
        String community,
        String subCommunity,
        Boolean casteNoBar,
        String gothra,
        String manglikStatus
) {
}
