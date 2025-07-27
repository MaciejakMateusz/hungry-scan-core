package com.hackybear.hungry_scan_core.filter;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class FilterBaseTest {

    private final FilterBase filter = new FilterBase() {
    };

    @Nested
    @DisplayName("getJwtParams()")
    class GetJwtParams {

        @Test
        @DisplayName("returns token & username when a jwt cookie is present")
        void returnsParamsWhenJwtPresent() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setCookies(
                    new Cookie("jwt", "token123"),
                    new Cookie("other", "value")
            );

            Function<String, String> usernameExtractor = t -> "alice";

            Map<String, String> result = filter.getJwtParams(request, usernameExtractor);

            assertEquals(2, result.size());
            assertEquals("token123", result.get("token"));
            assertEquals("alice", result.get("username"));
        }

        @Test
        @DisplayName("returns an empty map when no jwt cookie is present")
        void returnsEmptyWhenNoJwt() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setCookies(new Cookie("session", "xyz"));

            Map<String, String> result = filter.getJwtParams(request, t -> "ignored");

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("returns an empty map when the request has no cookies at all")
        void returnsEmptyWhenNoCookies() {
            MockHttpServletRequest request = new MockHttpServletRequest();

            Map<String, String> result = filter.getJwtParams(request, t -> "ignored");

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("invalidateJwtCookie()")
    class InvalidateJwtCookie {

        @Test
        @DisplayName("generates a ‘Secure; SameSite=None’ cookie when isSecure == true")
        void secureCookie() {
            String actual = filter.invalidateJwtCookie(true);

            String expected = ResponseCookie.from("jwt", "")
                    .path("/")
                    .httpOnly(true)
                    .secure(true)
                    .maxAge(0)
                    .sameSite("None")
                    .build()
                    .toString();

            assertEquals(expected, actual);
            assertTrue(actual.contains("Secure"));
            assertTrue(actual.contains("SameSite=None"));
        }

        @Test
        @DisplayName("generates a ‘SameSite=Strict’ cookie when isSecure == false")
        void insecureCookie() {
            String actual = filter.invalidateJwtCookie(false);

            String expected = ResponseCookie.from("jwt", "")
                    .path("/")
                    .httpOnly(true)
                    .secure(false)
                    .maxAge(0)
                    .sameSite("Strict")
                    .build()
                    .toString();

            assertEquals(expected, actual);
            assertFalse(actual.contains("Secure"));
            assertTrue(actual.contains("SameSite=Strict"));
        }
    }
}
