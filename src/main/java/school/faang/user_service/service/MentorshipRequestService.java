package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDtoForRequest;
import school.faang.user_service.dto.MentorshipRequestDtoForResponse;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.exception.RequestException;
import school.faang.user_service.filter.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<MentorshipRequestFilter> filters;
    private static final int PAUSE_TIME = 3;

    public MentorshipRequestDtoForResponse requestMentorship(MentorshipRequestDtoForRequest mentorshipRequestDtoForRequest) {

        validatedRequesterAndReceiverIds(mentorshipRequestDtoForRequest);
        validatedCreateTime(mentorshipRequestDtoForRequest);

        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.create(
                mentorshipRequestDtoForRequest.getRequesterId(),
                mentorshipRequestDtoForRequest.getReceiverId(),
                mentorshipRequestDtoForRequest.getDescription());

        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    private void validatedRequesterAndReceiverIds(MentorshipRequestDtoForRequest mentorshipRequestDtoForRequest) {
        if (mentorshipRequestDtoForRequest.getRequesterId().equals(mentorshipRequestDtoForRequest.getReceiverId())) {
            throw new RequestException(ErrorMessage.REQUEST_TO_YOURSELF);
        }

        if (!userRepository.existsById(mentorshipRequestDtoForRequest.getRequesterId()) ||
                !userRepository.existsById(mentorshipRequestDtoForRequest.getReceiverId())) {
            throw new RequestException(ErrorMessage.USER_DOES_NOT_EXIST);
        }
    }

    private void validatedCreateTime(MentorshipRequestDtoForRequest mentorshipRequestDtoForRequest) {
        mentorshipRequestRepository
                .findLatestRequest(mentorshipRequestDtoForRequest.getRequesterId(), mentorshipRequestDtoForRequest.getReceiverId())
                .ifPresent(request -> {
                            LocalDateTime currentDate = LocalDateTime.now();
                            LocalDateTime suitLastDate = currentDate.minusMonths(PAUSE_TIME);
                            if (request.getCreatedAt().isAfter(suitLastDate)) {
                                throw new RequestException(ErrorMessage.EARLY_REQUEST);
                            }
                        });
    }

    public List<MentorshipRequestDtoForResponse> getRequests(RequestFilterDto filterDto) {
        Stream<MentorshipRequest> requests = mentorshipRequestRepository.findAll().stream();
        return filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(requests,
                        (requests1, filter) -> filter.apply(requests1, filterDto),
                        Stream::concat)
                .map(mentorshipRequestMapper::toDto)
                .toList();
    }

    public void acceptRequest(long id) {
        MentorshipRequest request = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new RequestException(ErrorMessage.REQUEST_DOES_NOT_EXIST));
        List<User> mentors = request.getRequester().getMentors();
        if (mentors.contains(request.getReceiver())) {
            throw new RequestException(ErrorMessage.ALREADY_MENTOR);
        }
        mentors.add(request.getReceiver());
        request.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(request);
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest request = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new RequestException(ErrorMessage.REQUEST_DOES_NOT_EXIST));
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());
        mentorshipRequestRepository.save(request);
    }
}
