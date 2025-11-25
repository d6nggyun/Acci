package refresh.acci.domain.term.infra;

import org.springframework.data.repository.CrudRepository;
import refresh.acci.domain.term.model.Term;

public interface TermRepository extends CrudRepository<Term, Long> {
}
