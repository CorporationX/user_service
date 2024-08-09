package school.faang.user_service.service.user.extractor;

import java.util.Optional;
import java.util.function.Function;

public final class SafeExtractor {
    private SafeExtractor() {

    }

    public static <T, R> R extract(T object, Function<T, R> extractor) {
        return Optional.ofNullable(object)
                .map(extractor)
                .orElse(null);
    }
}
