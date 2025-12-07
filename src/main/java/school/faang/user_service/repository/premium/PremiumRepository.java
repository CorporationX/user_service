package school.faang.user_service.repository.premium;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.premium.Premium;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PremiumRepository extends JpaRepository<Premium, Long> {

    boolean existsByUserId(long userId);

    List<Premium> findAllByEndDateBefore(LocalDateTime endDate);

    Optional<Premium> findFirstByUser_Id(long userId);

    Optional<Premium> findFirstByUser_IdAndEndDateAfter(long userId, LocalDateTime endDate);

    Optional<Premium> findFirstByUserId(long userId);
}
