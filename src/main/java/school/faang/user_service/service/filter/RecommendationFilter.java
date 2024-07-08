package school.faang.user_service.service.filter;

import java.util.stream.Stream;

public interface RecommendationFilter<T, F> {
    boolean isApplicable(F filterDto);

    Stream<T> apply(Stream<T> stream, F filterDto);
}
