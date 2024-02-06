package school.faang.user_service.service.filter;

import school.faang.user_service.dto.entity.EntityDto;
import school.faang.user_service.dto.filter.FilterDto;

import java.util.stream.Stream;

public interface InMemoryFilterService<E extends EntityDto, F extends FilterDto> {
    Stream<E> applyFilters(Stream<E> userDtoStream, F filterDto);
}
