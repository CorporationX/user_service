package school.faang.user_service.service.user.filters;

import java.util.stream.Stream;

public interface UserFilter<F, D> {
    boolean isApplicable(F filterDto);

    Stream<D> apply(Stream<D> users, F filterDto);
}
