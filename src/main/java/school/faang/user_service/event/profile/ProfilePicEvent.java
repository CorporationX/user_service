package school.faang.user_service.event.profile;


import lombok.Builder;

import java.io.Serializable;

@Builder
public record ProfilePicEvent(long userId, String profilePicLink) implements Serializable {
}
