package school.faang.user_service.service.event;

import java.util.List;
import java.util.stream.IntStream;

public interface ListPartitioner {
    default <T> List<List<T>> partitionList(List<T> list, int size) {
        int numPartitions = (list.size() + size - 1) / size;
        return IntStream.range(0, numPartitions)
                .mapToObj(i -> list.subList(i * size, Math.min((i + 1) * size, list.size())))
                .toList();
    }
}
