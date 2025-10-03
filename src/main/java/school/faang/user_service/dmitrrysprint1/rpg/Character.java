package school.faang.user_service.dmitrrysprint1.rpg;

public abstract class Character {
    private String name;
    private int power;
    private int agility;
    private int health = 100;
    private int intellect;


    public Character(String name) {
        this.name = name;
        this.power = 5;
        this.agility = 5;
        this.intellect = 5;

    }

    public Character(String name, int power, int agility, int intellect) {
        this.name = name;
        this.power = power;
        this.agility = agility;
        this.intellect = intellect;
    }


    public abstract int attack(CharacterOpponent opponent);

    public String getName() {
        return name;
    }
}
