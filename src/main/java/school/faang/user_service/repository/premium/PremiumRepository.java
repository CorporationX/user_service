package school.faang.user_service.repository.premium;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.premium.Premium;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PremiumRepository extends JpaRepository<Premium, Long> {

    boolean existsByUserId(long userId);

    List<Premium> findAllByEndDateBefore(LocalDateTime endDate);

    @Modifying
    @Query(nativeQuery = true, value = """
        DELETE FROM user_premium up
        WHERE up.id IN :premiumIds
    """)
    void deleteByIdInBatch(@Param("premiumIds") List<Long> premiumIds);
}
