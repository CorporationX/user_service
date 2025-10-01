package school.faang.user_service.dmitrrysprint1.collections;

import java.util.*;

public class User {

    private String[] activieties;
    private int id;
    private int age;
    private String name;
    private User[] users;

    public User[] getUsers() {
        return users;
    }

    public void setUsers(User[] users) {
        this.users = users;
    }


    public User(String[] activieties, int id, int age, String name, User[] users) {
        this.activieties = activieties;
        this.id = id;
        this.age = age;
        this.name = name;
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getId() == user.getId() && getAge() == user.getAge() && Arrays.equals(getActivieties(), user.getActivieties()) && getName().equals(user.getName());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getId(), getAge(), getName());
        result = 31 * result + Arrays.hashCode(getActivieties());
        return result;
    }

    public User() {
    }

    public String[] getActivieties() {
        return activieties;
    }

    public void setActivieties(String[] activieties) {
        this.activieties = activieties;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "activieties=" + Arrays.toString(activieties) +
                ", id=" + id +
                ", age=" + age +
                ", name='" + name + '\'' +
                '}';
    }

    public Map<User, String> findHobbyLovers(User[] users, String[] actievities){

        Map<User, String> hobbyLovers = new HashMap<>();
        for(User u : users){
            String firstActivity = findFirstActievity(u.getActivieties(), actievities);
            if(firstActivity != "Not found"){
                hobbyLovers.put(u, firstActivity);
            }

        }

return hobbyLovers;
    }

    public String findFirstActievity(String[] firstValues, String[] secondValues){
        for( int i = 0; i<firstValues.length; i++){
            for(int j = 0; j<secondValues.length; j++){
                if(secondValues[j].equals(firstValues[i]))
                    return secondValues[j];
                break;
            }
        }
        return "Not found";

    }
}
