package school.faang.user_service.kafka.dto.user.update;

import school.faang.user_service.kafka.dto.country.CountryFilterDto;

public record UserUpdate(
        long id,
        String city,
        Integer experience,
        boolean active,
        String headline,
        String aboutMe,
        CountryFilterDto country
) implements UserUpdateEvent {
    @Override
    public String getType() {
        return "USER_UPDATE";
    }

    @Override
    public long getId() {
        return id;
    }
}
