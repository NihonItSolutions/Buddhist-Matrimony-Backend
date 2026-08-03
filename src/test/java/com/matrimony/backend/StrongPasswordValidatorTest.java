package com.matrimony.backend;

import com.matrimony.backend.validation.StrongPasswordValidator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StrongPasswordValidatorTest {
    private final StrongPasswordValidator validator = new StrongPasswordValidator();

    @Test
    void acceptsStrongPassword() {
        assertThat(validator.isValid("StrongPassword@123", null)).isTrue();
    }

    @Test
    void rejectsWeakPassword() {
        assertThat(validator.isValid("password", null)).isFalse();
    }
}
