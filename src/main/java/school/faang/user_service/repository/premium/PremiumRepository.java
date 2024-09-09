package school.faang.user_service.repository.premium;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.premium.Premium;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Repository
public interface PremiumRepository extends CrudRepository<Premium, Long> {

    boolean existsByUserId(long userId);

    List<Premium> findAllByEndDateBefore(LocalDateTime endDate);

    @Query(nativeQuery = true, value = """
            SELECT up.* FROM user_premium up
            """)
    Stream<Premium> findPremiumUsers();
}
