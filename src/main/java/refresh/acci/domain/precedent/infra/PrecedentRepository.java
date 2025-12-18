package refresh.acci.domain.precedent.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import refresh.acci.domain.precedent.model.Precedent;

public interface PrecedentRepository extends JpaRepository<Precedent, Long> {
}
