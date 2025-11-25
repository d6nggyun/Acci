package refresh.acci.domain.user.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import refresh.acci.domain.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
