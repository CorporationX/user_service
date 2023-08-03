package school.faang.user_service.listener;

import school.faang.user_service.entity.event.Event;

public class EventActivity implements Activity{

    private final Long rating = 5L;
    @Override
    public Long getUserId(Object object) {
        Event event = (Event) object;
        return event.getOwner().getId();
    }

    @Override
    public Long getRating(Object object) {
        return rating;
    }

    @Override
    public Event getEntity() {
        return new Event();
    }

}