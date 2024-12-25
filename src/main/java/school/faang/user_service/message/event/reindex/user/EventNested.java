package school.faang.user_service.message.event.reindex.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
public class EventNested {

    @Field(type = FieldType.Keyword)
    private Long id;

    @Field(type = FieldType.Text)
    private String tittle;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    @Field(type = FieldType.Text)
    private String location;

    @Field(type = FieldType.Integer)
    private Integer maxAttendees;

    @Field(type = FieldType.Keyword)
    private String usernameOwner;

    @Field(type = FieldType.Keyword)
    private EventType eventType;

    @Field(type = FieldType.Keyword)
    private EventStatus status;
}
