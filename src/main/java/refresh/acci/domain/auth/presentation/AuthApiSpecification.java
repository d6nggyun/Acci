package refresh.acci.domain.auth.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import refresh.acci.domain.auth.presentation.dto.TokenExchangeRequest;
import refresh.acci.domain.auth.presentation.dto.TokenResponse;
import refresh.acci.domain.user.model.CustomUserDetails;
import refresh.acci.global.exception.ErrorResponseEntity;

@Tag(name = "Auth (인증)", description = "Auth (인증) 관련 API")
public interface AuthApiSpecification {

    @Operation(
            summary = "OAuth 인증 코드 교환",
            description = "OAuth 로그인 후 받은 인증 코드(Authorization Code)를 Access Token과 Refresh Token으로 교환합니다. <br><br>" +
                    "Refresh Token은 HttpOnly 쿠키로 자동 저장되며, Access Token은 응답 바디로 반환됩니다. <br><br>" +
                    "프론트엔드는 Access Token을 Authorization 헤더에 포함하여 API 요청을 보내야 합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "토큰 교환 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TokenResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                "grantType": "Bearer",
                                                "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
                                                "accessTokenExpiresIn": 1735689600000
                                            }
                                            """))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "유효하지 않은 인증 코드",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponseEntity.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                "code": 401,
                                                "name": "INVALID_TOKEN",
                                                "message": "유효하지 않은 토큰입니다.",
                                                "errors": null
                                            }
                                            """)))
            })
    ResponseEntity<TokenResponse> exchangeToken(@Valid @RequestBody TokenExchangeRequest request);

    @Operation(
            summary = "Access Token 재발급",
            description = "만료된 Access Token을 Refresh Token을 이용하여 재발급합니다. <br><br>" +
                    "Refresh Token은 HttpOnly 쿠키에서 자동으로 읽어오며, 새로운 Access Token을 응답 바디로 반환합니다. <br><br>" +
                    "Refresh Token이 만료된 경우 재로그인이 필요합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "토큰 재발급 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TokenResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                "grantType": "Bearer",
                                                "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
                                                "accessTokenExpiresIn": 1735689600000
                                            }
                                            """))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Refresh Token이 없거나 유효하지 않음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponseEntity.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                "code": 401,
                                                "name": "REFRESH_TOKEN_NOT_FOUND",
                                                "message": "리프레시 토큰이 존재하지 않습니다.",
                                                "errors": null
                                            }
                                            """)))
            })
    ResponseEntity<TokenResponse> refresh(@CookieValue(name = "refreshToken") String refreshToken);

    @Operation(
            summary = "로그아웃",
            description = "현재 로그인된 사용자를 로그아웃 처리합니다. <br><br>" +
                    "HttpOnly 쿠키에 저장된 Refresh Token을 삭제하며, Access Token은 클라이언트에서 직접 삭제해야 합니다. <br><br>" +
                    "이 API는 인증이 필요하며, Authorization 헤더에 유효한 Access Token을 포함해야 합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "로그아웃 성공",
                            content = @Content()),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않은 사용자",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponseEntity.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                "code": 401,
                                                "name": "JWT_ENTRY_POINT",
                                                "message": "인증되지 않은 사용자입니다.",
                                                "errors": null
                                            }
                                            """)))
            })
    ResponseEntity<Void> logout(@AuthenticationPrincipal CustomUserDetails userDetails,
                                HttpServletResponse response);
}