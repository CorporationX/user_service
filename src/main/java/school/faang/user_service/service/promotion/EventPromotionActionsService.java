package school.faang.user_service.service.promotion;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.dto.promotion.PromotionType;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Product;
import school.faang.user_service.entity.promotion.PromotionPlan;
import school.faang.user_service.entity.promotion.event.EventPromotion;
import school.faang.user_service.entity.transaction.Transaction;
import school.faang.user_service.entity.transaction.TransactionStatus;
import school.faang.user_service.exception.ConflictPlanException;
import school.faang.user_service.exception.RelatednessNotConfirmedException;
import school.faang.user_service.mapper.promotion.PromotionMapper;
import school.faang.user_service.repository.event.EventRepositoryAdapter;
import school.faang.user_service.repository.promotion.EventPromotionRepository;
import school.faang.user_service.service.promotion.interfaces.PromotionActionsService;
import school.faang.user_service.service.transaction.TransactionService;

import java.util.List;
import java.util.Objects;

@Component("eventCreator")
@RequiredArgsConstructor
public class EventPromotionActionsService implements PromotionActionsService {
    private final EventRepositoryAdapter eventRepoAdapter;
    private final PromotionMapper promotionMapper;
    private final EventPromotionRepository eventPromotionRepo;
    private final TransactionService transactionService;

    @Override
    public Product create(@NotNull(message = "PromotionDto cannot be null") PromotionDto promotionDto,
                          @NotNull(message = "Сlient cannot be null") User client,
                          @NotNull(message = "Promotion Plan cannot be null") PromotionPlan plan) {
        Long eventId = promotionDto.getEventId();
        Objects.requireNonNull(eventId, "eventId cannot be null");
        checkIfEventPromoted(eventId);
        Event event = eventRepoAdapter.findById(eventId);
        checkIfCreator(event, client);
        return promotionMapper.toEventPromotion(plan, client, event);
    }

    public void getItemPaid(Product product, User client) {
        EventPromotion eventPromotion = (EventPromotion) product;
        Transaction transaction = transactionService.buyItem(client.getId(), eventPromotion);
        if (transaction.getTransactionStatus() == TransactionStatus.SETTLED) {
            eventPromotion.setActive(true);
        }

        eventPromotion.setTransactions(List.of(transaction));
        eventPromotionRepo.save(eventPromotion);
    }

    private void checkIfCreator(Event event, User client) {
        if (!Objects.equals(event.getOwner().getId(), client.getId())) {
            throw new RelatednessNotConfirmedException("Only owner of the event can promote it");
        }
    }

    @Override
    public PromotionType getType() {
        return PromotionType.EVENT;
    }

    private void checkIfEventPromoted(Long eventId) {
        if (eventPromotionRepo.existsByEventIdAndActiveTrue(eventId)) {
            throw new ConflictPlanException(String.format("Event with this id-%d already promoted", eventId));
        }
    }
}
