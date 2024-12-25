package school.faang.user_service.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CollectionUtils {

    @SafeVarargs
    public static <T> void filterAndProcess(
            Collection<T> items,
            Consumer<T> consumer,
            Predicate<T>... filters
    ) {
        Stream<T> stream = items.stream();
        for (Predicate<T> filter : filters) {
            stream = stream.filter(filter);
        }
        stream.forEach(consumer);
    }

    public static <T> List<T> excludeItemFrom(Collection<T> items, T excludedItem) {
        return items.stream()
                .filter(item -> !item.equals(excludedItem))
                .toList();
    }

    public static <T> List<T> findMissingElements(List<T> source, List<T> compareList) {
        return source.stream()
                .filter(skill -> !compareList.contains(skill))
                .toList();
    }

    public static <T> List<T> merge(List<T> list1, List<T> list2) {
        List<T> merged = new ArrayList<>(list1);
        merged.addAll(list2);
        return merged;
    }

    public static <T> boolean isNotEmpty(List<T> list) {
        return list != null && !list.isEmpty();
    }
}
