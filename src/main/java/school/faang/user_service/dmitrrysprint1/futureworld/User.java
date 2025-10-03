package school.faang.user_service.dmitrrysprint1.futureworld;


import java.util.Objects;

public class User {

    public final String[] VALID_JOBS = new String[]{"Google", "Uber", "Meta", "Amazon"};
    public final String[] VALID_ADDRESSES = new String[]{"London", "New York", "Amsterdam"};

    private int age;
    private String name;
    private String job;
    private String address;


    public User(int age, String name, String job, String adress) throws IllegalArgumentException {


        if (age != 0) {
            this.age = age;
        } else throw new IllegalArgumentException();

        if (!Objects.equals(name, "")) {
            this.name = name;
        } else throw new IllegalArgumentException();

        if (validator(VALID_JOBS, job)) {
            this.job = job;
        } else throw new IllegalArgumentException();
        if (validator(VALID_ADDRESSES, adress)) {
            this.address = adress;
        } else throw new IllegalArgumentException();
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

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private boolean validator(String[] toValidate, String value) {
        boolean findValue = false;
        for (String s : toValidate) {
            if (s.equals(value)) {
                findValue = true;
            }
        }

        return findValue;
    }
}
