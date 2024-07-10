package school.faang.user_service.filters;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface UserFilter<F, D> {
    boolean isApplicable(F filterDto);

    Stream<D> apply(Stream<D> users, F filterDto);
}
