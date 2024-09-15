package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.UserSkillGuarantee;

@Repository
public interface UserSkillGuaranteeRepository extends CrudRepository<UserSkillGuarantee, Long> {
    boolean existsUserSkillGuaranteeByUserIdAndSkillId(long userId, long skillId);

    @Query(nativeQuery = true, value = """
            INSERT INTO user_skill_guarantee (guarantor_id, user_id, skill_id)
            VALUES (?1,?2,?3)
            RETURNING id
            """)
    void create(long guarantorId, long userId, long skillId);
}