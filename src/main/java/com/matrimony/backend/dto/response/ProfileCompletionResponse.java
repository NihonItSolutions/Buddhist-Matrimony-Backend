package com.matrimony.backend.dto.response;

import java.util.List;

public record ProfileCompletionResponse(
        int percentage,
        List<String> missingSections,
        boolean canSubmitForApproval
) {
}
