package school.faang.user_service.repository.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.promotion.user.ProfilePromotion;

public interface ProfilePromotionRepository extends JpaRepository<ProfilePromotion, Long> {
    boolean existsByProfileIdAndActiveTrue(Long profileId);
}
