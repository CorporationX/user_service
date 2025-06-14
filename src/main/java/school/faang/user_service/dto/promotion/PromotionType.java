package school.faang.user_service.dto.promotion;

public enum PromotionType {
    PROFILE("profileCreator"),
    EVENT("eventCreator");

    private final String value;

    PromotionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
