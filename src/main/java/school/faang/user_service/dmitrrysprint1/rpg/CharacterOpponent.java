package school.faang.user_service.dmitrrysprint1.rpg;

public class CharacterOpponent {

    private int health;

    public CharacterOpponent() {
        this.health = 100;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public int interactionWithDamage(int damage) {
        setHealth(health - damage);
        return health - damage;
    }
}
