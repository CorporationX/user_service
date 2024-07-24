package school.faang.user_service.kafka.data;

/**
 * @author Evgenii Malkov
 */
public interface HeaderName {
    // id платежа
    String paymentId = "paymentId";

    // id обращения
    String requestId = "requestId";
}
