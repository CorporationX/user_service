package school.faang.user_service.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.user.UserSkillGuarantee;

public interface UserSkillGuaranteeRepository extends JpaRepository<UserSkillGuarantee, Long> {
}