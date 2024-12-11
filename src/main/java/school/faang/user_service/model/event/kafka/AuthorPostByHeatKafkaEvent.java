package school.faang.user_service.model.event.kafka;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthorPostByHeatKafkaEvent {
    private Long postId;
    private Long authorId;
    private List<Long> lastCommentAuthors;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime publishedAt;
}