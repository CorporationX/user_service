package school.faang.user_service.publisher;


public interface MessagePublisher<T> {
    public void publish(T event);
}
