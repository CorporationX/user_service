package school.faang.user_service.repository.goal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;

@Repository
public interface GoalInvitationRepository extends JpaRepository<GoalInvitation, Long> {

    List<GoalInvitation> findByInvited(long id);

    List<GoalInvitation> findByStatus(RequestStatus status);
}