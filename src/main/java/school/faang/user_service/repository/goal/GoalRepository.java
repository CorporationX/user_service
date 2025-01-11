package school.faang.user_service.repository.goal;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.dto.entity.User;
import school.faang.user_service.dto.entity.goal.Goal;

import java.util.List;
import java.util.stream.Stream;

public interface GoalRepository extends JpaRepository<Goal, Long>, JpaSpecificationExecutor<Goal> {

    @EntityGraph(value = "Goal.graph", type = EntityGraph.EntityGraphType.LOAD)
    List<Goal> findAll(Specification<Goal> spec);

    @Query(nativeQuery = true, value = """
            SELECT g.* FROM goal g
            JOIN user_goal ug ON g.id = ug.goal_id
            WHERE ug.user_id = ?1
            """)
    Stream<Goal> findGoalsByUserId(long userId);

    @Query(nativeQuery = true, value = """
            INSERT INTO goal (title, description, parent_goal_id, status, created_at, updated_at)
            VALUES (?1, ?2, ?3, 0, NOW(), NOW()) returning *
            """)
    Goal create(String title, String description, Long parent);

    @Query(nativeQuery = true, value = """
            INSERT INTO goal (title, description, parent_goal_id, status, created_at, updated_at, mentor_id)
            VALUES (?1, ?2, ?3, 0, NOW(), NOW(), ?4) returning *
            """)
    Goal createGoalWithMentor(String title, String description, Long parent, Long mentorId);

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

    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO user_goal (user_id, goal_id, created_at)
            VALUES (:userId, :goalId, NOW())
            """)
    void assignGoalToUser(long userId, long goalId);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM goal_skill WHERE goal_id = :goalId
            """)
    void removeSkillsFromGoal(long goalId);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM user_goal WHERE goal_id = :goalId
            """)
    void removeUsersFromGoal(long goalId);

    List<Goal> findAllByParentId(Long id);

}
