package school.faang.user_service.service.mentorship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class MentorshipRequestService {
    MentorshipRequestRepository mentorshipRequestRepository;
    UserRepository userRepository;
    MentorshipRequestMapper mentorshipRequestMapper;
    @Autowired
    public MentorshipRequestService(MentorshipRequestRepository mentorshipRequestRepository) {
        this.mentorshipRequestRepository = mentorshipRequestRepository;
    }

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        boolean isMoreThanThreeMonths = LocalDateTime.now().isAfter(mentorshipRequestRepository.findLatestRequest(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId()).get().getUpdatedAt().plusMonths(3));
        boolean isRecieverExists = userRepository.existsById(mentorshipRequestDto.getReceiverId());
        boolean isRequesterExists = userRepository.existsById(mentorshipRequestDto.getRequesterId());
        boolean isNotRequestToYourself = mentorshipRequestDto.getRequesterId() != mentorshipRequestDto.getReceiverId();

        if (!isMoreThanThreeMonths && !isRecieverExists && !isRequesterExists && !isNotRequestToYourself) {
            throw new IllegalArgumentException("Error validation");
        }

        mentorshipRequestRepository.create(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId(), mentorshipRequestDto.getDescription());

        MentorshipRequest mentorshipRequestEntity = mentorshipRequestMapper.MentorshipRequestToEntity(mentorshipRequestDto);
        mentorshipRequestEntity = mentorshipRequestRepository.save(mentorshipRequestEntity);
        return mentorshipRequestMapper.toMentorshipRequestDto(mentorshipRequestEntity);
    }

    public RejectionDto rejectRequest(long id, RejectionDto rejection) {
        if(!(mentorshipRequestRepository.existsById(id))){
            throw new IllegalArgumentException("There is no request in db with this ID");
        }

        mentorshipRequestRepository.findById(id).get().setStatus(RequestStatus.REJECTED);
        mentorshipRequestRepository.findById(id).get().setRejectionReason(rejection.getReason());

        MentorshipRequest mentorshipRequestEntity = mentorshipRequestMapper.RejectionDtoToEntity(rejection);
        mentorshipRequestEntity = mentorshipRequestRepository.save(mentorshipRequestEntity);
        return mentorshipRequestMapper.toRejectionDto(mentorshipRequestEntity);
    }
}
