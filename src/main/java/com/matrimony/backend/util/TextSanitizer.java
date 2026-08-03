package com.matrimony.backend.util;

import org.springframework.web.util.HtmlUtils;

public final class TextSanitizer {
    private TextSanitizer() {
    }

    public static String clean(String value) {
        if (value == null) {
            return null;
        }
        return HtmlUtils.htmlEscape(value.trim());
    }
}
