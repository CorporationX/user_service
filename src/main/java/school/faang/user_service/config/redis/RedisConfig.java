package school.faang.user_service.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.FollowerEventDto;
import school.faang.user_service.dto.event.GoalCompletedEventDto;
import school.faang.user_service.dto.event.MentorshipAcceptedEventDto;
import school.faang.user_service.dto.event.ProfileViewEvent;
import school.faang.user_service.service.user.redis.RedisMessageSubscriber;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final ObjectMapper objectMapper;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter(RedisMessageSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber);
    }

    @Bean
    public RedisMessageListenerContainer container(MessageListenerAdapter messageListenerAdapter,
                                                   @Qualifier("userBanChannel") ChannelTopic userBanChannel) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        container.addMessageListener(messageListenerAdapter, userBanChannel);
        return container;
    }

    @Bean
    RedisTemplate<String, EventDto> redisEventTemplate() {
        RedisTemplate<String, EventDto> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, EventDto.class));
        return template;
    }

    @Bean
    RedisTemplate<String, FollowerEventDto> followerEventredisTemplate() {
        RedisTemplate<String, FollowerEventDto> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, FollowerEventDto.class));
        return template;
    }

    @Bean
    public RedisTemplate<String, ProfileViewEvent> profileViewRedisTemplate() {
        RedisTemplate<String, ProfileViewEvent> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, ProfileViewEvent.class));
        return template;
    }

    @Bean
    RedisTemplate<String, GoalCompletedEventDto> redisGoalTemplate() {
        RedisTemplate<String, GoalCompletedEventDto> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, GoalCompletedEventDto.class));
        return template;
    }

    @Bean
    RedisTemplate<String, MentorshipAcceptedEventDto> mentorshipAcceptedEventredisTemplate() {
        RedisTemplate<String, MentorshipAcceptedEventDto> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, MentorshipAcceptedEventDto.class));
        return template;
    }

    @Bean(value = "userBanChannel")
    public ChannelTopic userBanChannel(
            @Value("${spring.data.redis.channels.user-ban-channel.name}") String userBanTopicName) {
        return new ChannelTopic(userBanTopicName);
    }

    @Bean(value = "eventTopic")
    public ChannelTopic eventTopic(@Value("${spring.data.redis.channels.event-event-channel.name}") String topic) {
        return new ChannelTopic(topic);
    }

    @Bean(value = "followerEventChannel")
    ChannelTopic followerEventChannelTopic(
            @Value("${spring.data.redis.channels.follower-channel.name}") String name) {
        return new ChannelTopic(name);
    }

    @Bean(value = "profileViewChannel")
    public ChannelTopic profileViewChannelTopic(
            @Value("${spring.data.redis.channels.profile-view-channel.name}") String profileViewChannelName) {
        return new ChannelTopic(profileViewChannelName);
    }

    @Bean(value = "goalCompletedTopic")
    public ChannelTopic goalCompletedTopic(
            @Value("${spring.data.redis.channels.goal-event-channel.name}") String topic) {
        return new ChannelTopic(topic);
    }

    @Bean(value = "mentorshipEventChannel")
    ChannelTopic mentorshipEventChannelTopic(
            @Value("${spring.data.redis.channels.mentorship-channel.name}") String name) {
        return new ChannelTopic(name);
    }
}
