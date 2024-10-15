package school.faang.user_service.redis.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestedEventDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private Topic mentorshipRequestReceivedTopicName;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MentorshipRequestPublisher mentorshipRequestPublisher;

    private MentorshipRequest mentorshipRequest;
    private MentorshipRequestedEventDto eventDto;

    @BeforeEach
    public void setUp() {
        mentorshipRequest = new MentorshipRequest();
        User user1 = User.builder()
                .id(2L)
                .build();
        User user2 = User.builder()
                .id(3L)
                .build();
        mentorshipRequest.setId(1L);
        mentorshipRequest.setReceiver(user1);
        mentorshipRequest.setRequester(user2);
        mentorshipRequest.setCreatedAt(LocalDateTime.now());

        eventDto = new MentorshipRequestedEventDto(
                mentorshipRequest.getId(),
                mentorshipRequest.getReceiver().getId(),
                mentorshipRequest.getRequester().getId(),
                mentorshipRequest.getCreatedAt()
        );
    }

    @Test
    public void testPublishPostEvent() throws Exception {
        when(objectMapper.writeValueAsString(any(MentorshipRequestedEventDto.class))).thenReturn(eventDto.toString());

        mentorshipRequestPublisher.publishPostEvent(mentorshipRequest);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);


        verify(redisTemplate).convertAndSend(topicCaptor.capture(), messageCaptor.capture());

        assertEquals(mentorshipRequestReceivedTopicName.getTopic(), topicCaptor.getValue());
        assertEquals(eventDto.toString(), messageCaptor.getValue());
    }

    @Test
    public void testPublishPostEventJsonProcessingException() throws Exception {
        when(objectMapper.writeValueAsString(any(MentorshipRequestedEventDto.class))).thenThrow(new JsonProcessingException("Error") {});

        mentorshipRequestPublisher.publishPostEvent(mentorshipRequest);

        verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
    }
}