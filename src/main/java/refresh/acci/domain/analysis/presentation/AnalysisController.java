package refresh.acci.domain.analysis.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import refresh.acci.domain.analysis.application.AnalysisService;
import refresh.acci.domain.analysis.presentation.dto.req.AnalysisUploadRequest;
import refresh.acci.domain.analysis.presentation.dto.res.AnalysisResultResponse;
import refresh.acci.domain.analysis.presentation.dto.res.AnalysisUploadResponse;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping
    public ResponseEntity<AnalysisUploadResponse> analyze(@Valid @RequestBody AnalysisUploadRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(analysisService.anaylze(request));
    }

    @GetMapping("/loading")
    public ResponseEntity<String> getLoadingVideo(@RequestParam LocalDate todayDate) {
        return ResponseEntity.status(HttpStatus.OK).body(analysisService.getLoadingVideo(todayDate));
    }

    @GetMapping("/{analysisId}")
    public ResponseEntity<AnalysisResultResponse> getAnalysisResult(@PathVariable UUID analysisId) {
        return ResponseEntity.status(HttpStatus.OK).body(analysisService.getAnalysisResult(analysisId));
    }
}
