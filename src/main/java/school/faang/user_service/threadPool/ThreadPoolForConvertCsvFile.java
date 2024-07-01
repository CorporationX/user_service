package school.faang.user_service.threadPool;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolForConvertCsvFile {

    @Value("${pull.pullForCsvReader}")
    private int pullNumbers;

    @Bean
    public ExecutorService taskExecutor() {
        return Executors.newFixedThreadPool(pullNumbers);
    }
}
