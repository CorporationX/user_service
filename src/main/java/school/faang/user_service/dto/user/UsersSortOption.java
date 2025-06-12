package school.faang.user_service.dto.user;

public enum UsersSortOption {
    ID("id"),
    EXPERIENCE("experience");

    private String value;

    UsersSortOption(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
