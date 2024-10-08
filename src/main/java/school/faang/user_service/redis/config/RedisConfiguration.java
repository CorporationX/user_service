package school.faang.user_service.redis.config;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.redis.MessagePublisher;
import school.faang.user_service.redis.RedisMessageConsumerMentorshipRequests;
import school.faang.user_service.redis.RedisMessageMentorshipRequestsPublisher;
import school.faang.user_service.redis.RedisMessageMentorshipRequestsSubscriber;


@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {

    private final RedisMessageConsumerMentorshipRequests consumerMentorshipRequests;

    @Value("${spring.data.redis.channels.mentorship-channel.name}")
    private String topicName;

    @Bean
    public MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(new RedisMessageMentorshipRequestsSubscriber());
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(consumerMentorshipRequests);
        container.addMessageListener(messageListenerAdapter, new ChannelTopic(topicName));

        return container;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic(topicName);
    }

    @Bean
    public MessagePublisher redisPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
        return new RedisMessageMentorshipRequestsPublisher(redisTemplate, topic);
    }
}