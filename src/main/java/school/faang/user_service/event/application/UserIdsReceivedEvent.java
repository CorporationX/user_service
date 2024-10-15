package school.faang.user_service.event.application;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class UserIdsReceivedEvent extends ApplicationEvent {
    private final List<Long> userIds;

    public UserIdsReceivedEvent(Object source, List<Long> userIds) {
        super(source);
        this.userIds = userIds;
    }
}
