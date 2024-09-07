package school.faang.user_service.service.mentorship;

import lombok.NonNull;
import school.faang.user_service.entity.User;

import java.util.Collection;

public interface MentorshipService {
    Collection<User> getMentees(@NonNull Long userId);

    Collection<User> getMentors(@NonNull Long userId);

    void deleteMentee(@NonNull Long menteeId, @NonNull Long mentorId);

    void deleteMentor(@NonNull Long menteeId, @NonNull Long mentorId);
}
