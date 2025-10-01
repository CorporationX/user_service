package school.faang.user_service.dmitrrysprint1.hogwarts;

import java.util.Objects;

public class SpellEvent {

    private int id;

    private String eventType;

    private String action;


    public SpellEvent() {
    }

    public SpellEvent(int id, String eventType, String action) {
        this.id = id;
        this.eventType = eventType;
        this.action = action;
    }


    @Override
    public String toString() {
        return "SpellEvent{" +
                "id=" + id +
                ", eventType='" + eventType + '\'' +
                ", action='" + action + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpellEvent)) return false;
        SpellEvent that = (SpellEvent) o;
        return id == that.id && eventType.equals(that.eventType) && action.equals(that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventType, action);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
