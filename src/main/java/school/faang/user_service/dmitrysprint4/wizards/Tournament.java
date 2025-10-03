package school.faang.user_service.dmitrysprint4.wizards;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class Tournament {

    public CompletableFuture<School> startTask(School shcool, Task task) throws InterruptedException {
        CompletableFuture<School> future = CompletableFuture.supplyAsync(() -> {
            List<Student> students = shcool.getTeam();
            shcool.getTotalPoints(students, task);
            log.info(shcool.getTotalPoints(students, task) + " " + shcool.getName());

            try {
                Thread.sleep(task.getDifficulty());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return shcool;
        });

        return future;
    }
}
