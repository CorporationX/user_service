package school.faang.user_service.service;

public class OnlyForTests {

    public String doSomething(String param) {
        return switch (param) {
            case "test1" -> "test1";
            case "test2" -> "test2";
            case "test3" -> "test3";
            default -> "unknown";
        };
    }
}
