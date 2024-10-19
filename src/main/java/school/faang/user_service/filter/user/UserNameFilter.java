package school.faang.user_service.filter.user;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.filter_dto.UserFilterDto;
import school.faang.user_service.model.entity.User;

import java.util.stream.Stream;

@Component
public class UserNameFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getNamePattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.filter(g -> g.getUsername().contains(filters.getNamePattern()));
    }

    @Override
    public Specification<User> toSpecification(UserFilterDto filters) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("username")),
                        "%" + filters.getNamePattern().toLowerCase() + "%"
                );
    }
}
