package refresh.acci.global.security.jwt;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresAt;
    private Long refreshTokenExpiresAt;
    private Integer accessTokenMaxAge;
    private Integer refreshTokenMaxAge;
}
