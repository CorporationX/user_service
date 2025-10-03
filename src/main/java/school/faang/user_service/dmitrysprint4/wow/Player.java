package school.faang.user_service.dmitrysprint4.wow;

public class Player {
    private String name;
    private int level;
    private long expirience;

    public Player(String name, int level, long expirience) {
        this.name = name;
        this.level = level;
        this.expirience = expirience;
    }

    public Player() {
    }


    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public long getExpirience() {
        return expirience;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setExpirience(long expirience) {
        this.expirience = expirience;
    }
}
