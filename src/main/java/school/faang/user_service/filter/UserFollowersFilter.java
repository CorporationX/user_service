package school.faang.user_service.filter;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

public interface UserFollowersFilter {
    boolean isApplicable(User follower, UserFilterDto filter);

    boolean test(User follower, UserFilterDto filter);

    default boolean apply(User follower, UserFilterDto filter) {
        if (follower == null || filter == null) {
            return false;
        }
        return !isApplicable(follower, filter) || test(follower, filter);
    }
}
