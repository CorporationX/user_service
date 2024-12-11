package school.faang.user_service.model.event.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthorCommentKafkaEvent {
    private Long commentAuthorId;
}
