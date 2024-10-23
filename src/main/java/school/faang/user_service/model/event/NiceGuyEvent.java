package school.faang.user_service.model.event;

import lombok.Builder;

@Builder
public record NiceGuyEvent(
        Long authorId,
        Long receiverId,
        String text
) {
}
