package school.faang.user_service.service.recommendation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Data
@AllArgsConstructor
public class User {
    private final String name;
    private int age;
    private String job;
    private String address;

    private static Map<Integer, List<User>> groupUsers(List<User> users) {

        return null;
    }

    public static void main(String[] args) {
        User user1 = new User("user1", 1, "job1", "address1");
        User user2 = new User("user2", 1, "job2", "address2");
        User user3 = new User("user3", 1, "job3", "address3");
        User user4 = new User("user4", 1, "job4", "address4");
        User user5 = new User("user5", 1, "job5", "address5");
        List<User> users = new ArrayList<>(List.of(user1, user2, user3, user4, user5));
        Map<Integer, List<User>> integerListMap = groupUsers(users);
        System.out.println(integerListMap);
    }
}
