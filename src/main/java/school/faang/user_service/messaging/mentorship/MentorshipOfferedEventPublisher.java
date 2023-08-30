package school.faang.user_service.messaging.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorshipRequest.MentorshipOfferRequestSentDto;
import school.faang.user_service.util.Mapper;

@Component
@Slf4j
@RequiredArgsConstructor
public class MentorshipOfferedEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic mentorshipEventTopic;
    private final Mapper mapper;

    public void publish(MentorshipOfferRequestSentDto dto) {
        mapper.toJson(dto)
                .ifPresent(json -> redisTemplate.convertAndSend(mentorshipEventTopic.getTopic(), json));
        log.info("MentorshipOffer was sent into: {}", mentorshipEventTopic.getTopic());
    }
}
