package school.faang.user_service.dmitrrysprint1.rpg;

public class Archer extends Character {

    private int power;
    private int agility;
    private int health = 100;
    private int intellect;

    public Archer(String name) {
        super(name);
        this.power = 3;
        this.intellect = 5;
        this.agility = 10;
        this.health = 100;
    }


    @Override
    public int attack(CharacterOpponent opponent) {
        int damage = agility / 2;
        if (opponent.getHealth() - damage < 0) {
            opponent.setHealth(0);
            return opponent.getHealth();
        } else opponent.interactionWithDamage(damage);
        return opponent.getHealth() - damage;

    }
}
