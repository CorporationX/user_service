package school.faang.user_service.config.kafka;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KafkaTopics {
    public static final String PAYMENT_PROMOTION_TOPIC = "payment-success-topic";
    public static final String USER_BAN_TOPIC = "user-ban-topic";
}
