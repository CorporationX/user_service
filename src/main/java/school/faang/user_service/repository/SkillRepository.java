package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.Skill;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    boolean existsByTitle(String title);

    @Query(nativeQuery = true, value = "SELECT COUNT(id) FROM skill WHERE id IN (?1)")
    int countExisting(List<Long> ids);

    @Query(nativeQuery = true, value = """
            SELECT s.* FROM skill s
            JOIN user_skill us ON us.skill_id = s.id
            WHERE us.user_id = ?1
            """)
    List<Skill> findAllByUserId(long userId);

    @Query(nativeQuery = true, value = """
            SELECT s.* FROM skill s
            JOIN skill_offer so ON so.skill_id = s.id
            JOIN recommendation r ON r.id = so.recommendation_id
            WHERE r.receiver_id = :userId
            """)
    List<Skill> findSkillsOfferedToUser(long userId);

    @Query(nativeQuery = true, value = """
            SELECT s.* FROM skill s
            JOIN user_skill us ON s.id = us.skill_id AND us.skill_id = :skillId AND us.user_id = :userId
            """)
    Optional<Skill> findUserSkill(long skillId, long userId);

    @Query(nativeQuery = true, value = "INSERT INTO user_skill (skill_id, user_id) VALUES (:skillId, :userId)")
    @Modifying
    void assignSkillToUser(long skillId, long userId);

    @Query(nativeQuery = true, value = """
        DELETE FROM user_skill
        WHERE skill_id = :skillId AND user_id = :userId
        """)
    @Modifying
    void removeSkillFromUserId(long skillId, long userId);

    @Query(nativeQuery = true, value = """
            SELECT s.* FROM skill s
            WHERE s.id IN (SELECT gs.skill_id FROM goal_skill gs
            WHERE gs.goal_id = ?1)
            """)
    List<Skill> findSkillsByGoalId(long goalId);

    @Query(nativeQuery = true, value = """
            INSERT INTO goal_skill (goal_id, skill_id, created_at, updated_at)
            VALUES (?2, ?1, NOW(), NOW())
            """)
    @Modifying
    void addSkillToGoalId(long skillId, long goalId);

    @Query(nativeQuery = true, value = """
        DELETE FROM goal_skill
        WHERE skill_id = ?1 AND goal_id = ?2
        """)
    @Modifying
    void removeSkillFromGoalId(long skillId, long goalId);

}