package school.faang.user_service.dto.country;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CountryDto {
    private long id;
    private String title;
}
