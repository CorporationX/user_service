package school.faang.user_service.filter;

import java.util.List;
import java.util.stream.Stream;

public interface Filter<F, E> {

    boolean isApplicable(F filter);

    List<E> apply(F filter, Stream<E> entityList);
}
