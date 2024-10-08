package school.faang.user_service.redis;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisMessageConsumerMentorshipRequests implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(RedisMessageConsumerMentorshipRequests.class);

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());
        log.info("Received message from channel {}: {}", channel, body);
    }
}
