package school.faang.user_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.enums.FilterOperation;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterRequest {

    private String field;
    private FilterOperation operation;
    private Object value;
    private Object valueTo;
    private String joinType;

}
