package school.faang.user_service.messaging.mentorshipEventPublisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorshipRequest.MentorshipEventDto;
import school.faang.user_service.messaging.EventPublisher;
import school.faang.user_service.util.Mapper;

@Component
@Slf4j
public class MentorshipEventPublisher implements EventPublisher<MentorshipEventDto> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ChannelTopic mentorshipEventTopic;
    @Autowired
    private Mapper mapper;

    public MentorshipEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic mentorshipEventTopic) {
        this.redisTemplate = redisTemplate;
        this.mentorshipEventTopic = mentorshipEventTopic;
    }

    @Override
    public void publish(MentorshipEventDto mentorshipEventDto) {
        mapper.toJson(mentorshipEventDto)
                .ifPresent(json -> redisTemplate.convertAndSend(mentorshipEventTopic.getTopic(), json));
        log.info("Mentorship was sent into: {}", mentorshipEventTopic.getTopic());
    }
}
