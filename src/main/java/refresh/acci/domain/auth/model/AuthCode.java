package refresh.acci.domain.auth.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AuthCode {

    private static final int DEFAULT_VALIDITY_SECONDS = 30;

    private final String code;
    private final String accessToken;
    private final Long accessTokenExpiresAt;
    private final Integer accessTokenMaxAge;
    private final String refreshToken;
    private final Long refreshTokenExpiresAt;
    private final Integer refreshTokenMaxAge;
    private final LocalDateTime expiresAt;

    public AuthCode(String code, String accessToken, Long accessTokenExpiresAt, Integer accessTokenMaxAge, String refreshToken, Long refreshTokenExpiresAt, Integer refreshTokenMaxAge, int validitySeconds) {
        this.code = code;
        this.accessToken = accessToken;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
        this.accessTokenMaxAge = accessTokenMaxAge;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
        this.refreshTokenMaxAge = refreshTokenMaxAge;
        this.expiresAt = LocalDateTime.now().plusSeconds(validitySeconds);
    }

    public static AuthCode of(String code, String accessToken, Long accessTokenExpiresAt, Integer accessTokenMaxAge, String refreshToken, Long refreshTokenExpiresAt, Integer refreshTokenMaxAge) {
        return new AuthCode(code, accessToken, accessTokenExpiresAt, accessTokenMaxAge, refreshToken, refreshTokenExpiresAt, refreshTokenMaxAge, DEFAULT_VALIDITY_SECONDS);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
