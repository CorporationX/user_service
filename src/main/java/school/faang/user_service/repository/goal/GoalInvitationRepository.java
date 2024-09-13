package school.faang.user_service.repository.goal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;

@Repository
public interface GoalInvitationRepository extends JpaRepository<GoalInvitation, Long> {
    @Query(nativeQuery = true, value = """
        SELECT *
        FROM goal_invitation
        WHERE (:inviterId IS NULL OR inviter_id = :inviterId)
            AND (:invitedId IS NULL OR invited_id = :invitedId)
            AND (:inviterNamePattern IS NULL OR username LIKE %:inviterNamePattern%)
            AND (:invitedNamePattern IS NULL OR username LIKE %:invitedNamePattern%)
            AND (:requestStatus IS NULL OR request_status = :requestStatus);
    """)
    List<GoalInvitation> getAllFiltered(
            long inviterId,
            long invitedId,
            String inviterNamePattern,
            String invitedNamePattern,
            int requestStatus
    );
}