package school.faang.service.user.repository.premium;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.service.user.entity.premium.Premium;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PremiumRepository extends CrudRepository<Premium, Long> {

    boolean existsByUserId(long userId);

    List<Premium> findAllByEndDateBefore(LocalDateTime endDate);
}
