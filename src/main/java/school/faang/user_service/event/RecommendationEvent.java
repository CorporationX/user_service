package school.faang.user_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationEvent extends Event{

    private long authorId;

    private long receiverId;

    private String recommendationText;

    private LocalDateTime sendAt;
}
