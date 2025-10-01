package school.faang.user_service.dmitrrysprint1.hogwarts;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class HogwartsSpells {

    private HashMap<Integer, SpellEvent> spellById;

    private HashMap<String, SpellEvent> spellsByType;
    private String[] tipesOfSpells =new String[]{"kill", "paralize", "summon", "create"};

    public HogwartsSpells() {
    }

    public HogwartsSpells(HashMap<Integer, SpellEvent> spellById, HashMap<String, SpellEvent> spellsByType) {
        this.spellById = spellById;
        this.spellsByType = spellsByType;
    }

    public SpellEvent addSpellEvent(String eventType, String actionDescription) {
        SpellEvent spellEvent = new SpellEvent();
        spellEvent.setId(generateId());
        spellEvent.setEventType(randomSpellType());

        return null;
    } //создает объект класса SpellEvent, автоматически генерирует уникальный ID, добавляет событие в обе HashMap;


    public SpellEvent getSpellEventById(int id) {
        return null;
    } //возвращает событие заклинания по его ID;

    public List<SpellEvent> getSpellEventsByType(String eventType) {
        return null;
    } //возвращает список событий заклинаний по типу;

    public void deleteSpellEvent(int id) {

    } //удаляет событие заклинания по его ID из обеих HashMap;

    public void printAllSpellEvents() {

    }// выводит информацию о всех событиях заклинаний в консоль, используя обход элементов Entry в HashMap spellById. Выведите ID, тип и данные каждого события.

    public Integer generateId() {
        Integer generatedId = new Random().nextInt();
        while(spellById.containsKey(generatedId)){
            generatedId = new Random().nextInt();
        }
        return generatedId;
    }



    public boolean checkID(int id) {
        if (spellById.containsKey(id)) return true;

        else return false;
    }
    public String randomSpellType(){
        int spellTypeNumber = new Random().nextInt(4);
        return tipesOfSpells[spellTypeNumber];
    }
}

