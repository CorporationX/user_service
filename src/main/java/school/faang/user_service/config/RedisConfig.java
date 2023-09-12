package school.faang.user_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.listeners.UserBannerListener;

@Configuration
@Slf4j
public class RedisConfig {
    @Value("${spring.data.redis.channels.user_banner_channel.name}")
    private String userBannerChannel;
    @Value("${spring.data.redis.channels.mentorship_requested_channel.name}")
    private String mentorshipRequestEventChannel;
    @Value("${spring.data.redis.channels.event_start_channel.name}")
    private String eventStartChannel;
    @Value("${spring.data.redis.channels.goal_set_channel.name}")
    private String goalSetEventChannel;
    @Value("${spring.data.redis.channels.search_appearance_channel.name}")
    private String searchAppearanceEventChannel;
    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        log.info("Created redis connection factory with host: {}, port: {}", host, port);
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory,
                                                                       UserBannerListener messageListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListener, new ChannelTopic(userBannerChannel));
        return container;
    }

    @Bean
    ChannelTopic mentorshipRequestEventChannel() {
        return new ChannelTopic(mentorshipRequestEventChannel);
    }

    @Bean
    ChannelTopic eventStartChannel() {
        return new ChannelTopic(eventStartChannel);
    }

    @Bean
    ChannelTopic goalSetEventChannel() {
        return new ChannelTopic(goalSetEventChannel);
    }

    @Bean
    ChannelTopic searchAppearanceEventChannel() {
        return new ChannelTopic(searchAppearanceEventChannel);
    }
}
