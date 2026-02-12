package refresh.acci.global.security.oauth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import refresh.acci.global.util.CookieUtil;
import refresh.acci.global.util.SerializationUtil;

@Slf4j
@Component
@RequiredArgsConstructor
public class CookieOAuthAuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private static final int COOKIE_EXPIRE_SECONDS = 600; //10분

    private final CookieUtil cookieUtil;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return cookieUtil.getOAuthAuthorizationRequestCookie(request)
                .map(Cookie::getValue)
                .map(value -> SerializationUtil.deserialize(value, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            cookieUtil.deleteOAuthAuthorizationRequestCookie(response);
            return;
        }
        String serialized = SerializationUtil.serialize(authorizationRequest);
        cookieUtil.addOAuthAuthorizationRequestCookie(response, serialized, COOKIE_EXPIRE_SECONDS);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        OAuth2AuthorizationRequest authorizationRequest = loadAuthorizationRequest(request);
        cookieUtil.deleteOAuthAuthorizationRequestCookie(response);
        return authorizationRequest;
    }
}
