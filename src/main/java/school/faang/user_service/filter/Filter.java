package school.faang.user_service.filter;

import java.util.stream.Stream;

public interface Filter<T, F> {

    boolean isApplicable(F filter);

    void apply(Stream<T> goalInvitationDto, F filter);
}
