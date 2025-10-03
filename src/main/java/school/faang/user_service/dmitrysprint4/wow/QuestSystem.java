package school.faang.user_service.dmitrysprint4.wow;

import java.util.concurrent.CompletableFuture;

public class QuestSystem {

    public CompletableFuture<Player> startQuest(Player player, Quest quest) {
        CompletableFuture<Player> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(quest.getDifficulty());
                player.setExpirience(player.getExpirience() + quest.getReward());


            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            return player;
        });
        return future;
    }
}
