package school.faang.user_service.dmitrysprint4.wizards;

import java.util.List;

public class School {
    private String name;

    private List<Student> team;

    public long getTotalPoints(List<Student> students, Task task) {
        students.stream().forEach(s -> s.setPoints(s.getPoints() + task.getReward()));

        return students.stream().mapToInt((Student::getPoints)).sum();
    }

    public School(String name, List<Student> team) {
        this.name = name;
        this.team = team;
    }

    public long getTaskResultPoints() {

        return team.stream().mapToInt((Student::getPoints)).sum();
    }

    public List<Student> getTeam() {
        return team;
    }

    public String getName() {
        return name;
    }
}
