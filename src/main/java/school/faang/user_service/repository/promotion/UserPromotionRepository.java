package school.faang.user_service.repository.promotion;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.promotion.UserPromotion;

import java.util.Optional;

@Repository
public interface UserPromotionRepository extends CrudRepository<UserPromotion, Long> {

    @Modifying
    @Query(nativeQuery = true, value = """
            UPDATE user_promotion
            SET number_of_views = number_of_views - 1
            WHERE id = :id
            AND number_of_views > 0
            """)
    void decrementPromotionViews(@Param("id") long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT up FROM UserPromotion up WHERE up.id = :id")
    Optional<UserPromotion> findByIdWithLock(@Param("id") long id);
}
