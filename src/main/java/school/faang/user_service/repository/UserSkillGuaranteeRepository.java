package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.UserSkillGuarantee;

@Repository
public interface UserSkillGuaranteeRepository extends CrudRepository<UserSkillGuarantee, Long> {

    @Query(nativeQuery = true, value = """
            INSERT INTO user_skill_guarantee(user_id, skill_id, guaranor_id)
            VALUES (?1, ?2, ?3) returning id
            """)
    Long create(long userId, long skillId, long guarantorId);

    @Query(nativeQuery = true, value = """
        SELECT EXISTS(
            SELECT 1 FROM user_skill_guarantee
            WHERE user_id = ?1 AND skill_id = ?2 AND guarantor_id = ?3
        )
        """)
    boolean isGuaranteeExists(long userId, long skillId, long guarantorId);
}