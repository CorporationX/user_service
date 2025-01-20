package school.faang.user_service.enums;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import school.faang.user_service.dto.request.SortRequest;

public enum SortDirection {

    ASC {
        public <T> Order build(Root<T> root, CriteriaBuilder cb, SortRequest request) {
            // if request.getKey() is dot-notation, do something like:
            Path<?> path = resolvePath(root, request.getKey());
            return cb.asc(path);
        }
    },
    DESC {
        public <T> Order build(Root<T> root, CriteriaBuilder cb, SortRequest request) {
            Path<?> path = resolvePath(root, request.getKey());
            return cb.desc(path);
        }
    };

    private static <T> Path<?> resolvePath(Root<T> root, String dotPath) {
        // e.g. "cars.engine.horsepower" => root.join("cars").join("engine").get("horsepower")
        String[] parts = dotPath.split("\\.");
        From<?, ?> current = root;
        for (int i = 0; i < parts.length - 1; i++) {
            current = current.join(parts[i], JoinType.LEFT);
        }
        return current.get(parts[parts.length - 1]);
    }

    public abstract <T> Order build(Root<T> root, CriteriaBuilder cb, SortRequest request);

}
