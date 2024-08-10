package school.faang.user_service.repository.premium;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.premium.Premium;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PremiumRepository extends CrudRepository<Premium, Long> {

    boolean existsByUserId(long userId);

    List<Premium> findAllByEndDateBefore(LocalDateTime endDate);

    void deleteByUserId(long userId);

    @Modifying
    @Query(nativeQuery = true, value = """
    delete from user_premium
    where id in (?1)
    """)
    int deleteByIds(List<Long> ids);
}
