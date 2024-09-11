package school.faang.user_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.listener.UserBanListener;
import school.faang.user_service.dto.ProfileViewEvent;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;
    @Value("${spring.data.redis.channels.mentorship_request_channel.name}")
    private String mentorshipRequestTopicName;
    @Value("${spring.data.redis.channels.follower_channel.name}")
    private String followerChannel;
    @Value("${spring.data.redis.channels.mentorship_channel.name}")
    private String mentorshipChannel;
    @Value("${spring.data.redis.topic.userBan}")
    private String userBanChannel;
    @Value("${spring.data.redis.channels.mentorship_offered_channel.name}")
    private String mentorshipOfferedChannelName;
    @Value("${spring.data.redis.channels.profile_picture_channel.name}")
    private String profilePicture;
    @Value("${spring.data.redis.channels.mentorship-accepted-channel}")
    private String mentorshipAcceptedChannel;
    @Value("${spring.data.redis.channels.profile_view_channel.name}")
    private String profileViewTopicName;

    public interface MessagePublisher {
        void publish(ProfileViewEvent profileViewEvent);
    }

    @Bean(name = "followerChannelTopic")
    public ChannelTopic followerChannelTopic() {
        return new ChannelTopic(followerChannel);
    }

    @Bean
    public ChannelTopic mentorshipChannelTopic() {
        return new ChannelTopic(mentorshipChannel);
    }

    @Bean
    public ChannelTopic userBanTopic() {
        return new ChannelTopic(userBanChannel);
    }

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public ChannelTopic mentorshipAcceptedChannelTopic() {
        return new ChannelTopic(mentorshipAcceptedChannel);
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
    public ChannelTopic mentorshipOfferedChannel() {
        return new ChannelTopic(mentorshipOfferedChannelName);
    }

    @Bean
    public ChannelTopic profilePictureTopic() {
        return new ChannelTopic(profilePicture);
    }

    @Bean
    public ChannelTopic profileViewTopic() {
        return new ChannelTopic(profileViewTopicName);
    }

    @Bean
    public ChannelTopic mentorshipRequestTopic() {
        return new ChannelTopic(mentorshipRequestTopicName);
    }

    @Bean
    MessageListenerAdapter messageListener(UserBanListener userBanListener) {
        return new MessageListenerAdapter(userBanListener);
    }
}
