package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.UserSkillGuarantee;

import java.util.Optional;

@Repository
public interface UserSkillGuaranteeRepository extends JpaRepository<UserSkillGuarantee, Long> {
    Optional<UserSkillGuarantee> findByReceiverIdAndSkillIdAndAuthorId(long receiverId, long skillId, long authorId);
}