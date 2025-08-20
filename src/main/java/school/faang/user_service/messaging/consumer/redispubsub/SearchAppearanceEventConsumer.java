package school.faang.user_service.messaging.consumer.redispubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import school.faang.user_service.mapper.analytics.SearchAppearanceMapper;
import school.faang.user_service.messaging.dto.SearchAppearanceEvent;
import school.faang.user_service.service.analytics.SearchAppearanceService;

import java.nio.charset.StandardCharsets;

/**
 * Консьюмер для событий {@link SearchAppearanceEvent}, поступающих из Redis Pub/Sub.
 *
 * @author Myrza
 * @since 19.08.2025
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SearchAppearanceEventConsumer implements MessageListener {
    private final ObjectMapper objMapper;
    private final SearchAppearanceService service;
    private final SearchAppearanceMapper mapper;

    /**
     * Обрабатывает входящее сообщение из Redis Pub/Sub.
     *
     * @param message сообщение в формате JSON, содержащее событие {@link SearchAppearanceEvent}
     * @param pattern топик, из которого пришло сообщение
     */
    @Override
    public void onMessage(@NotNull Message message, byte[] pattern) {
        var topic = new String(pattern, StandardCharsets.UTF_8);
        try {
            var event = objMapper.readValue(message.getBody(), SearchAppearanceEvent.class);
            log.info("start consume message on topic: '{}' event: {}", topic, event);
            var dto = mapper.toDto(event);
            service.addSearchAppearance(dto);
        } catch (Exception e) {
            log.error("SearchAppearanceEventConsumer.onMessage topic: {}", topic, e);
        }
    }
}
