package school.faang.user_service.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import school.faang.user_service.model.entity.Promotion;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends CrudRepository<Promotion, Long> {

    boolean existsByUserId(long userId);

    @Query(nativeQuery = true, value = "SELECT nextval('promotion_payment_number_sequence')")
    Long getPromotionPaymentNumber();

    @Modifying
    @Transactional
    @Query("UPDATE Promotion p SET p.remainingShows = p.remainingShows - 1" +
            " WHERE p.id IN :promotionIds AND p.promotionTarget = :promotionTarget")
    void decreaseRemainingShows(@Param("promotionIds") List<Long> promotionIds,
                                @Param("promotionTarget") String promotionTarget);

    @Query("SELECT p FROM Promotion p WHERE p.remainingShows = 0 AND p.promotionTarget = :promotionTarget")
    List<Promotion> findAllExpiredPromotions(@Param("promotionTarget") String promotionTarget);

    Optional<Promotion> findByUserId(long userId);
}
