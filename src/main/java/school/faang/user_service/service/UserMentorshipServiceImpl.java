package school.faang.user_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.model.promotion.event.MentorshipStartEvent;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserMentorshipServiceImpl implements UserMentorshipService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Qualifier("mentorshipChannel")
    private final ChannelTopic channelTopic;
    private final ObjectMapper objectMapper;

    @Override
    public void startMentorship(Long mentorId, Long menteeId) {
        log.info("Starting mentorship: mentor={}, mentee={}", mentorId, menteeId);
        MentorshipStartEvent event = new MentorshipStartEvent(mentorId, menteeId);
        try {
            String payload = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channelTopic.getTopic(), payload);
            log.info("Published MentorshipStartEvent for mentor={} to topic {}", mentorId, channelTopic.getTopic());
        } catch (JsonProcessingException e) {
            log.error("Failed to publish MentorshipStartEvent: {}", event, e);
        }
    }
}
