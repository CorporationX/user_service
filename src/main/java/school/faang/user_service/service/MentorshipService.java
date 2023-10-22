package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.RequestNotFoundException;
import school.faang.user_service.mapper.MapperUserDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.messaging.MentorshipEventPublisher;
import school.faang.user_service.messaging.events.MentorshipStartEvent;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MapperUserDto mapperUserDto;
    private final MentorshipEventPublisher mentorshipEventPublisher;

    @Transactional
    public void approveMentorshipRequest(Long requesterId, Long receiverId){
        Optional<MentorshipRequest> mentorshipRequest = mentorshipRequestRepository.findLatestRequest(requesterId, receiverId);
        if (mentorshipRequest.isEmpty()){
            throw new RequestNotFoundException(requesterId, receiverId);
        }
        if (mentorshipRequest.get().getStatus().equals(RequestStatus.PENDING)){
            setAcceptedStatus(mentorshipRequest.get());
        }
        MentorshipStartEvent event = MentorshipStartEvent.builder().menteeId(requesterId).menteeId(receiverId).build();
        mentorshipEventPublisher.publish(event);
    }

    public List<UserDto> getMentees(Long userId) {
        Optional<User> mentorId = mentorshipRepository.findById(userId);
        if (mentorId.isEmpty()) {
            throw new DataValidationException("Invalid mentee ID" + userId);
        }
        return mapperUserDto.toDto(mentorId.get().getMentees());
    }

    public List<UserDto> getMentors(Long userId) {
        Optional<User> menteeId = mentorshipRepository.findById(userId);
        if (menteeId.isEmpty()) {
            throw new DataValidationException("Invalid mentor ID" + userId);
        }
        return mapperUserDto.toDto(menteeId.get().getMentors());
    }

    @Transactional
    public void deleteMentee(Long menteeId, Long mentorId) {
        User mentee = findUserValidation(menteeId);
        User mentor = findUserValidation(mentorId);

        if (mentor.getId() == mentee.getId()) {
            throw new DataValidationException("Invalid deletion. You can't be mentee of yourself");
        }

        mentor.getMentees().remove(mentee);
        userRepository.save(mentor);
    }

    @Transactional
    public void deleteMentor(Long menteeId, Long mentorId) {
        User mentee = findUserValidation(menteeId);
        User mentor = findUserValidation(mentorId);

        if (mentor.getId() == mentee.getId()) {
            throw new DataValidationException("Invalid deletion. You can't be mentor of yourself");
        }

        mentee.getMentors().remove(mentor);
        userRepository.save(mentee);
    }

    private User findUserValidation(Long id) {
        if (id == null) {
            throw new DataValidationException("Invalid ID");
        }
        return userRepository.findById(id).orElseThrow(() ->
                new DataValidationException("Invalid ID"));
    }

    private void setAcceptedStatus(MentorshipRequest mentorshipRequest){
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequest.setUpdatedAt(LocalDateTime.now());
        mentorshipRequestRepository.save(mentorshipRequest);
    }
}


