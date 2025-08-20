package school.faang.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Конфигурация для пулов потоков приложения.
 * <p>
 * Данный класс определяет {@link ExecutorService}, используемый для асинхронной
 * публикации событий и других задач, где требуется многопоточность.
 * </p>
 *
 * <ul>
 *     <li>Создаётся пул из 4 потоков ({@code Executors.newFixedThreadPool(4)}).</li>
 *     <li>Бин помечен {@code destroyMethod = "shutdown"} — при завершении приложения
 *     Spring корректно останавливает все потоки.</li>
 *     <li>Экземпляр пула является singleton (по умолчанию в Spring),
 *     используется во всём приложении.</li>
 * </ul>
 *
 * @author Myrza
 * @since 20.08.2025
 */
@Configuration
public class ExecutorConfig {
    @Bean(destroyMethod = "shutdown")
    public ExecutorService eventPublisherExecutor() {
        return Executors.newFixedThreadPool(4);
    }
}
