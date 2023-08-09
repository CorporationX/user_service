package school.faang.user_service.filter.event;

import java.util.stream.Stream;

public interface Filter<T, U> {
    boolean isApplicable(U filter);

    Stream<T> applyFilter(Stream<T> events, U filter);
}
