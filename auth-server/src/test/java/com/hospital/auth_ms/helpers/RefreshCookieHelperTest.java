package com.hospital.auth_ms.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.test.util.ReflectionTestUtils;

class RefreshCookieHelperTest {

    private RefreshCookieHelper helper(boolean secure, String sameSite) {
        RefreshCookieHelper helper = new RefreshCookieHelper();
        ReflectionTestUtils.setField(helper, "refreshTokenExpiration", 604800L);
        ReflectionTestUtils.setField(helper, "secure", secure);
        ReflectionTestUtils.setField(helper, "sameSite", sameSite);
        ReflectionTestUtils.setField(helper, "path", "/auth-server/auth/refresh-token");
        return helper;
    }

    @Test
    void createUsesConfiguredAttributes() {
        ResponseCookie cookie = helper(true, "Strict").create("abc");

        assertEquals("refresh_token", cookie.getName());
        assertEquals("abc", cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertTrue(cookie.isSecure());
        assertEquals("Strict", cookie.getSameSite());
        assertEquals("/auth-server/auth/refresh-token", cookie.getPath());
        assertEquals(Duration.ofSeconds(604800), cookie.getMaxAge());
    }

    @Test
    void devProfileCanDisableSecure() {
        ResponseCookie cookie = helper(false, "Lax").create("abc");

        assertFalse(cookie.isSecure());
        assertEquals("Lax", cookie.getSameSite());
    }

    @Test
    void clearKeepsSamePathAndExpiresImmediately() {
        ResponseCookie cookie = helper(true, "Strict").clear();

        assertEquals("", cookie.getValue());
        assertEquals("/auth-server/auth/refresh-token", cookie.getPath());
        assertEquals(Duration.ZERO, cookie.getMaxAge());
    }
}
