package school.faang.user_service.messaging.fundRaising;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.fundraising.FundRaisedEvent;
import school.faang.user_service.messaging.EventPublisher;
import school.faang.user_service.util.Mapper;

@Component
public class FundRaisedEventPublisher extends EventPublisher<FundRaisedEvent> {
    @Autowired
    public FundRaisedEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                    ChannelTopic fundRaisedChannel,
                                    ObjectMapper jsonMapper) {
        super(redisTemplate, fundRaisedChannel, jsonMapper);
    }
}
