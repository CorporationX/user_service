package school.faang.user_service.filter;

import java.util.stream.Stream;

public interface DtoFilter<FilterDto, Entity> {

    boolean isApplicable(FilterDto filters);

    Stream<Entity> apply(Stream<Entity> entities, FilterDto filters);

}
