package school.faang.service.user.repository.goal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.service.user.entity.goal.GoalInvitation;

@Repository
public interface GoalInvitationRepository extends JpaRepository<GoalInvitation, Long> {
}