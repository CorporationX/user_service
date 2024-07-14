package school.faang.service.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.service.user.entity.UserSkillGuarantee;

import java.util.List;

@Repository
public interface UserSkillGuaranteeRepository extends JpaRepository<UserSkillGuarantee, Long> {

    @Query(nativeQuery = true, value = "INSERT INTO user_skill_guarantee (user_id, skill_id, guarantor_id) VALUES (?1, ?2, ?3) returning id")
    Long create(long userId, long skillId, long guarantorId);

    @Query(nativeQuery = true, value = """
            SELECT usg.* FROM user_skill_guarantee usg
            JOIN users u ON u.id = usg.user_id
            WHERE usg.user_id = ?1
            """)
    List<UserSkillGuarantee> findAllByUserId(long userId);
}