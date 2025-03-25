package school.faang.user_service.repository.goal;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;

public interface GoalInvitationRepository extends JpaRepository<GoalInvitation, Long> {

    boolean existsByInvitedAndGoal(User invited, Goal goal);

    @Query("SELECT gi FROM GoalInvitation gi WHERE gi.inviter.id = :inviterId "
            + "AND gi.invited.id = :invitedId AND gi.status = :status")
    List<GoalInvitation> findByInviterIdAndInvitedIdAndStatus(
            @Param("inviterId") Long inviterId,
            @Param("invitedId") Long invitedId,
            @Param("status") RequestStatus status
    );

    List<GoalInvitation> findByInvitedId(Long invitedId);
}