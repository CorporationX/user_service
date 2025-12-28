package school.faang.user_service.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostCreatedEvent {
    private long id;
    private String content;
    private long projectId;
    private long authorId;
    private long createdAt;
}
