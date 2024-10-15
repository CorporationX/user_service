package school.faang.user_service.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProfileViewEvent {
    private Long viewerId;
    private Long profileOwnerId;
}
