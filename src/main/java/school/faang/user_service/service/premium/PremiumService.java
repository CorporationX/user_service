package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.transaction.TransactionResultDto;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.entity.transaction.Transaction;
import school.faang.user_service.entity.transaction.TransactionStatus;
import school.faang.user_service.kafka.events.AnalyticsEventType;
import school.faang.user_service.kafka.events.PremiumBoughtEvent;
import school.faang.user_service.kafka.producer.KafkaDataSenderImpl;
import school.faang.user_service.kafka.producer.KafkaTopics;
import school.faang.user_service.mapper.TransactionMapper;
import school.faang.user_service.service.transaction.TransactionService;
import school.faang.user_service.service.utils.PremiumServiceUtils;

@Log4j2
@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumServiceUtils premiumServiceUtils;
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;
    private final UserContext userContext;
    private final KafkaDataSenderImpl kafkaDataSender;
    private final KafkaTopics kafkaTopics;

    public TransactionResultDto buyPremium(Long userId, Integer durationDays) {
        premiumServiceUtils.checkUserHasNoPremium(userId);
        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(durationDays);
        Transaction transaction = transactionService.buyItem(userId, premiumPeriod);
        if (transaction.getTransactionStatus()
                .equals(TransactionStatus.SETTLED)) {
            premiumServiceUtils.assignPremiumToUser(userId, durationDays);
        }

        PremiumBoughtEvent premiumBoughtEvent = new PremiumBoughtEvent();
        premiumBoughtEvent.setPaymentAmount(transaction.getAmount());
        premiumBoughtEvent.setSubscriptionDuration(durationDays);
        premiumBoughtEvent.setSentAt(transaction.getCreatedAt());
        premiumBoughtEvent.setReceiverId(userId);
        premiumBoughtEvent.setAuthorId(userContext.getUserId());
        premiumBoughtEvent.setEventTypeEnum(AnalyticsEventType.PREMIUM_BOUGHT);
        log.info("Sending PremiumBoughtEvent : {}", premiumBoughtEvent);
        kafkaDataSender.send(kafkaTopics.getPremiumBoughtTopic(), premiumBoughtEvent);
        return transactionMapper.toDto(transaction);
    }
}
