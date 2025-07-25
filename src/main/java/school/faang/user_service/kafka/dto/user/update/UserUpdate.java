package school.faang.user_service.kafka.dto.user.update;

public record UserUpdate(
        long id,
        String city,
        Integer experience,
        boolean active,
        String aboutMe,
        long countryId
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
