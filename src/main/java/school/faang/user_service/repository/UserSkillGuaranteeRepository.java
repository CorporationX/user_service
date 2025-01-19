package school.faang.user_service.repository;

import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.dto.entity.UserSkillGuarantee;

public interface UserSkillGuaranteeRepository extends CrudRepository<UserSkillGuarantee, Long> {
}