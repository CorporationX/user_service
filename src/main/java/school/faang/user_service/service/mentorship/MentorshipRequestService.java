package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.mentorship.MentorshipRequestException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class MentorshipRequestService {

    private MentorshipRequestRepository mentorshipRequestRepository;
    private MentorshipRequestMapper mentorshipRequestMapper;

    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        Optional<MentorshipRequest> byId = mentorshipRequestRepository.findById(id);
        if (byId.isPresent()) {
            MentorshipRequest mentorshipRequest = byId.get();
            mentorshipRequest.setStatus(RequestStatus.REJECTED);
            mentorshipRequest.setRejectionReason(rejection.getReason());
            mentorshipRequestRepository.save(mentorshipRequest);
            return mentorshipRequestMapper.toDTO(mentorshipRequest);
        } else {
            throw new MentorshipRequestException("There is no mentorship request with this id");
        }
    }
}