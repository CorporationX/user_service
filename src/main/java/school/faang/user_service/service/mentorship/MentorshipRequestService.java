package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.User;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.dto.event.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.dto.filter.RequestFilterDto;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.service.filter.MentorshipFilter;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.validation.MentorshipRequestValidator;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipFilter mentorshipFilter;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        mentorshipRequestValidator.validate(mentorshipRequest);
        return mentorshipRequestMapper.toDto(mentorshipRequestRepository.save(mentorshipRequest));
    }

    @Transactional
    public List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilter) {
        List<MentorshipRequest> allRequests = StreamSupport.stream(mentorshipRequestRepository.findAll().spliterator(),
                false).toList();

        return allRequests.stream()
                .map(mentorshipRequestMapper::toDto)
                .filter(requestDto -> mentorshipFilter.filter(requestFilter, requestDto))
                .toList();
    }

    @Transactional
    public MentorshipRequestDto acceptRequest(Long id) {
        MentorshipRequest request = findRequest(id);

        User requester = request.getRequester();
        User receiver = request.getReceiver();

        if (!requester.getMentors().contains(receiver)) {
            requester.getMentors().add(receiver);
            request.setStatus(RequestStatus.ACCEPTED);
            return mentorshipRequestMapper.toDto(request);
        }
        throw new IllegalArgumentException("Invalid request. Mentorship request is already accepted");
    }

    @Transactional
    public MentorshipRequestDto rejectRequest(Long id, RejectionDto rejection) {
        MentorshipRequest request = findRequest(id);
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());

        return mentorshipRequestMapper.toDto(request);
    }

    private MentorshipRequest findRequest(Long id) {
        return mentorshipRequestRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Invalid request. Mentorship request not found"));
    }
}