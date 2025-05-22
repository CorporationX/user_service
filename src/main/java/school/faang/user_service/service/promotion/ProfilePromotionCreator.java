package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.dto.promotion.PromotionType;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.Product;
import school.faang.user_service.entity.promotion.PromotionPlan;
import school.faang.user_service.exception.ConflictPlanException;
import school.faang.user_service.mapper.promotion.PromotionMapper;
import school.faang.user_service.repository.promotion.ProfilePromotionRepository;

import java.util.Objects;

@Component("PROFILE")
@RequiredArgsConstructor
public class ProfilePromotionCreator implements PromotionCreator {
    private final PromotionMapper promotionMapper;
    private final ProfilePromotionRepository userPromotionRepo;

    @Override
    public Product create(PromotionDto promotionDto, User user, PromotionPlan plan) {
        Objects.requireNonNull(promotionDto, "promotionDto cannot be null");
        Objects.requireNonNull(user, "user cannot be null");
        Objects.requireNonNull(plan, "plan cannot be null");
        checkIfProfileHasPromoted(user.getId());
        return promotionMapper.toProfilePromotionProduct(plan, user);
    }

    @Override
    public PromotionType getType() {
        return PromotionType.PROFILE;
    }

    private void checkIfProfileHasPromoted(Long profileId) {
        if (userPromotionRepo.existsByProfileIdAndActiveTrue(profileId)) {
            throw new ConflictPlanException(String.format("Profile with this id-%d already promoted", profileId));
        }
    }
}
