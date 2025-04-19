package school.faang.user_service.config.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.service.UserService;

@Configuration
@Slf4j
public class RedisConfig {

    @Bean
    public RedisMessageListenerContainer listenerContainer(
            RedisConnectionFactory factory,
            MessageListenerAdapter adapter
    ) {
        log.info("Creating RedisMessageListenerContainer for topic 'user_ban'");
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(adapter, new ChannelTopic("user_ban"));
        return container;
    }

    @Bean
    public MessageListenerAdapter messageListener(UserService userService) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(userService, "banUser");
        adapter.setSerializer(new StringRedisSerializer());
        return adapter;
    }
}
