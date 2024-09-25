package school.faang.user_service.filter;

import org.springframework.data.jpa.domain.Specification;

import java.util.stream.Stream;

public interface DtoFilter<FilterDto, Entity> {

    boolean isApplicable(FilterDto filters);

    Stream<Entity> apply(Stream<Entity> entities, FilterDto filters);

    default Specification<Entity> toSpecification(FilterDto filters) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    }
}
