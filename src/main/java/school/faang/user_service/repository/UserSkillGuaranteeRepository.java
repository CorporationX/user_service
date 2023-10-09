package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.UserSkillGuarantee;

@Repository
public interface UserSkillGuaranteeRepository extends JpaRepository<UserSkillGuarantee, Long> {

    @Query(nativeQuery = true, value = """
            INSERT INTO user_skill_guarantee (user_id, skill_id, guarantor_id)
            VALUES (:userId, :skillId, :guarantorId)"""
    )
    Long create(long userId, long skillId, long guarantorId);
}