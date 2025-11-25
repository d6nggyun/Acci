package refresh.acci.domain.precedent.infra;

import org.springframework.data.repository.CrudRepository;
import refresh.acci.domain.precedent.model.Precedent;

public interface PrecedentRepository extends CrudRepository<Precedent, Long> {
}
