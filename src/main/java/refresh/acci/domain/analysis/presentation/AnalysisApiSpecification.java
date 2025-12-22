package refresh.acci.domain.analysis.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import refresh.acci.domain.analysis.presentation.dto.res.AnalysisResultResponse;
import refresh.acci.domain.analysis.presentation.dto.res.AnalysisUploadResponse;

import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "Analysis", description = "Analysis 관련 API")
public interface AnalysisApiSpecification {

    @Operation(summary = "영상 분석 업로드", description = "교통사고 영상을 업로드하여 AI 서버에 전송합니다.", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "영상 분석 업로드 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AnalysisUploadResponse.class)))
    })
    ResponseEntity<AnalysisUploadResponse> analyze(@RequestPart("video") MultipartFile file);

    ResponseEntity<String> getLoadingVideo(@RequestParam LocalDate todayDate);

    ResponseEntity<AnalysisResultResponse> getAnalysisResult(@PathVariable UUID analysisId);
}
