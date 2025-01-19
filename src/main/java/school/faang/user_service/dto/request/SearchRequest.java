package school.faang.user_service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchRequest {

    private FilterGroupRequest rootGroup;
    private SortRequest sort;

}
