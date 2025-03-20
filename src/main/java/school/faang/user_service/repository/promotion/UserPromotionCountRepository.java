package school.faang.user_service.repository.promotion;

import jakarta.transaction.Transactional;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.promotion.user.UserPromotionCount;

public interface UserPromotionCountRepository extends JpaRepository<UserPromotionCount, Long> {
    @Query("SELECT u.count FROM UserPromotionCount u WHERE u.id = :id")
    Integer findCountByUserId(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE UserPromotionCount u SET u.count = u.count + :increment WHERE u.userId = :userId")
    void incrementCountByUserId(Long userId, Integer increment);
}
