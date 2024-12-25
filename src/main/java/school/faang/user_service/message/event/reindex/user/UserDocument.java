package school.faang.user_service.message.event.reindex.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.List;

@Data
@Document(indexName = "user_indexing_topic")
@Setting(settingPath = "elasticsearch/settings.json")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
public class UserDocument extends BaseDocument {

    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "standard"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
            })
    private String username;

    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "standard"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
            })
    private String country;

    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "standard"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
            })
    private String city;

    @Field(type = FieldType.Integer)
    private Integer experience;


    @Field(type = FieldType.Nested)
    private List<GoalNested> goals;

    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "standard"),
    otherFields = {
            @InnerField(suffix = "keyword", type = FieldType.Keyword),
    })
    private List<String> skillNames;

    @Field(type = FieldType.Nested)
    private List<EventNested> events;

    @Field(type = FieldType.Double)
    private Double averageRating;

}
