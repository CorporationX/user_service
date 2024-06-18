package school.faang.user_service.config.threadPool;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig {

    @Primary
    @Bean(name = "threadPoolForConvertCsvFile")
    public ThreadPoolTaskExecutor threadPoolForConvertCsvFile() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("CSVFileExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "threadPoolForEventProcessing")
    public ThreadPoolTaskExecutor threadPoolForEventProcessing() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(1000);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("EventProcessor-");
        executor.initialize();
        return executor;
    }
}
