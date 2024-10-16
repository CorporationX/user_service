package school.faang.user_service.dto.event;

public record FollowerEvent (
    Long followerId,
    Long followeeId
) {}
