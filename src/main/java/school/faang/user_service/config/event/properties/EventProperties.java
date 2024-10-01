package school.faang.user_service.config.event.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class EventProperties {
    @Value("${clean_past_events.clean_interval}")
    private String cleanInterval;

    @Value("${clean_past_events.threads_num}")
    private int threadsNum;

    @Value("${clean_past_events.sublist_size}")
    private int sublistSize;
}
