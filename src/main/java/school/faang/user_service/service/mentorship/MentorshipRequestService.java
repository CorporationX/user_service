package school.faang.user_service.service.mentorship;

import lombok.Data;
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
import java.util.*;
@Data
@Component
public class MentorshipRequestService {
    private MentorshipRequestRepository mentorshipRequestRepository;
    private UserRepository userRepository;
    private MentorshipRequestMapper mentorshipRequestMapper;
    private Map<List<Long>, List<Long>> mentorsAndUsers = new HashMap<>();
    @Autowired
    public MentorshipRequestService(MentorshipRequestRepository mentorshipRequestRepository) {
        this.mentorshipRequestRepository = mentorshipRequestRepository;
    }

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        boolean isMoreThanThreeMonths = LocalDateTime.now().isAfter(mentorshipRequestRepository.findLatestRequest(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId()).get().getUpdatedAt().plusMonths(3));
        boolean isRecieverExists = userRepository.existsById(mentorshipRequestDto.getReceiverId());
        boolean isRequesterExists = userRepository.existsById(mentorshipRequestDto.getRequesterId());
        boolean isNotRequestToYourself = mentorshipRequestDto.getRequesterId() != mentorshipRequestDto.getReceiverId();

        if (!isMoreThanThreeMonths) {
            throw new IllegalArgumentException("Less than 3 months have passed since last request");
        } else if (!isRecieverExists) {
            throw new IllegalArgumentException("There are no this receiver in data base");
        } else if (!isRequesterExists) {
            throw new IllegalArgumentException("There are no this requester in data base");
        } else if (!isNotRequestToYourself) {
            throw new IllegalArgumentException("You can not send a request to yourself");
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

    public void acceptRequest(long id) {
        long mentorId = mentorshipRequestRepository.findById(id).get().getReceiver().getId();
        long senderId = mentorshipRequestRepository.findById(id).get().getRequester().getId();

        if(!mentorshipRequestRepository.existsById(id)) {
            throw new IllegalArgumentException("There are no this request in DB");
        }

        if(mentorsAndUsers.containsKey(mentorId) && mentorsAndUsers.get(mentorId).contains(senderId)){
            throw new IllegalArgumentException("The user is already the sender's mentor");
        }

        mentorsAndUsers.put(List.of(mentorId), List.of(senderId));
        mentorshipRequestRepository.findById(id).get().setStatus(RequestStatus.ACCEPTED);
    }
}
