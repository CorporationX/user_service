package school.faang.user_service.event;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;


@Builder
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfilePicEvent extends Event {

    long userId;

    String avatarUrl;
}
