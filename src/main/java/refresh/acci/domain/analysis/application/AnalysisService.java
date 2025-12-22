package refresh.acci.domain.analysis.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import refresh.acci.domain.analysis.infra.AnalysisRepository;
import refresh.acci.domain.analysis.presentation.dto.req.AnalysisUploadRequest;
import refresh.acci.domain.analysis.presentation.dto.res.AnalysisResultResponse;
import refresh.acci.domain.analysis.presentation.dto.res.AnalysisUploadResponse;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisRepository analysisRepository;

    @Transactional
    public AnalysisUploadResponse anaylze(AnalysisUploadRequest request) {

    }

    @Transactional(readOnly = true)
    public String getLoadingVideo(LocalDate todayDate) {

    }

    @Transactional(readOnly = true)
    public AnalysisResultResponse getAnalysisResult(UUID analysisId) {

    }
}
