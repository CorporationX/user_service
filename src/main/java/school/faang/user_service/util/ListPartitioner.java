package school.faang.user_service.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ListPartitioner {
    /**
     * Разбивает список на указанное количество частей.
     * @param <T> тип элементов в списке
     * @param list список для разбиения
     * @param numPartitions количество частей для разбиения
     * @return список списков, каждый из которых представляет часть исходного списка
     */
    public <T> List<List<T>> partitionList(List<T> list, int numPartitions) {
        if (numPartitions <= 0) {
            numPartitions = 1;
        }
        int totalSize = list.size();
        int partitionSize = (int) Math.ceil((double) totalSize / numPartitions);
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < totalSize; i += partitionSize) {
            partitions.add(list.subList(i, Math.min(totalSize, i + partitionSize)));
        }
        return partitions;
    }
}