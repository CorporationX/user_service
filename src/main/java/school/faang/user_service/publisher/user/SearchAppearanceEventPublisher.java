package school.faang.user_service.publisher.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.publisher.AbstractEventPublisher;
import school.faang.user_service.publisher.MessagePublisher;


@Component
public class SearchAppearanceEventPublisher extends AbstractEventPublisher implements MessagePublisher {
    @Autowired
    public SearchAppearanceEventPublisher(ObjectMapper objectMapper,
                                          RedisTemplate<String, Object> redisTemplate,
                                          ChannelTopic userProfileFilterViewTopic) {
        super(objectMapper, redisTemplate, userProfileFilterViewTopic);
    }

    @Override
    public void publish(Object object) {
        convertAndSend(object);
    }
}
