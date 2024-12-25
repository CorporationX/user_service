package school.faang.user_service.config.batch;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.user.UserService;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserPartitioner implements Partitioner {

    private final UserService userService;

    @NotNull
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Long minId = userService.findMinId();
        Long maxId = userService.findMaxId();
        int partitionSize = (int) (maxId - minId) / gridSize;

        Map<String, ExecutionContext> result = new HashMap<>();
        long number = 0;
        long start = minId;
        long end = start + partitionSize - 1;

        while (start <= maxId) {
            ExecutionContext context = new ExecutionContext();
            context.putLong("minId", start);
            context.putLong("maxId", end);

            result.put(String.format("partition%d", number), context);

            start = end + 1;
            end = start + partitionSize - 1;
            if (end > maxId) {
                end = maxId;
            }
            number++;
        }
        return result;
    }
}
