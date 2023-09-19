package school.faang.user_service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.messaging.events.ProjectFollowerEvent;

@Service
public class ProjectFollowerEventPublisher implements MessagePublisher<ProjectFollowerEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic folowerTopic;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProjectFollowerEventPublisher(RedisTemplate<String, Object> redisTemplate, @Qualifier("folowerTopic") ChannelTopic folowerTopic) {
        this.redisTemplate = redisTemplate;
        this.folowerTopic = folowerTopic;
    }

    @Override
    public void publish(ProjectFollowerEvent event) {
        String json;
        try {
            json = objectMapper.writeValueAsString(event);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(folowerTopic.getTopic(), json);
    }

}
