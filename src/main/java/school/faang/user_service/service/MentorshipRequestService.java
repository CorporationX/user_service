package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.MentorshipAlreadyExistsException;
import school.faang.user_service.filter.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final UserRepository userRepository;
    private final List<MentorshipRequestFilter> filters;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {

        validateMentorshipRequest(mentorshipRequestDto);
        MentorshipRequest request = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        MentorshipRequest response = mentorshipRequestRepository.save(request);
        return mentorshipRequestMapper.toDto(response);
    }

    private void validateMentorshipRequest(MentorshipRequestDto mentorshipRequestDto) {
        Long requesterId = mentorshipRequestDto.getRequesterId();
        Long receiverId = mentorshipRequestDto.getReceiverId();
        if (requesterId == null || receiverId == null
                || !userRepository.existsById(requesterId) || !userRepository.existsById(receiverId)) {
            log.info("Both users need to be registered or IDs cannot be null");
            throw new IllegalArgumentException("User is not existed or is null");
        }

        mentorshipRequestRepository.findLatestRequest(mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId())
                .ifPresent(mentorshipRequest -> {
                    LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
                    LocalDateTime createdAt = mentorshipRequest.getCreatedAt();
                    if (createdAt.isAfter(threeMonthsAgo) || createdAt.equals(threeMonthsAgo)) {
                        log.info("The request was made in the last 3 months, try later");
                        throw new IllegalArgumentException("The request was made in the last 3 months");
                    }
                });

        if (mentorshipRequestDto.getRequesterId() == mentorshipRequestDto.getReceiverId()) {
            log.info("The user can not be requester and receiver at the same time");
            throw new IllegalArgumentException("Requester user and receiver user is equal");
        }
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filterDto) {
        Iterable<MentorshipRequest> mentorshipRequestIterable = mentorshipRequestRepository.findAll();
        Stream<MentorshipRequest> requestStream = StreamSupport
                .stream(mentorshipRequestIterable.spliterator(), false);

        Stream<MentorshipRequest> filteredStream = filters.stream()
                .filter(f -> f.isApplyable(filterDto))
                .reduce(
                        requestStream,
                        (stream, filter) ->
                                filter.filter(stream, filterDto),
                        (s1, s2) -> s1
                );

        return filteredStream
                .map(mentorshipRequestMapper::toDto)
                .toList();
    }

    @Transactional
    public void acceptRequest(long mentorshipRequestId) {
        MentorshipRequest request = mentorshipRequestRepository.findById(mentorshipRequestId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Mentorship request with id %d not found", mentorshipRequestId)));

        User requester = request.getRequester();
        User futureMentor = request.getReceiver();

        if (!requester.getMentors().contains(futureMentor)) {
            requester.getMentors().add(futureMentor);
            futureMentor.getMentees().add(requester);
            request.setStatus(RequestStatus.ACCEPTED);

            userRepository.save(requester);
            userRepository.save(futureMentor);
            mentorshipRequestRepository.save(request);
        } else {
            throw new MentorshipAlreadyExistsException(
                    String.format("User %d is already a mentor for user %d",
                            futureMentor.getId(), requester.getId()));
        }
    }

    @Transactional
    public void rejectRequest(long mentorshipRequestId, RejectionDto rejection) {
        MentorshipRequest request = mentorshipRequestRepository.findById(mentorshipRequestId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Mentorship request with id %d not found", mentorshipRequestId)));

        request = mentorshipRequestMapper.updateRequestFromDto(rejection, request);
        mentorshipRequestRepository.save(request);
    }
}
