package school.faang.user_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.events.MentorshipOfferedEvent;
import school.faang.user_service.publishers.EventJsonConverter;
import school.faang.user_service.publishers.RedisPublisher;

@Component
@Slf4j
public class MentorshipOfferedEventService {
    private final RedisPublisher redisPublisher;
    private final EventJsonConverter<MentorshipOfferedEvent> jsonConverter;

    public MentorshipOfferedEventService(RedisPublisher redisPublisher,
                                         EventJsonConverter<MentorshipOfferedEvent> jsonConverter,
                                         @Qualifier("mentorshipOfferedChannel") ChannelTopic mentorshipOfferedChannel) {
        redisPublisher.setChannelTopic(mentorshipOfferedChannel);
        this.redisPublisher = redisPublisher;
        this.jsonConverter = jsonConverter;
    }

    public void publishEvent(MentorshipRequest request) {
        MentorshipOfferedEvent mentorshipOfferedEvent = new MentorshipOfferedEvent();
        mentorshipOfferedEvent.setAuthorId(request.getRequester().getId());
        mentorshipOfferedEvent.setMentorId(request.getReceiver().getId());
        mentorshipOfferedEvent.setRequestId(request.getId());
        String message = jsonConverter.toJson(mentorshipOfferedEvent);
        redisPublisher.publish(message);
    }
}
