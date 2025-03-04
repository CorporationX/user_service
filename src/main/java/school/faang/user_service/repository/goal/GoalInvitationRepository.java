package school.faang.user_service.repository.goal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.goal.GoalInvitation;

public interface GoalInvitationRepository extends JpaRepository<GoalInvitation, Long> {
    @Query(nativeQuery = true, value = "select exists(select 1 from users where id = :inviter_id)")
    boolean existsInviterById(long userId);

    @Query(nativeQuery = true, value = "select exists(select 1 from users where id = :invited_id)")
    boolean existsInvitedById(long userId);
}