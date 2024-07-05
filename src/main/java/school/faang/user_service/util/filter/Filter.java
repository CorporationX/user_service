package school.faang.user_service.util.filter;

import school.faang.user_service.dto.filter.FilterDto;

import java.util.stream.Stream;

public interface Filter<T extends FilterDto, F> {
    boolean isApplicable(T filterDto);

    Stream<F> apply(Stream<F> itemStream, T filterDto);
}