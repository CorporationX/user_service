package school.faang.user_service.dto.promotion.user;

public record UserPromotionRequestDto(
        UserDto userDto,
        UserPromotionDto userPromotionDto
) {
}
