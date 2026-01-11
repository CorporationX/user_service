package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.RecommendationReceivedEvent;
import school.faang.user_service.publisher.RecommendationReceivedEventPublisher;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceImplTest {
    @Mock
    RecommendationReceivedEventPublisher publisher;
    @Mock
    UserContext userContext;
    @InjectMocks
    RecommendationServiceImpl service;

    @Test
    public void recommendUser_publishesEventWithCorrectAuthorAndReceiver() {
        long authorId = 10L;
        long receiverId = 5L;
        when(userContext.getUserId()).thenReturn(authorId);

        service.recommendUser(receiverId);
        ArgumentCaptor<RecommendationReceivedEvent> captor =
                ArgumentCaptor.forClass(RecommendationReceivedEvent.class);
        verify(publisher).publish(captor.capture());

        RecommendationReceivedEvent event = captor.getValue();
        assertThat(event.getAuthorId()).isEqualTo(authorId);
        assertThat(event.getReceiveId()).isEqualTo(receiverId);
        assertThat(event.getId()).isBetween(0L, 999L);
    }
}