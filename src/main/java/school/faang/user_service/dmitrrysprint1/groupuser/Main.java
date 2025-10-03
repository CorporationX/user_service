package school.faang.user_service.dmitrrysprint1.groupuser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<User> users = new ArrayList<>();
        User u1 = new User(5, "Lev Sinichkin", "developer", "Orenburg");
        User u2 = new User(5, "Igor Ivanovich", "developer", "Orenburg");
        User u3 = new User(7, "Stepan Petrovich", "developer", "Orenburg");
        User u4 = new User(6, "Andrey Unqer", "developer", "Orenburg");
        User u5 = new User(7, "Nicolay Primakov", "developer", "Orenburg");

        users.add(u1);
        users.add(u2);
        users.add(u3);
        users.add(u4);
        users.add(u5);

        System.out.println(users.size());


        HashMap<Integer, List<User>> users2 = User.addGroupAndUser(users);


        System.out.println(users2.entrySet());


    }
}