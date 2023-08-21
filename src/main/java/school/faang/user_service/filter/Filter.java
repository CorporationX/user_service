package school.faang.user_service.filter;


import java.util.stream.Stream;

public interface Filter<U, T> {
    boolean isApplicable(T filterDto);

    Stream<U> apply(Stream<U> stream, T filterDto);
}
