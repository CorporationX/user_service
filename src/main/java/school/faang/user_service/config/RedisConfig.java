package school.faang.user_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import school.faang.user_service.subscriber.UsersBanListener;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final UsersBanListener usersBanListener;
    @Value("${topic.user_ban}")
    private String userBanTopic;
    @Value("${spring.data.redis.channels.profile_view_channel.name}")
    private String profileViewChannel;
    @Bean
    public MessageListenerAdapter userBanMessageListenerAdapter(){
        return new MessageListenerAdapter(usersBanListener);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(userBanMessageListenerAdapter(), userBanTopic());
        return container;
    }

    @Bean
    public ChannelTopic profileViewTopic() {
        return new ChannelTopic(profileViewChannel);
    }

    @Bean
    public ChannelTopic userBanTopic(){
        return new ChannelTopic(userBanTopic);
    }
}
