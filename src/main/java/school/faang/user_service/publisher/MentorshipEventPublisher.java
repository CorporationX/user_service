package school.faang.user_service.publisher;

import school.faang.user_service.event.MentorshipEvent;

public class MentorshipEventPublisher implements MessagePublisher<MentorshipEvent> {

    @Override
    public void publish(MentorshipEvent event) {

    }
}
