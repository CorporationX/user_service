package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.filter.RequestFilter;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private static final String USER_NOT_EXIST = "User does not exist in the database";
    private static final String REQUEST_NOT_EXIST = "Request does not exist in the database";

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final UserRepository userRepository;
    private final List<RequestFilter> requestFilters;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        Long requesterId = mentorshipRequestDto.getRequesterId();
        Long receiverId = mentorshipRequestDto.getReceiverId();

        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);

        mentorshipRequestValidator.validateRequest(mentorshipRequest);


        mentorshipRequest.setRequester(userRepository
                .findById(requesterId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_EXIST))
        );
        mentorshipRequest.setReceiver(userRepository
                .findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_EXIST))
        );

        return mentorshipRequestMapper.toDto(mentorshipRequestRepository
                .create(
                        requesterId,
                        receiverId,
                        mentorshipRequestDto.getDescription()
                )
        );
    }

    @Transactional(readOnly = true)
    public List<MentorshipRequestDto> getRequests(RequestFilterDto filters) {
        Stream<MentorshipRequest> mentorshipRequests = mentorshipRequestRepository.findAll().stream();
        return mentorshipRequestMapper.toDto(
                requestFilters
                        .stream()
                        .filter(filter -> filter.isApplicable(filters))
                        .reduce(mentorshipRequests,
                                (stream, filter) -> filter.apply(stream, filters),
                                (s1, s2) -> s1)
                        .toList()
        );
    }

    @Transactional
    public void acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = getRequestById(id);

        mentorshipRequestValidator.validateRequest(mentorshipRequest);

        mentorshipRequest.getRequester().getMentors().add(mentorshipRequest.getReceiver());
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);

        mentorshipRequestRepository.save(mentorshipRequest);
    }

    @Transactional
    public void rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = getRequestById(id);

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.getReason());

        mentorshipRequestRepository.save(mentorshipRequest);
    }

    private MentorshipRequest getRequestById(long id) {
        return mentorshipRequestRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(REQUEST_NOT_EXIST));
    }
}
