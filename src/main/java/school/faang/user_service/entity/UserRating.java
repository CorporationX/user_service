package school.faang.user_service.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("user_rating")
@Document(indexName = "user_ratings", createIndex = false)
public class UserRating {
    @Field(type = FieldType.Keyword, name = "id")
    @PrimaryKey
    private UUID id;

    @Field(type = FieldType.Text, name = "user_name")
    @Column("username")
    private String username;

    @Column("user_id")
    @Field(type = FieldType.Long, name = "user_id")
    private Long userId;

    @Field(type = FieldType.Integer, name = "score")
    private Integer score ;

    @Column("score_with_penalty")
    @Field(type = FieldType.Integer, name = "score_with_penalty")
    private Integer scoreWithPenalty;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, name = "last_active_time")
    private LocalDateTime lastActiveTime;
}
