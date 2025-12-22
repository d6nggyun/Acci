package refresh.acci.domain.analysis.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import refresh.acci.domain.analysis.application.AnalysisService;
import refresh.acci.domain.analysis.presentation.dto.res.AnalysisResultResponse;
import refresh.acci.domain.analysis.presentation.dto.res.AnalysisUploadResponse;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
public class AnalysisController implements AnalysisApiSpecification{

    private final AnalysisService analysisService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AnalysisUploadResponse> analyze(@RequestPart("video") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.OK).body(analysisService.anaylze(file));
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
