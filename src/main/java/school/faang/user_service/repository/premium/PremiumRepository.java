package school.faang.user_service.repository.premium;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.dto.entity.premium.Premium;

import java.time.LocalDateTime;
import java.util.List;

public interface PremiumRepository extends CrudRepository<Premium, Long> {

    @Query(nativeQuery = true,
            value = "SELECT EXISTS(SELECT 1 FROM USER_PREMIUM WHERE USER_ID = :userId and end_date > NOW())")
    boolean existsByUserId(long userId);

    List<Premium> findAllByEndDateBefore(LocalDateTime endDate);
}
