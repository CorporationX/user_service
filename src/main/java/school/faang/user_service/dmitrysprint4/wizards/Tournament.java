package school.faang.user_service.dmitrysprint4.wizards;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Tournament {

    public CompletableFuture<School> startTask(School shcool, Task task) throws InterruptedException {
        CompletableFuture<School> future = CompletableFuture.supplyAsync(() ->
        {
            List<Student> students = shcool.getTeam();
            shcool.getTotalPoints(students, task);
            System.out.println(shcool.getTotalPoints(students, task) + " " + shcool.getName());
            System.out.println("done");

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
