package school.faang.user_service.publisher;


import school.faang.user_service.event.ProfileViewEventDto;

public interface ProfileViewEventPublisher {
    void publish(ProfileViewEventDto message);
}
