package school.faang.user_service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class FilterGroupRequest {

    private String groupOperator;
    private List<FilterRequest> filters;
    private List<FilterGroupRequest> children;

}
