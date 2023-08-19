package school.faang.user_service.executors;

import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class PullUserService {
    private final ExecutorService pull = Executors.newFixedThreadPool(100);
}
