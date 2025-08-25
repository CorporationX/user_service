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
    private static final String TYPE = "USER_UPDATE";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public long getId() {
        return id;
    }
}
