package school.faang.user_service.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class UserFilterDto {
    private List<String> country;
    private List<String> cities;
    private List<Long> skillIds;
}
