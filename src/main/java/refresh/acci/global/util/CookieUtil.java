package refresh.acci.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import refresh.acci.global.config.CookieProperties;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CookieUtil {

    private static final String ACCESS_TOKEN_NAME = "accessToken";
    private static final String REFRESH_TOKEN_NAME = "refreshToken";
    private static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";

    private final CookieProperties cookieProperties;

    /**
     * JWT 토큰용 메서드
     */
    public void setAccessTokenCookie(HttpServletResponse response, String accessToken, int maxAge) {
        addCookie(response, ACCESS_TOKEN_NAME, accessToken, maxAge, cookieProperties.getAccessTokenSameSite());
    }

    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken, int maxAge) {
        addCookie(response, REFRESH_TOKEN_NAME, refreshToken, maxAge, cookieProperties.getRefreshTokenSameSite());
    }

    public void deleteAccessTokenCookie(HttpServletResponse response) {
        deleteCookie(response, ACCESS_TOKEN_NAME, cookieProperties.getAccessTokenSameSite());
    }

    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        deleteCookie(response, REFRESH_TOKEN_NAME, cookieProperties.getRefreshTokenSameSite());
    }

    public void setAuthTokenCookies(HttpServletResponse response, String accessToken, Integer accessMaxAge, String refreshToken, Integer refreshMaxAge) {
        setAccessTokenCookie(response, accessToken, accessMaxAge);
        setRefreshTokenCookie(response, refreshToken, refreshMaxAge);
        log.debug("인증 쿠키 설정 완료 - AccessToken, RefreshToken");
    }

    public void deleteAllAuthCookies(HttpServletResponse response) {
        deleteAccessTokenCookie(response);
        deleteRefreshTokenCookie(response);
        log.debug("인증 쿠키 삭제 완료 - AccessToken, RefreshToken");
    }

    /**
     * OAuth Authorization Request용 메서드
     */
    public Optional<Cookie> getOAuthAuthorizationRequestCookie(HttpServletRequest request) {
        return getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    }

    public void addOAuthAuthorizationRequestCookie(HttpServletResponse response, String value, int maxAge) {
        addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, value, maxAge, "Lax");
        log.debug("OAuth2 Authorization Request 쿠키 저장 완료");
    }

    public void deleteOAuthAuthorizationRequestCookie(HttpServletResponse response) {
        deleteCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, "Lax");
        log.debug("OAuth2 Authorization Request 쿠키 삭제 완료");
    }

    /**
     * 범용 쿠키 메서드
     */
    private Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge, String sameSite) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .path("/")
                .maxAge(maxAge)
                .sameSite(sameSite);

        if (cookieProperties.getDomain() != null && !cookieProperties.getDomain().isEmpty()) {
            builder.domain(cookieProperties.getDomain());
        }

        response.addHeader("Set-Cookie", builder.build().toString());
    }

    private void deleteCookie(HttpServletResponse response, String name, String sameSite) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .path("/")
                .maxAge(0)
                .sameSite(sameSite);

        if (cookieProperties.getDomain() != null && !cookieProperties.getDomain().isEmpty()) {
            builder.domain(cookieProperties.getDomain());
        }

        response.addHeader("Set-Cookie", builder.build().toString());
    }
}