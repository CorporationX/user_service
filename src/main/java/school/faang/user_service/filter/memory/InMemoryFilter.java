package school.faang.user_service.filter.memory;

import school.faang.user_service.dto.filter.FilterDto;
import school.faang.user_service.dto.user.UserDto;

import java.util.stream.Stream;

public interface InMemoryFilter<T, D extends FilterDto> {

    boolean isApplicable(D filterDto);

    Stream<T> apply(Stream<UserDto> stream, D filterDto);

}
