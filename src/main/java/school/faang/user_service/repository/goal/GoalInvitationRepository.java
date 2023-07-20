package school.faang.user_service.repository.goal;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.goal.GoalInvitation;

@Repository
public interface GoalInvitationRepository extends CrudRepository<GoalInvitation, Long> {
    @Query(nativeQuery = true, value = """
            INSERT INTO goal_invitation (goal_id, inviter_id, invited_id, status, created_at, updated_at)
            VALUES (?1, ?2, ?3, 0, NOW(), NOW())
            """)
    GoalInvitation create(long goal_id, long inviter_id, long invited_id);
}