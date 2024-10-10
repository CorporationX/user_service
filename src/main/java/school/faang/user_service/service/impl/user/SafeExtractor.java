package school.faang.user_service.service.impl.user;

import java.util.function.Function;

public class SafeExtractor {
    private SafeExtractor() {
    }

    public static <T, R> R extract(T object, Function<T, R> extractor) {
        try {
            return extractor.apply(object);
        } catch (NullPointerException e) {
            return null;
        }
    }
}
