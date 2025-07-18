package school.faang.user_service.entity.filter;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.function.Function;

public abstract class BaseFilterBuilder<T, U> {
    public abstract List<? extends BaseFilter<T, U>> getFilters();

    private static <T> Specification<T> all() {
        return (root, query, cb) -> cb.conjunction();
    }

    public Specification<T> buildSpecification(
            U params,
            Function<Specification<T>, Specification<T>> customizer
    ) {
        Specification<T> spec = all();
        if (customizer != null) {
            spec = customizer.apply(spec);
        }
        if (params == null) {
            return spec;
        }
        for (BaseFilter<T, U> filter : getFilters()) {
            if (filter.isApplicable(params)) {
                spec = filter.apply(spec, params);
            }
        }

        return spec;
    }
}
