package refresh.acci.domain.analysis.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import refresh.acci.domain.analysis.model.Analysis;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
}
