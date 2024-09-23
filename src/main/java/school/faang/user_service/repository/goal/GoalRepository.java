package school.faang.user_service.repository.goal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    @Query(value = """
    SELECT g.*
    FROM goal g
    JOIN user_goal ug ON g.id = ug.goal_id
    WHERE ug.user_id = :userId
    """, nativeQuery = true)
    List<Goal> findGoalsByUserId(Long userId);

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
        SELECT * FROM subtasks WHERE id != :goalId AND title LIKE %:titleFilter%
        """)
    List<Goal> findByParent(@Param("goalId") long goalId, @Param("titleFilter") String titleFilter);
    @Query(nativeQuery = true, value = """
            SELECT u.* FROM users u
            JOIN user_goal ug ON u.id = ug.user_id
            WHERE ug.goal_id = :goalId
            """)
    List<User> findUsersByGoalId(long goalId);
}
