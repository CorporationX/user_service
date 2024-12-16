package school.faang.user_service.config.redis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Value("${spring.data.redis.channel.search-appearance.name}")
    private String searchAppearanceTopicName;

    private static final String CREATE_CHANNEL_LOG_MESSAGE = "Создание ChannelTopic для канала: {}";

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        log.info("Создание LettuceConnectionFactory для Redis с хостом: {} и портом:{}", redisProperties.getRedisHost(), redisProperties.getRedisPort());
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisProperties.getRedisHost(), redisProperties.getRedisPort());
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        log.info("LettuceConnectionFactory успешно создан");
        return connectionFactory;
    }

    @Bean
    public ChannelTopic topicEventParticipation() {
        log.info(CREATE_CHANNEL_LOG_MESSAGE, redisProperties.getTopicEventParticipation());
        return new ChannelTopic(redisProperties.getTopicEventParticipation());
    }

    @Bean
    public ChannelTopic followerChannel() {
        log.info(CREATE_CHANNEL_LOG_MESSAGE, redisProperties.getFollowerChannel());
        return new ChannelTopic(redisProperties.getFollowerChannel());
    }

    @Bean
    public ChannelTopic unfollowerChannel() {
        log.info(CREATE_CHANNEL_LOG_MESSAGE, redisProperties.getUnfollowChannel());
        return new ChannelTopic(redisProperties.getUnfollowChannel());
    }

    @Bean
    public ChannelTopic followerProjectChannel() {
        log.info(CREATE_CHANNEL_LOG_MESSAGE, redisProperties.getFollowerProjectChannel());
        return new ChannelTopic(redisProperties.getFollowerProjectChannel());
    }

    @Bean
    public ChannelTopic unfollowProjectChannel() {
        log.info(CREATE_CHANNEL_LOG_MESSAGE, redisProperties.getUnfollowProjectChannel());
        return new ChannelTopic(redisProperties.getUnfollowProjectChannel());
    }

    @Bean
    public ChannelTopic createAppearanceTopic() {
        return new ChannelTopic(searchAppearanceTopicName);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("Создание RedisTemplate с кастомными сериализаторами.");

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);

        log.info("RedisTemplate создан и сериализаторы настроены.");

        return redisTemplate;
    }
}
