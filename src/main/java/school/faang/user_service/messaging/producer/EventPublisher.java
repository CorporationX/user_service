package school.faang.user_service.messaging.producer;

/**
 * Универсальный интерфейс для публикации событий.
 *
 * <p>
 * Реализации этого интерфейса должны определять механизм доставки события
 * (например, публикация в Kafka, Redis Pub/Sub, RabbitMQ или внутренняя обработка).
 * </p>
 *
 * @param <E> тип публикуемого события
 * @author Myrza
 * @since 19.08.2025
 */
public interface EventPublisher<E> {
    void publish(E event);
}
