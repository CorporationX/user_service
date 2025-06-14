package school.faang.user_service.service.promotion;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.dto.promotion.PromotionType;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.Product;
import school.faang.user_service.entity.promotion.PromotionPlan;
import school.faang.user_service.entity.promotion.user.ProfilePromotion;
import school.faang.user_service.entity.transaction.Transaction;
import school.faang.user_service.entity.transaction.TransactionStatus;
import school.faang.user_service.exception.ConflictPlanException;
import school.faang.user_service.mapper.promotion.PromotionMapper;
import school.faang.user_service.repository.promotion.ProfilePromotionRepository;
import school.faang.user_service.service.promotion.interfaces.PromotionActionsService;
import school.faang.user_service.service.transaction.TransactionService;

import java.util.ArrayList;
import java.util.List;

@Component("profileCreator")
@RequiredArgsConstructor
public class ProfilePromotionActionsService implements PromotionActionsService {
    private final PromotionMapper promotionMapper;
    private final ProfilePromotionRepository userPromotionRepo;
    private final TransactionService transactionService;

    @Override
    public Product create(@NotNull(message = "PromotionDto cannot be null") PromotionDto promotionDto,
                          @NotNull(message = "User cannot be null") User user,
                          @NotNull(message = "Promotion Plan cannot be null") PromotionPlan plan) {
        checkIfProfileHasPromoted(user.getId());
        return promotionMapper.toProfilePromotion(plan, user);
    }

    public void getItemPaid(Product product, User client) {
        ProfilePromotion userPromotion = (ProfilePromotion) product;
        Transaction transaction = transactionService.buyItem(client.getId(), userPromotion);
        if (transaction.getTransactionStatus() == TransactionStatus.SETTLED) {
            userPromotion.setActive(true);
        }
        List<Transaction> transactions = userPromotion.getTransactions();
        if (transactions == null) {
            transactions = new ArrayList<>();
        }

        transactions.add(transaction);
        userPromotion.setTransactions(transactions);
        userPromotionRepo.save(userPromotion);
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
