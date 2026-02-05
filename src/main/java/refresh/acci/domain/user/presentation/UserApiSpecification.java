package refresh.acci.domain.user.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import refresh.acci.domain.user.model.CustomUserDetails;
import refresh.acci.domain.user.presentation.dto.MyPageResponse;
import refresh.acci.global.exception.ErrorResponseEntity;

@Tag(name = "User (사용자)", description = "User (사용자) 관련 API")
public interface UserApiSpecification {

    @Operation(
            summary = "내 정보 조회",
            description = "현재 로그인한 사용자의 프로필 정보를 조회합니다. <br><br>" +
                    "이 API는 인증이 필요하며, HttpOnly 쿠키에 저장된 Access Token이 자동으로 전송됩니다. <br>" +
                    "프론트엔드는 API 요청 시 credentials: 'include' 옵션을 설정해야 합니다. <br><br>" +
                    "사용자의 이름, 이메일, 프로필 이미지, 권한 정보를 반환합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "내 정보 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MyPageResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                "name": "홍길동",
                                                "email": "hong@example.com",
                                                "profileImage": "https://example.com/profile.jpg",
                                                "role": "USER"
                                            }
                                            """))),
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
                                            """))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "사용자 정보를 찾을 수 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponseEntity.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                "code": 404,
                                                "name": "USER_NOT_FOUND",
                                                "message": "인증된 사용자 정보를 찾을 수 없습니다.",
                                                "errors": null
                                            }
                                            """)))
            })
    ResponseEntity<MyPageResponse> getMyPage(@AuthenticationPrincipal CustomUserDetails userDetails);

    @Operation(
            summary = "회원 탈퇴",
            description = "현재 로그인한 사용자를 탈퇴 처리합니다. <br><br>" +
                    "이 API는 인증이 필요하며, HttpOnly 쿠키에 저장된 Access Token이 자동으로 전송됩니다. <br>" +
                    "프론트엔드는 API 요청 시 credentials: 'include' 옵션을 설정해야 합니다. <br><br>" +
                    "탈퇴 시 사용자 정보는 즉시 삭제되지 않고, 논리 삭제(Soft Delete) 처리됩니다. <br>" +
                    "개인정보 보호법에 따라 탈퇴 후 6개월간 데이터가 보관되며, 이후 자동으로 완전 삭제됩니다. <br><br>" +
                    "탈퇴 처리 시 Access Token과 Refresh Token 쿠키가 모두 삭제됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "회원 탈퇴 성공",
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
                                            """))),
                    @ApiResponse(
                            responseCode = "404",
                            description = "사용자 정보를 찾을 수 없음",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponseEntity.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                "code": 404,
                                                "name": "USER_NOT_FOUND",
                                                "message": "인증된 사용자 정보를 찾을 수 없습니다.",
                                                "errors": null
                                            }
                                            """)))
            })
    ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletResponse response);
}