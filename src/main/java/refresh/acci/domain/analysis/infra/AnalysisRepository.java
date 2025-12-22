package refresh.acci.domain.analysis.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import refresh.acci.domain.analysis.model.Analysis;

import java.util.UUID;

public interface AnalysisRepository extends JpaRepository<Analysis, UUID> {
}
