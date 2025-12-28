package school.faang.user_service.service.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import school.faang.user_service.dto.post.PostCreatedEvent;
import school.faang.user_service.dto.post.PostToFeedEvent;
import school.faang.user_service.outbox.entity.OutboxEvent;
import school.faang.user_service.outbox.entity.OutboxEventType;
import school.faang.user_service.outbox.entity.OutboxStatus;
import school.faang.user_service.outbox.repository.OutboxRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class PostCreatedBatchServiceTest {
    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PostCreatedBatchService service;

    @Test
    void processBatch_shouldSaveOutboxEvent() throws Exception {
        ReflectionTestUtils.setField(service, "serviceName", "user-service");

        PostCreatedEvent event = PostCreatedEvent.builder()
                .id(10L)
                .authorId(20L)
                .createdAt(123L)
                .build();

        List<Long> subscribers = List.of(1L, 2L, 3L);

        String json = "{\"ok\":true}";

        when(objectMapper.writeValueAsString(any(PostToFeedEvent.class)))
                .thenReturn(json);

        service.processBatch(event, subscribers);

        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxRepository).save(captor.capture());

        OutboxEvent saved = captor.getValue();

        assertThat(saved.getEventType()).isEqualTo(OutboxEventType.POST_TO_FEED);
        assertThat(saved.getAggregateId()).isEqualTo(10L);
        assertThat(saved.getPayload()).isEqualTo(json);
        assertThat(saved.getStatus()).isEqualTo(OutboxStatus.NEW);
        assertThat(saved.getSourceService()).isEqualTo("user-service");
    }

    @Test
    void processBatch_shouldThrow_whenSerializationFails() throws Exception {
        ReflectionTestUtils.setField(service, "serviceName", "user-service");

        PostCreatedEvent event = PostCreatedEvent.builder()
                .id(1L)
                .authorId(2L)
                .createdAt(123L)
                .build();

        when(objectMapper.writeValueAsString(any()))
                .thenThrow(new JsonProcessingException("boom") {});

        assertThatThrownBy(() ->
                service.processBatch(event, List.of(1L, 2L))
        ).isInstanceOf(JsonProcessingException.class);

        verify(outboxRepository, never()).save(any());
    }
}