package school.faang.user_service.config.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.config.event.properties.EventProperties;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@RequiredArgsConstructor
public class EventExecutorConfig {
    private final EventProperties eventProperties;

    @Bean(name = "deletePastEventsThreadPool")
    public ExecutorService deletePastEventsThreadPool() {
        return Executors.newFixedThreadPool(eventProperties.getThreadsNum());
    }
}
