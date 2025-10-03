package school.faang.user_service.dmitrrysprint1.collections;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        User user = new User();
        String[] youngAgeActievities = new String[]{"lodka", "vodka", "molodka"};
        String[] matureAgeActievities = new String[]{"kino", "vino", "domino"};
        String[] oldAgeActievities = new String[]{"kefir", "klistir", "sortir"};
        String[] userActivities = new String[]{"sport", "vino", "kino", "domino"};
        user.setActivieties(oldAgeActievities);
        user.setName("Default");
        user.setAge(1);
        user.setId(1);

        User user2 = new User();
        user2.setActivieties(matureAgeActievities);
        user2.setName("Default");
        user2.setAge(1);
        user2.setId(1);
        User user3 = new User();
        user3.setActivieties(youngAgeActievities);
        user3.setName("Default");
        user3.setAge(1);
        user3.setId(1);
        User user4 = new User();
        user4.setActivieties(oldAgeActievities);
        user4.setName("Default");
        user4.setAge(1);
        user4.setId(1);
        User user5 = new User();
        user5.setActivieties(youngAgeActievities);
        user5.setName("Default");
        user5.setAge(1);
        user5.setId(1);


        User[] users =new User[5];
        users[0] = user;
        users[1] = user2;
        users[2] = user3;
        users[3] = user4;
        users[4] = user5;


        Map<User, String> actMap = user.findHobbyLovers(users,youngAgeActievities);

        System.out.println(actMap.entrySet());
    }
}
