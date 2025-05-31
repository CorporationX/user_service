package school.faang.user_service.service.promotion;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.dto.promotion.PromotionType;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.Product;
import school.faang.user_service.entity.promotion.PromotionPlan;
import school.faang.user_service.entity.promotion.enums.Plan;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.repository.promotion.ProductRepository;
import school.faang.user_service.repository.promotion.PromotionPlanRepository;
import school.faang.user_service.repository.user.UserRepositoryAdapter;
import school.faang.user_service.service.promotion.interfaces.PromotionActionsService;
import school.faang.user_service.service.promotion.interfaces.PromotionService;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionPlanRepository promotionPlanRepo;
    private final ProductRepository productRepo;
    private final UserRepositoryAdapter userRepoAdapter;
    private final Map<String, PromotionActionsService> promotionCreators;

    @Override
    @Transactional
    public void addPromotion(@NotNull(message = "promotionDto cannot be null") PromotionDto promotionDto) {
        PromotionActionsService promotionActionsService = getCreator(promotionDto.getPromotionType());
        PromotionPlan plan = findPromotionPlan(promotionDto.getPlan());
        User client = userRepoAdapter.findById(promotionDto.getClientId());
        Product promotionProduct = promotionActionsService.create(promotionDto, client, plan);
        Product savedProduct = productRepo.save(promotionProduct);
        promotionActionsService.getItemPaid(savedProduct, client);
        log.info("Added new promotion successfully with id = {}", promotionProduct.getId());
    }

    private PromotionActionsService getCreator(@NotNull(message = "Type of promotion cannot be null")
                                               PromotionType promotionType) {
        PromotionActionsService promotionActionsService = promotionCreators.get(promotionType.getValue());
        if (promotionActionsService == null) {
            throw new IllegalArgumentException("Unsupported type: " + promotionType);
        }
        return promotionActionsService;
    }


    private PromotionPlan findPromotionPlan(@NotNull(message = "Promotion plan cannot be null") Plan plan) {
        return promotionPlanRepo.findByPlan(plan).orElseThrow(() ->
                new NotFoundException(String.format("No plan details were found for the plan: %s", plan)));
    }
}
