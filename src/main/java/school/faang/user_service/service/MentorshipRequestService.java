package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
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
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<MentorshipRequestFilter> filters;
    private static final int PAUSE_TIME = 3;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {

        if (!userRepository.existsById(mentorshipRequestDto.getRequesterId())) {
            throw new RequestException(ErrorMessage.REQUESTER_DOES_NOT_EXIST);
        }
        if (!userRepository.existsById(mentorshipRequestDto.getReceiverId())) {
            throw new RequestException(ErrorMessage.RECEIVER_DOES_NOT_EXIST);
        }
        if (mentorshipRequestDto.getRequesterId().equals(mentorshipRequestDto.getReceiverId())) {
            throw new RequestException(ErrorMessage.REQUEST_TO_YOURSELF);
        }

        mentorshipRequestRepository
                .findLatestRequest(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId())
                .ifPresent(request -> {
                            LocalDateTime currentDate = LocalDateTime.now();
                            LocalDateTime suitLastDate = currentDate.minusMonths(PAUSE_TIME);
                            if (request.getCreatedAt().isAfter(suitLastDate)) {
                                throw new RequestException(ErrorMessage.EARLY_REQUEST);
                            }
                        });

        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.create(
                mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId(),
                mentorshipRequestDto.getDescription());

        Optional<User> requester = userRepository.findById(mentorshipRequestDto.getRequesterId());
        Optional<User> receiver = userRepository.findById(mentorshipRequestDto.getReceiverId());
        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filterDto) {
        Stream<MentorshipRequest> requests = mentorshipRequestRepository.findAll().stream();
        return filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .flatMap(filter -> filter.apply(requests, filterDto))
                .map(mentorshipRequestMapper::toDto)
                .toList();
    }

    public void acceptRequest(long id) {
        if (!mentorshipRequestRepository.existsById(id)) {
            throw new RequestException(ErrorMessage.REQUEST_DOES_NOT_EXIST);
        }
        MentorshipRequest request = mentorshipRequestRepository.getReferenceById(id);
        if (request.getRequester().getMentors().contains(mentorshipRequestRepository.getReferenceById(id).getReceiver())) {
            throw new RequestException(ErrorMessage.ALREADY_MENTOR);
        }
        request.getRequester().getMentors().add(request.getReceiver());
        request.setStatus(RequestStatus.ACCEPTED);
    }


    public void rejectRequest(long id, RejectionDto rejection) {
        if (!mentorshipRequestRepository.existsById(id)) {
            throw new RequestException(ErrorMessage.REQUEST_DOES_NOT_EXIST);
        }
        mentorshipRequestRepository.getReferenceById(id).setStatus(RequestStatus.REJECTED);
        mentorshipRequestRepository.getReferenceById(id).setRejectionReason(rejection.getReason());
    }
}
