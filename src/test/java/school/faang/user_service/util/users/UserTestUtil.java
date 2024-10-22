package school.faang.user_service.util.users;

import school.faang.user_service.constant.TestConst;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.LongStream;

public class UserTestUtil {
    public static User buildUser(long id) {
        return User.builder()
                .id(id)
                .build();
    }

    public static User buildUser(long id, String username) {
        return User.builder()
                .id(id)
                .username(username)
                .build();
    }

    public static List<User> buildUsers(int number) {
        return LongStream.rangeClosed(1, number)
                .mapToObj(UserTestUtil::buildUser)
                .toList();
    }

    public static User getUser(long id) {
        return User.builder()
                .id(id)
                .build();
    }

    public static User getUser(long id, String username, String email) {
        return User.builder()
                .id(id)
                .username(username)
                .email(email)
                .build();
    }

    public static List<User> getUsersWithIdUsernameEmail(int number) {
        return LongStream.rangeClosed(1, number)
                .mapToObj(i -> getUser(i, "Username " + i, i + TestConst.TEST_EMAIL))
                .toList();
    }
}