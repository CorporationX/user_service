package school.faang.user_service.repository.premium;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.premium.Premium;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PremiumRepository extends JpaRepository<Premium, Long> {

    boolean existsByUserId(long userId);

    List<Premium> findAllByEndDateBefore(LocalDateTime endDate);

    @Query(nativeQuery = true, value = "SELECT nextval('premium_payment_number_sequence')")
    Long getPremiumPaymentNumber();
}
