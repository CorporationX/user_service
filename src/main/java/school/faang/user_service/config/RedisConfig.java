package school.faang.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.listener.UserBanEventListener;

@Configuration
public class RedisConfig {

    @Value("${redis.channels.project-follower}")
    private String projectFollowerEventChannel;

    @Value("${redis.channels.premium-bought}")
    private String premiumBoughtEventChannel;

    @Value("${redis.channels.user-ban}")
    private String userBanEventChannel;

    public interface MessagePublisher<T> {
        void publish(T redisEvent);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean(name = "projectFollowerTopic")
    public ChannelTopic projectFollowerChannelTopic() {
        return new ChannelTopic(projectFollowerEventChannel);
    }

    @Bean(name = "premiumBoughtTopic")
    public ChannelTopic premiumBoughtChannelTopic() {
        return new ChannelTopic(premiumBoughtEventChannel);
    }

    @Bean
    public ChannelTopic userBanChannelTopic() {
        return new ChannelTopic(userBanEventChannel);
    }

    @Bean
    public MessageListenerAdapter userBanEventListenerAdapter(UserBanEventListener userBanEventListener) {
        return new MessageListenerAdapter(userBanEventListener, "onMessage");
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(
            LettuceConnectionFactory lettuceConnectionFactory,
            UserBanEventListener userBanEventListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(lettuceConnectionFactory);
        container.addMessageListener(userBanEventListenerAdapter(userBanEventListener), userBanChannelTopic());
        return container;
    }
}