package school.faang.user_service.repository.role;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.Role;
import school.faang.user_service.model.user.RoleThesaurus;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleThesaurus name);
}
