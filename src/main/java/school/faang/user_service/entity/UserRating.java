package school.faang.user_service.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_rating", indexes = {@Index(name = "idx_user_rating_score", columnList = "score")})
@Document(indexName = "user_ratings", createIndex = false)
public class UserRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Field(type = FieldType.Long, name = "user_id")
    @Column(unique = true, nullable = false)
    private Long userId;

    @Field(type = FieldType.Text, name = "user_name")
    private String username;

    @Column(nullable = false)
    private Integer score;

    @Field(type = FieldType.Integer, name = "score_with_penalty")
    @Column(nullable = false)
    private Integer scoreWithPenalty;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, name = "last_active_time")
    @Column(nullable = false)
    private LocalDateTime lastActiveTime;
}
