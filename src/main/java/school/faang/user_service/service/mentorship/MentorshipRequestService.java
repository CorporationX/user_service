package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.handler.exception.EntityNotFoundException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.filtres.RequestFilter;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<RequestFilter> requestFilters;

    @Transactional
    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        long requesterId = mentorshipRequestDto.getIdRequester();
        long receiverId = mentorshipRequestDto.getIdReceiver();
        mentorshipRequestValidator.requestMentorshipValidationUserIds(requesterId, receiverId);
        mentorshipRequestValidator.requestMentorshipValidationLatestRequest(requesterId, receiverId);
        mentorshipRequestRepository.create(requesterId, receiverId, mentorshipRequestDto.getDescription());
    }

    @Transactional(readOnly = true)
    public List<MentorshipRequestDto> getRequest(RequestFilterDto requestFilterDto) {
        return requestFilters.stream()
                .filter(requestFilter -> requestFilter.isApplicable(requestFilterDto))
                .flatMap(requestFilter -> requestFilter.apply(mentorshipRequestRepository.findAll().stream()
                        , requestFilterDto))
                .map(mentorshipRequestMapper::toDto).toList();
    }

    @Transactional
    public MentorshipRequestDto acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = getMentorshipRequest(id);
        User requester = mentorshipRequest.getRequester();
        User receiver = mentorshipRequest.getReceiver();
        List<User> receiverMentees = receiver.getMentees();
        if (receiverMentees.contains(requester)) {
            throw new IllegalArgumentException("The requester is already on the mentees list for this mentor");
        }
        receiverMentees.add(requester);
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(mentorshipRequest);
        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    @Transactional
    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = getMentorshipRequest(id);
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.getReason());
        mentorshipRequestRepository.save(mentorshipRequest);
        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    private MentorshipRequest getMentorshipRequest(long id) {
        return mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no such query in the database"));
    }
}