package school.faang.user_service.event;

import lombok.Builder;

@Builder
public record ProfilePicEvent(long userId, String profilePicKey) {}
