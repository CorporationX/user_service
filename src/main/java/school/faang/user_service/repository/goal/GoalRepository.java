package school.faang.user_service.repository.goal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    @Query(nativeQuery = true, value = """
            SELECT g.* FROM goal g
            JOIN user_goal ug ON g.id = ug.goal_id
            WHERE ug.user_id = ?1
            """)
    Stream<Goal> findGoalsByUserId(long userId);

    @Query(nativeQuery = true, value = """
            INSERT INTO goal (title, description, parent_goal_id, status, created_at, updated_at, deadline)
            VALUES (?1, ?2, ?3, 0, NOW(), NOW(), ?4) returning *
            """)
    Goal create(String title, String description, Long parent, LocalDateTime deadline);

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

    @Query("""
            SELECT g.status FROM Goal g WHERE
            g.id = :goalId
            """)
    GoalStatus getGoalStatusById(Long goalId);

    @Query("""
            SELECT g FROM Goal g
            WHERE g.parent.id = :parentId AND
            (:title IS NULL OR g.title = :title) AND
            (:status IS NULL OR g.status = :status)
            """)
    Stream<Goal> findByParentIdAndFilter(long parentId, String title, GoalStatus status);

    @Query(nativeQuery = true, value = """
        SELECT g.* 
        FROM goal g 
        JOIN user_goal ug ON g.id = ug.goal_id 
        WHERE ug.user_id = ?1 
          AND (?2 IS NULL OR g.title = ?2) 
          AND (?3 IS NULL OR g.status = ?3)
        """)
    Stream<Goal> findByUserIdAndFilter(long userId, String title, Integer status);

    @Query("""
            DELETE FROM Goal g WHERE g.id = :id
            """)
    @Modifying
    void deleteById(long id);
}
