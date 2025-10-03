package school.faang.user_service.dmitrrysprint1.groupuser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class User {

    private int age;
    private String name;
    private String job;
    private String address;


    public User(int age, String name, String job, String address) {
        this.age = age;
        this.name = name;
        this.job = job;
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public static HashMap<Integer, List<User>> addGroupAndUser(List<User> users) {
        HashMap<Integer, List<User>> userMap = new HashMap<>();
        for (User user : users) {

            if (!userMap.containsKey(user.getAge())) {
                ArrayList<User> usList = new ArrayList<>();
                usList.add(user);
                userMap.put(user.getAge(), usList);

            } else {
                List<User> fdf = userMap.get(user.getAge());
                fdf.add(user);

                userMap.put(user.getAge(), fdf);
            }
        }
        return userMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getAge() == user.getAge() && Objects.equals(name, user.name) && Objects.equals(job, user.job) && Objects.equals(address, user.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAge(), name, job, address);
    }

    @Override
    public String toString() {
        return "User{" + "age=" + age + ", name='" + name + '\'' + ", job='" + job + '\'' + ", address='" + address + '\'' + '}';
    }
}
