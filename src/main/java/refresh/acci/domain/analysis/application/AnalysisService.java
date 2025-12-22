package refresh.acci.domain.analysis.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import refresh.acci.domain.analysis.infra.AnalysisRepository;
import refresh.acci.domain.analysis.presentation.dto.res.AnalysisResultResponse;
import refresh.acci.domain.analysis.presentation.dto.res.AnalysisUploadResponse;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    @Value("${ai.server.port}")
    private String aiServerPort;


    @Transactional
    public AnalysisUploadResponse anaylze(MultipartFile video) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("video", video.getResource())
                .filename(video.getOriginalFilename())
                .contentType(MediaType.parseMediaType(video.getContentType()));

        WebClient webClient = webClientBuilder.baseUrl(aiServerUrl + ":" + aiServerPort).build();

        return webClient.post()
                .uri("/analyze") // AI 서버의 분석 엔드포인트
                .contentType(MediaType.MULTIPART_FORM_DATA) // Content-Type 설정
                .body(BodyInserters.fromMultipartData(builder.build())) // 멀티파트 데이터를 실제 HTTP 요청 바디로 인코딩해서 넣음
                .retrieve() // 응답을 받아옴
                .bodyToMono(AnalysisUploadResponse.class) // 응답 바디를 AnalysisUploadResponse 객체로 변환
                .block(); // Mono를 동기 방식으로 기다려서 결과를 실제 객체로 꺼냄
    }

    @Transactional(readOnly = true)
    public String getLoadingVideo(LocalDate todayDate) {

    }

    @Transactional(readOnly = true)
    public AnalysisResultResponse getAnalysisResult(UUID analysisId) {

    }
}
