package com.matrimony.backend.dto.request;

import jakarta.validation.constraints.Size;

public record AboutRequest(@Size(max = 2000) String aboutMe) {
}
