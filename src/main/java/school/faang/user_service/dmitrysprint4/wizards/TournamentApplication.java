package school.faang.user_service.dmitrysprint4.wizards;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TournamentApplication {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Tournament tournament = new Tournament();

        // Создание школ
        List<Student> hogwartsTeam = List.of(new Student("Harry", 5, 0), new Student("Hermione", 5, 0));
        List<Student> beauxbatonsTeam = List.of(new Student("Fleur", 6, 0), new Student("Gabrielle", 6, 0));
        School hogwarts = new School("Hogwarts", hogwartsTeam);
        School beauxbatons = new School("Beauxbatons", beauxbatonsTeam);

        // Создание заданий
        Task task1 = new Task("Triwizard Tournament", 10, 100);
        Task task2 = new Task("Yule Ball Preparations", 5, 50);

        // Запуск заданий для школ
        CompletableFuture<School> hogwartsTask = tournament.startTask(hogwarts, task1);
        CompletableFuture<School> beauxbatonsTask = tournament.startTask(beauxbatons, task2);
        hogwartsTask.get();
        beauxbatonsTask.get();

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(hogwartsTask, beauxbatonsTask);
        // Обработка результатов всех заданий и определение победителя

        allTasks.get();
        School hogwartsResult = hogwartsTask.join();
        School beauxbatonsResult = beauxbatonsTask.join();
        List<School> tournamentSchools = List.of(hogwartsResult, beauxbatonsResult);
        Optional<School> winnerShool = tournamentSchools.stream().max((v, v1) -> (int) v.getTaskResultPoints());
        School tournamentWinner = winnerShool.get();
        System.out.println(tournamentWinner.getName());


    }
}
