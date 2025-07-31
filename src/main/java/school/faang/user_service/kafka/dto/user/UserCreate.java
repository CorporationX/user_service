package school.faang.user_service.kafka.dto.user;

import school.faang.user_service.kafka.dto.country.CountryFilterDto;

public record UserCreate(
        long id,
        boolean active,
        CountryFilterDto country
) {
}
