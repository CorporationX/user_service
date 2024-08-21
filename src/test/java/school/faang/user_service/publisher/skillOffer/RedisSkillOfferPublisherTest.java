package school.faang.user_service.publisher.skillOffer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.skill.SkillOfferedEventDto;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisSkillOfferPublisherTest {

    @Mock
    RedisTemplate<String, Object> redisTemplate;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    ChannelTopic skillOfferedTopic;

    @InjectMocks
    RedisSkillOfferPublisher redisSkillOfferPublisher;

    SkillOfferedEventDto skillOfferedEventDto;
    String messageJson;
    String skillOfferedTopicName;

    @BeforeEach
    @DisplayName("Should successfully publish a SkillOfferedEventDto message to Redis")
    void setUp() {
        skillOfferedEventDto = SkillOfferedEventDto.builder().build();
        messageJson = skillOfferedEventDto.toString();
        skillOfferedTopicName = "skill_offered_channel";
    }

    @Test
    void publish() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(skillOfferedEventDto)).thenReturn(messageJson);
        when(skillOfferedTopic.getTopic()).thenReturn(skillOfferedTopicName);

        redisSkillOfferPublisher.publish(skillOfferedEventDto);

        verify(objectMapper).writeValueAsString(skillOfferedEventDto);
        verify(skillOfferedTopic).getTopic();
        verify(redisTemplate).convertAndSend(skillOfferedTopic.getTopic(), messageJson);
    }
}