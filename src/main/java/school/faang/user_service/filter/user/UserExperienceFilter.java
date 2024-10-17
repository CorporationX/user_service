package school.faang.user_service.filter.user;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.filter_dto.user.UserFilterDto;
import school.faang.user_service.model.entity.User;

import java.util.stream.Stream;

@Component
public class UserExperienceFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getExperience() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.filter(g -> g.getExperience() >= filters.getExperience());
    }

    @Override
    public Specification<User> toSpecification(UserFilterDto filters) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("experience"),
                        filters.getExperience()
                );
    }
}
