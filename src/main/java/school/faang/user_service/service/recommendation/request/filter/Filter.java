package school.faang.user_service.service.recommendation.request.filter;

import java.util.stream.Stream;

public interface Filter<F, D> {

    default Stream<D> applyFilter(Stream<D> filterable, F filter) {
        return filterable.filter(d -> applyFilter(d, filter));
    }

    boolean applyFilter(D data, F filter);

    boolean isApplicable(F filter);
}
