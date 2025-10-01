package school.faang.user_service.dmitrrysprint1.rpg;

public class Warrior extends Character {
    private int power;
    private int agility;
    private int health;
    private int intellect;


    public Warrior(String name) {
        super(name);
        this.power = 10;
        this.intellect = 3;
        this.agility = 5;
        this.health = 100;

    }

    @Override
    public int attack(CharacterOpponent opponent) {
        int damage = power;
        if (opponent.getHealth() - damage < 0) {
            opponent.setHealth(0);
            return opponent.getHealth();
        } else opponent.interactionWithDamage(damage);
        return opponent.getHealth() - damage;
    }


}
