package school.faang.user_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.support.Acknowledgment;
import school.faang.user_service.kafka.producer.FeedHeatKafkaProducer;
import school.faang.user_service.model.event.kafka.AuthorPostByHeatKafkaEvent;
import school.faang.user_service.model.event.kafka.PostPublishedKafkaEvent;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.RedisUserService;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorPostByHeatKafkaConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisUserService redisUserService;

    @Mock
    private FeedHeatKafkaProducer feedHeatKafkaProducer;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private AuthorPostByHeatKafkaConsumer consumer;

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        Field batchSizeField = AuthorPostByHeatKafkaConsumer.class.getDeclaredField("followerBatchSize");
        batchSizeField.setAccessible(true);
        batchSizeField.set(consumer, 1000);
    }

    @Test
    void testProcessEvent_Success() {
        Long authorId = 1L;
        Long postId = 2L;
        LocalDateTime publishedAt = LocalDateTime.now();
        List<Long> lastCommentAuthors = List.of(3L, 4L);

        AuthorPostByHeatKafkaEvent event = new AuthorPostByHeatKafkaEvent(postId, authorId, lastCommentAuthors, publishedAt);

        Page<Long> followersPage1 = new PageImpl<>(List.of(5L, 6L));
        Page<Long> emptyPage = Page.empty();

        when(userRepository.findUnbannedFollowerIdsByUserId(eq(authorId), any(PageRequest.class)))
                .thenReturn(followersPage1, emptyPage);

        consumer.processEvent(event);

        verify(redisUserService).saveUser(authorId);
        lastCommentAuthors.forEach(commentAuthor -> verify(redisUserService).saveUser(commentAuthor));

        ArgumentCaptor<PostPublishedKafkaEvent> eventCaptor = ArgumentCaptor.forClass(PostPublishedKafkaEvent.class);
        verify(feedHeatKafkaProducer).sendEvent(eventCaptor.capture());

        PostPublishedKafkaEvent capturedEvent = eventCaptor.getValue();
        assertEquals(postId, capturedEvent.getPostId());
        assertEquals(List.of(5L, 6L), capturedEvent.getFollowerIds());
        assertEquals(publishedAt, capturedEvent.getPublishedAt());
    }

    @Test
    void testSendPostPublishedKafkaEvents_NoFollowers() {
        Long authorId = 1L;
        Long postId = 2L;
        LocalDateTime publishedAt = LocalDateTime.now();
        List<Long> lastCommentAuthors = List.of(3L, 4L);

        AuthorPostByHeatKafkaEvent event = new AuthorPostByHeatKafkaEvent(postId, authorId, lastCommentAuthors, publishedAt);

        when(userRepository.findUnbannedFollowerIdsByUserId(eq(authorId), any(PageRequest.class))).thenReturn(Page.empty());

        consumer.processEvent(event);

        verify(feedHeatKafkaProducer, never()).sendEvent(any());
    }
}
