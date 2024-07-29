package school.faang.user_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableFeignClients("school.faang.user_service.client")
@EnableAsync
@OpenAPIDefinition(
        info = @Info(
                title = "User Service",
                version = "1.0.0")
)
public class UserServiceApplication {

    @Value("${thread-pool.size.min}")
    private int POOL_MIN_SIZE;

    @Value("${thread-pool.size.max}")
    private int POOL_MAX_SIZE;

    @Value("${thread-pool.keep-alive.time}")
    private long KEEP_ALIVE_TIME;

    @Value("${thread-pool.keep-alive.time-unit}")
    private TimeUnit KEEP_ALIVE_TIME_UNIT;

    @Value("${thread-pool.queue-size}")
    private int POOL_QUEUE_SIZE;

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ThreadPoolExecutor threadPool() {
        return new ThreadPoolExecutor(POOL_MIN_SIZE,
                POOL_MAX_SIZE,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                new LinkedBlockingQueue<>(POOL_QUEUE_SIZE));
    }
}