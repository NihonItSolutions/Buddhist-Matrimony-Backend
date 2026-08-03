package com.matrimony.backend.dto.request;

import com.matrimony.backend.enums.Gender;
import com.matrimony.backend.enums.ProfileCreatedFor;
import com.matrimony.backend.validation.StrongPassword;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotNull ProfileCreatedFor profileCreatedFor,
        @NotNull Gender gender,
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @NotBlank @Email @Size(max = 180) String email,
        @NotBlank @Pattern(regexp = "^[0-9]{7,15}$", message = "Mobile number must contain 7 to 15 digits") String mobileNumber,
        @NotBlank @Pattern(regexp = "^\\+[1-9][0-9]{0,3}$", message = "Country code must start with + and contain country digits") String countryCode,
        @StrongPassword String password,
        @AssertTrue(message = "Terms must be accepted") boolean acceptedTerms,
        @AssertTrue(message = "Privacy policy must be accepted") boolean acceptedPrivacyPolicy
) {
}
