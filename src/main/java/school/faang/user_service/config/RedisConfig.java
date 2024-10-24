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

    @Value("${redis.channels.search-appearance-channel}")
    private String searchAppearanceChannel;

    @Value("${redis.channels.profile-view-channel}")
    private String profileViewChannel;

    @Value("${redis.channels.user-ban}")
    private String userBanEventChannel;

    @Value("${redis.channels.user-follower}")
    private String userFollowerEventChannel;

    @Value("${redis.channels.goal-completed}")
    private String goalCompletedEventChannel;

    @Value("${redis.channels.mentorship-accepted}")
    private String mentorshipAcceptedEventChannel;

    @Value("${redis.channels.skill-acquired}")
    private String skillAcquiredEventChannel;

    @Value("${redis.channels.mentorship-offered}")
    private String mentorshipOfferedEventChannel;

    @Value("${redis.channels.recommendation-received}")
    private String recommendationReceivedEventChannel;

    @Value("${redis.channels.profile-view}")
    private String profileViewEventChannel;

    @Value("${redis.channels.skill-offered}")
    private String skillOfferedEventChannel;

    @Value("${redis.channels.recommendation-requested}")
    private String recommendationRequestedEventChannel;

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

    @Bean
    public ChannelTopic projectFollowerChannelTopic() {
        return new ChannelTopic(projectFollowerEventChannel);
    }

    @Bean
    ChannelTopic searchAppearanceTopic() {
        return new ChannelTopic(searchAppearanceChannel);
    }

    @Bean
    ChannelTopic profileViewTopic() {
        return new ChannelTopic(profileViewChannel);

    }

    @Bean
    public ChannelTopic userBanChannelTopic() {
        return new ChannelTopic(userBanEventChannel);
    }

    @Bean
    public ChannelTopic userFollowerChannelTopic() {
        return new ChannelTopic(userFollowerEventChannel);
    }

    @Bean(name = "goalCompletedTopic")
    public ChannelTopic goalCompletedChannelTopic() {
        return new ChannelTopic(goalCompletedEventChannel);
    }

    @Bean(name = "mentorshipAcceptedTopic")
    public ChannelTopic mentorshipAcceptedChannelTopic() {
        return new ChannelTopic(mentorshipAcceptedEventChannel);
    }

    @Bean(name = "skillAcquiredTopic")
    public ChannelTopic skillAcquiredChannelTopic() {
        return new ChannelTopic(skillAcquiredEventChannel);
    }

    @Bean
    public ChannelTopic mentorshipOfferedChannelTopic() {
        return new ChannelTopic(mentorshipOfferedEventChannel);
    }

    @Bean
    public ChannelTopic recommendationReceivedChannelTopic() {
        return new ChannelTopic(recommendationReceivedEventChannel);
    }

    @Bean(name = "profileViewTopic")
    public ChannelTopic profileViewChannelTopic() {
        return new ChannelTopic(profileViewEventChannel);
    }

    @Bean(name = "skillOfferedTopic")
    public ChannelTopic skillOfferedChannelTopic() {
        return new ChannelTopic(skillOfferedEventChannel);
    }

    @Bean(name = "recommendationRequestedTopic")
    public ChannelTopic recommendationRequestedChannelTopic() {
        return new ChannelTopic(recommendationRequestedEventChannel);
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
