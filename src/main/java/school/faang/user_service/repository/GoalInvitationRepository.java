package school.faang.user_service.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.enums.RequestStatus;
import school.faang.user_service.model.entity.GoalInvitation;

@Repository
public interface GoalInvitationRepository extends JpaRepository<GoalInvitation, Long> {
    @Modifying
    @Transactional
    @Query("update GoalInvitation g set g.status = ?1 where g.id = ?2")
    void updateStatusById(RequestStatus status, long id);
}