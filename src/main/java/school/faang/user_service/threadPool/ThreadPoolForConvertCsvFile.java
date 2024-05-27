package school.faang.user_service.threadPool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolForConvertCsvFile {

    @Bean
    public ExecutorService taskExecutor() {
        return Executors.newFixedThreadPool(4);
    }
}
