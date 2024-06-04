package school.faang.user_service.repository.premium;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.premium.Premium;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PremiumRepository extends CrudRepository<Premium, Long> {

    Optional<Premium> findByUserId(Long userId);

    boolean existsByUserId(long userId);

    List<Premium> findAllByEndDateBefore(LocalDateTime endDate);
}
