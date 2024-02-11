package school.faang.user_service.util;

import school.faang.user_service.entity.User;

import java.util.List;

public class UserUtils {
    public static boolean findUserById(List<User> userList, long userId) {
        for (User user : userList) {
            if (user.getId() == userId) {
                return true;
            }
        }
        return false;
    }
}
