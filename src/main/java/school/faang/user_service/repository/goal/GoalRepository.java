package school.faang.user_service.repository.goal;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface GoalRepository extends CrudRepository<Goal, Long> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM goal g
            JOIN user_goal ug ON g.id = ug.goal_id
            WHERE ug.user_id = ?1
            """)
    Stream<Goal> findGoalsByUserId(long userId);

    @Query(nativeQuery = true, value = """
            INSERT INTO goal (title, description, parent_goal_id, status, created_at, updated_at)
            VALUES (?1, ?2, ?3, 0, NOW(), NOW()) returning goal
            """)
    Goal create(String title, String description, Long parent);

    @Query(nativeQuery = true, value = """
            SELECT COUNT(ug.goal_id) FROM user_goal ug
            JOIN goal g ON g.id = ug.goal_id
            WHERE ug.user_id = :userId AND g.status = 0
            """)
    int countActiveGoalsPerUser(long userId);

    @Query(nativeQuery = true, value = """
            WITH RECURSIVE subtasks AS (
            SELECT * FROM goal WHERE id = :goalId
            UNION
            SELECT g.* FROM goal g
            JOIN subtasks st ON st.id = g.parent_goal_id
            )
            SELECT * FROM subtasks WHERE id != :goalId
            """)
    Stream<Goal> findByParent(long goalId);

    @Query(nativeQuery = true, value = """
            SELECT u.* FROM users u
            JOIN user_goal ug ON u.id = ug.user_id
            WHERE ug.goal_id = :goalId
            """)
    List<User> findUsersByGoalId(long goalId);

    @Query(nativeQuery = true, value = """
            INSERT INTO goal_skill (goal_id, skill_id, created_at, updated_at)
            VALUES (?1, ?2, NOW(), NOW())
            """)
    void addSkillToGoal(long skillId, long goalId);

    @Query(nativeQuery = true, value = """
            DELETE FROM goal_skill
            WHERE goal_skill.goal_id = ?1
            """)
    void removeSkillsFromGoal(long goalId);

    @Query(nativeQuery = true, value = """
            DELETE FROM goal
            WHERE goal.id = ?1
            """)
    void deleteGoalById(long goalId);

    @Query(nativeQuery = true, value = """
            SELECT * FROM goal
            WHERE goal.id = ?1
            """)
    Goal findGoalById(long goalId);

    @Query(nativeQuery = true, value = """
                SELECT id FROM  goal
                WHERE goal.title = ?1
            """)
    Long findGoalIdByTitle(String title);

    @Query(nativeQuery = true, value = """
        UPDATE goal 
        SET status = 1
        WHERE goal.id = ?1
    """)
    void updateGoalStatusById(Long goalId);
}
