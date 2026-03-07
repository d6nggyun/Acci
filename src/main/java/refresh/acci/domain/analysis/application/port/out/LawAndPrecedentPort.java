package refresh.acci.domain.analysis.application.port.out;

import refresh.acci.domain.vectorDb.presentation.dto.res.PrecedentCasesResponse;
import refresh.acci.domain.vectorDb.presentation.dto.res.RagSummaryResponse;
import refresh.acci.domain.vectorDb.presentation.dto.res.RelatedLawsResponse;

import java.util.List;
import java.util.UUID;

public interface LawAndPrecedentPort {
    boolean saveRelatedLawsAndPrecedents(UUID analysisId, RagSummaryResponse ragSummaryResponse);
    List<RelatedLawsResponse> getRelatedLawsByAnalysisId(UUID analysisId);
    List<PrecedentCasesResponse> getPrecedentCasesByAnalysisId(UUID analysisId);
}
