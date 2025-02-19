package school.faang.user_service.repository.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentorshipRequestRepositoryAdapter {
  private final MentorshipRequestRepository mentorshipRequestRepository;

  public MentorshipRequest findById(long id) {
    return mentorshipRequestRepository
        .findById(id)
        .orElseThrow(
            () -> {
              log.error("Mentorship request with ID {} does not exist", id);

              return new DataValidationException(
                  "Mentorship request with ID \"" + id + "\" does not exist");
            });
  }
}
