package school.faang.user_service.dmitrysprint4.wow;

public class Quest {
    private String name;

    private int difficulty;


    private int reward;

    public Quest(String name, int difficulty, int reward) {
        this.name = name;
        this.difficulty = difficulty;
        this.reward = reward;
    }


    public Quest() {
    }

    public String getName() {
        return name;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getReward() {
        return reward;
    }
}
