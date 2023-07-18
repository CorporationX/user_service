package school.faang.user_service.repository.goal;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.service.goal.GoalService;

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
            SELECT * FROM goal g
            WHERE g.goal_id = ?1
            """)
    Goal findGoal(long goalId);

    @Query(nativeQuery = true, value = """
            INSERT INTO goal (title, description, parent_goal_id, status, created_at, updated_at)
            VALUES (?1, ?2, ?3, 0, NOW(), NOW()) returning goal
            """)
    Goal create(String title, String description, Long parent);

    @Modifying
    @Query("UPDATE goal g SET g.title = ?1, g.description = ?2, g.status = ?2 WHERE g.id = ?3")
    void update(String title, String description, GoalStatus status, Long id);

    @Modifying
    @Query("UPDATE goal_skill gs SET gs.skill_id = ?1 WHERE gs.goal_id = ?2")
    void updateGoalSkill(Long sid, Long gid);

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
            JOIN subtasks st ON st.id = g.parent_id
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
}