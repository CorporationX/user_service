package school.faang.user_service.repository.promotion;

import feign.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.promotion.Promotion;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends CrudRepository<Promotion, Long> {

    boolean existsByUserId(long userId);

    @Query(nativeQuery = true, value = "SELECT nextval('promotion_payment_number_sequence')")
    Long getPromotionPaymentNumber();

    @Modifying
    @Transactional
    @Query("UPDATE Promotion p SET p.showCount = p.showCount - 1 WHERE p.id IN :promotionIds")
    void decreaseShowCount(@Param("promotionIds") List<Long> promotionIds);

    @Query("SELECT p FROM Promotion p WHERE p.showCount = 0")
    List<Promotion> findAllExpiredPromotions();

    Optional<Promotion> findByUserId(long userId);
}
