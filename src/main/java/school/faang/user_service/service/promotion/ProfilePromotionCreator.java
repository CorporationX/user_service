package school.faang.user_service.service.promotion;

import jakarta.validation.constraints.NotNull;
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
import school.faang.user_service.service.promotion.interfaces.PromotionCreator;

@Component("profileCreator")
@RequiredArgsConstructor
public class ProfilePromotionCreator implements PromotionCreator {
    private final PromotionMapper promotionMapperClass;
    private final ProfilePromotionRepository userPromotionRepo;

    @Override
    public Product create(@NotNull(message = "PromotionDto cannot be null") PromotionDto promotionDto,
                          @NotNull(message = "User cannot be null") User user,
                          @NotNull(message = "Promotion Plan cannot be null") PromotionPlan plan) {
        checkIfProfileHasPromoted(user.getId());
        return promotionMapperClass.toProfilePromotion(plan, user);
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
