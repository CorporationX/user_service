package school.faang.user_service.repository.skill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.user.Skill;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    boolean existsByTitle(String title);
    @Query("SELECT  s FROM Skill s JOIN s.users u WHERE u.id = :userId")
    List<Skill> findAllByUserId(@Param("userId") Long userId);
    @Modifying
    @Query(value ="INSERT INTO user_skills (user_id, skill_id) VALUES (:userId, :skillId)", nativeQuery = true)
    void assignSkillToUser(@Param("skillId")long skillId, @Param("userId")long userId);
    @Query(value = "SELECT EXISTS(SELECT 1 FROM user_skills WHERE user_id = :userId AND skill_id = :skillId)", nativeQuery = true)
    boolean existsByUserIdAndSkillId(@Param("userId") long userId,@Param("skillId") long skillId);
}
