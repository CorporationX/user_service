package school.faang.user_service.dmitrysprint4.wizards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.util.List;


@Slf4j
@Data
@AllArgsConstructor
public class School {
    private String name;

    private List<Student> team;

    public long getTotalPoints(List<Student> students, Task task) {

        students.stream().forEach(s -> s.setPoints(s.getPoints() + task.getReward()));

        return students.stream().mapToInt((Student::getPoints)).sum();
    }

    public long getTaskResultPoints() {

        return team.stream().mapToInt((Student::getPoints)).sum();
    }

}
