package school.faang.user_service.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import school.faang.user_service.service.user.redis.RedisMessageSubscriber;

@Configuration
public class RedisConfig {

    @Value("${redis.topic.user-ban}")
    private String userBanTopicName;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic(userBanTopicName);
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter(RedisMessageSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber);
    }

    @Bean
    public RedisMessageListenerContainer container(MessageListenerAdapter messageListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        container.addMessageListener(messageListenerAdapter, channelTopic());
        return container;
    }
}
