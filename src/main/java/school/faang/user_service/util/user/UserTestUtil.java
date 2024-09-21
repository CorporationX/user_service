package school.faang.user_service.util.user;

import school.faang.user_service.builder.user.UserBuilder;
import school.faang.user_service.constant.TestConst;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.LongStream;

public class UserTestUtil {
    public static User getUser(long id, String username, String email) {
        return new UserBuilder()
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