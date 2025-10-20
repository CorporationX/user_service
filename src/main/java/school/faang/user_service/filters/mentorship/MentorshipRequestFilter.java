package school.faang.user_service.filters.mentorship;

import org.jetbrains.annotations.NotNull;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.user.MentorshipRequest;

import java.util.stream.Stream;

@Validated
public interface MentorshipRequestFilter {
    boolean isApplicable(@NotNull MentorshipRequestFilterDto mentorshipRequestFilterDto);

    Stream<MentorshipRequest> apply(
            @NotNull Stream<MentorshipRequest> mentorshipRequest,
            MentorshipRequestFilterDto mentorshipRequestFilterDto);
}