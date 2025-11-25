package refresh.acci.domain.law.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import refresh.acci.domain.law.model.Law;

public interface LawRepository extends JpaRepository<Law, Long> {
}
