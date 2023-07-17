package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.UserSkillGuarantee;

@Repository
public interface UserSkillGuaranteeRepository extends JpaRepository<UserSkillGuarantee, Long> {

    boolean existsByUserIdAndSkillIdAndGuarantorId(long userId, long skillId, long guarantorId);
}