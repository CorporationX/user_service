package school.faang.user_service.dto.post;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PostToFeedEvent {
    private long postId;
    private long authorId;
    private List<Long> subscriberIds;
    private long createdAt;
}