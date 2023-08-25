package school.faang.user_service.repository.premium;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.premium.Premium;

@Repository
public interface PremiumRepository extends JpaRepository<Premium, Long> {

    boolean existsByUserId(long userId);
}
