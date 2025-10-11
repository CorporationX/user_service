package school.faang.user_service.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.user.UserSkillGuarantee;

public interface UserSkillGuaranteeRepository extends JpaRepository<UserSkillGuarantee, Long> {

    @Query(value = """
            SELECT COUNT(usg) FROM UserSkillGuarantee usg
            WHERE usg.user.id = :userId 
            AND usg.skill.id = :skillId 
            AND usg.guarantor.id = :guarantorId
            """)
    long countByUserAndSkillAndGuarantor(long userId, long skillId, long guarantorId);
}