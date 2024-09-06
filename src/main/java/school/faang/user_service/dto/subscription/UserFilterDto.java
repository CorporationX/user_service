package school.faang.user_service.dto.subscription;

public record UserFilterDto(String namePattern, String aboutPattern, String emailPattern, String contactPattern,
                            String countryPattern, String cityPattern, String phonePattern, String skillPattern,
                            Integer experienceMin, Integer experienceMax, Integer page, Integer pageSize) {
}
