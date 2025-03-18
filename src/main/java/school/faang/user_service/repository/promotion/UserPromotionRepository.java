package school.faang.user_service.repository.promotion;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.promotion.user.UserPromotion;

import java.time.LocalDateTime;

public interface UserPromotionRepository extends JpaRepository<UserPromotion, Long> {
    @Query("""
            SELECT u
            FROM UserPromotion u
            WHERE u.user.id = :userId
                AND u.startDate = :startDate
                AND u.endDate = :endDate
                AND u.percentage = :percentage
                AND u.feedRank = :feedRank
            """)
    UserPromotion findSamePromotion(Long userId, LocalDateTime startDate, LocalDateTime endDate,
                                    Integer percentage, Integer feedRank);

    @Query("""
            SELECT u
            FROM UserPromotion u
            WHERE u.user.id = :userId
                AND u.startDate = :startDate
                AND u.endDate = :endDate
            """)
    UserPromotion findPromotionByUserIdStartDateEndDate(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    @Modifying
    @Transactional
    @Query("""
            UPDATE UserPromotion u
            SET u.percentage = :newPercentage
            WHERE u.user.id = :userId
                AND u.startDate = :startDate
                AND u.endDate = :endDate
                AND u.feedRank = :feedRank
            """)
    void updateUserPromotionPercentage(Long userId, LocalDateTime startDate, LocalDateTime endDate,
                                       Integer newPercentage, Integer feedRank);

    @Query("""
            SELECT u.percentage
            FROM UserPromotion u
            WHERE u.user.id = :userId
                AND u.startDate = :startDate
                AND u.endDate = :endDate
                AND u.feedRank = :feedRank
            """)
    Integer getUserPercentage(Long userId, LocalDateTime startDate, LocalDateTime endDate, Integer feedRank);

    @Query("""
            SELECT u.feedRank
            FROM UserPromotion u
            WHERE u.user.id = :userId
                AND u.startDate = :startDate
                AND u.endDate = :endDate
                AND u.percentage = :percentage
            """)
    Integer getUserFeedRank(Long userId, LocalDateTime startDate, LocalDateTime endDate, Integer percentage);

    @Modifying
    @Transactional
    @Query("""
            UPDATE UserPromotion u
            SET u.feedRank = :newFeedRank
            WHERE u.user.id = :userId
                AND u.startDate = :startDate
                AND u.endDate = :endDate
                AND u.percentage = :percentage
            """)
    void updateUserFeedRank(Long userId, LocalDateTime startDate, LocalDateTime endDate,
                            Integer percentage, Integer newFeedRank);
}
