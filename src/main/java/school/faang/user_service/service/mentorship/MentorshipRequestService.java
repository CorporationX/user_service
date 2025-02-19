package school.faang.user_service.service.mentorship;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestResponseDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestResponseMapper;
import school.faang.user_service.repository.adapter.MentorshipRequestRepositoryAdapter;
import school.faang.user_service.repository.adapter.UserRepositoryAdapter;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorshipRequestService {

  private static final int NUMBER_OF_MONTH_THAT_MUST_PASS = 3;

  private final MentorshipRequestRepository mentorshipRequestRepository;
  private final MentorshipRequestRepositoryAdapter mentorshipRequestRepositoryAdapter;
  private final UserRepositoryAdapter userRepositoryAdapter;
  private final List<MentorshipRequestFilter> mentorshipRequestFilters;
  private final MentorshipRequestResponseMapper mentorshipRequestResponseMapper;

  @Transactional
  public MentorshipRequestResponseDto requestMentorship(
      MentorshipRequestRequestDto mentorshipRequestRequestDto) {
    Long requesterId = mentorshipRequestRequestDto.requesterId();
    Long receiverId = mentorshipRequestRequestDto.receiverId();

    validateRequestMentorship(requesterId, receiverId);

    mentorshipRequestRepository.create(
        requesterId, receiverId, mentorshipRequestRequestDto.description());

    MentorshipRequest createdMentorshipRequest =
        mentorshipRequestRepository
            .findLatestRequest(requesterId, receiverId)
            .orElseThrow(
                () -> {
                  log.error("An error occurred while saving the mentorship request");

                  return new DataValidationException(
                      "An error occurred while saving the mentorship request");
                });

    log.info(
        "Mentorship request from user with ID {} to user with ID {} created",
        requesterId,
        receiverId);

    return mentorshipRequestResponseMapper.toDto(createdMentorshipRequest);
  }

  public List<MentorshipRequestResponseDto> getRequests(MentorshipRequestFilterDto filter) {
    Stream<MentorshipRequest> mentorshipRequests = mentorshipRequestRepository.findAll().stream();

    for (MentorshipRequestFilter mentorshipRequestFilter : mentorshipRequestFilters) {
      if (mentorshipRequestFilter.isApplicable(filter)) {
        mentorshipRequests = mentorshipRequestFilter.apply(mentorshipRequests, filter);
      }
    }

    List<MentorshipRequest> mentorshipRequestsList =
        mentorshipRequests
            .peek(
                mentorshipRequest ->
                    log.info("Mentorship request with ID {} found", mentorshipRequest.getId()))
            .toList();

    return mentorshipRequestResponseMapper.toDtoList(mentorshipRequestsList);
  }

  @Transactional
  public MentorshipRequestResponseDto acceptRequest(long id) {
    MentorshipRequest mentorshipRequest = mentorshipRequestRepositoryAdapter.findById(id);

    User requester = mentorshipRequest.getRequester();
    User receiver = mentorshipRequest.getReceiver();

    if (requester.getMentors().contains(receiver)) {
      Long receiverId = receiver.getId();
      Long requesterId = requester.getId();

      log.error(
          "The user with ID {} is already a mentor for the user with ID {}",
          receiverId,
          requesterId);

      throw new DataValidationException(
          "The user with ID "
              + receiverId
              + " is already a mentor for the user with ID "
              + requesterId);
    }

    requester.getMentors().add(receiver);
    mentorshipRequest.setStatus(RequestStatus.ACCEPTED);

    log.info("Mentorship request with ID {} accepted", mentorshipRequest.getId());

    return mentorshipRequestResponseMapper.toDto(mentorshipRequest);
  }

  @Transactional
  public MentorshipRequestResponseDto rejectRequest(long id, RejectionDto rejection) {
    MentorshipRequest mentorshipRequest = mentorshipRequestRepositoryAdapter.findById(id);

    mentorshipRequest.setStatus(RequestStatus.REJECTED);
    mentorshipRequest.setRejectionReason(rejection.reason());

    log.info("Mentorship request with ID {} rejected", mentorshipRequest.getId());

    return mentorshipRequestResponseMapper.toDto(mentorshipRequest);
  }

  private void validateRequestMentorship(Long requesterId, Long receiverId) {
    if (!userRepositoryAdapter.existsById(requesterId)) {
      log.error("User with ID {} does not exist", requesterId);

      throw new DataValidationException("User with ID \"" + requesterId + "\" does not exist");
    }

    if (!userRepositoryAdapter.existsById(receiverId)) {
      log.error("User with ID {} does not exist", receiverId);

      throw new DataValidationException("User with ID \"" + receiverId + "\" does not exist");
    }

    if (Objects.equals(requesterId, receiverId)) {
      log.error("User cannot send a mentorship request to himself");

      throw new DataValidationException("User cannot send a mentorship request to himself");
    }

    Optional<MentorshipRequest> optionalLatestMentorshipRequest =
        mentorshipRequestRepository.findLatestRequest(requesterId, receiverId);

    if (optionalLatestMentorshipRequest.isPresent()) {
      MentorshipRequest latestMentorshipRequest = optionalLatestMentorshipRequest.get();

      if (LocalDateTime.now().getMonth().getValue()
          < latestMentorshipRequest.getCreatedAt().getMonth().getValue()
              + NUMBER_OF_MONTH_THAT_MUST_PASS) {
        log.error(
            "Mentorship request can be made once every {} months", NUMBER_OF_MONTH_THAT_MUST_PASS);

        throw new DataValidationException(
            "Mentorship request can be made once every "
                + NUMBER_OF_MONTH_THAT_MUST_PASS
                + " months");
      }
    }
  }
}
