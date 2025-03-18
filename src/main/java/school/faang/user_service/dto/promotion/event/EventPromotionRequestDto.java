package school.faang.user_service.dto.promotion.event;

public record EventPromotionRequestDto(
        EventDto eventDto,
        EventPromotionDto eventPromotionDto
) {
}
