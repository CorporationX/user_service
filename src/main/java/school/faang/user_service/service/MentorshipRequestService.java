package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.event.MentorshipStartEvent;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.MentorshipMapper;
import school.faang.user_service.publisher.MentorshipEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestService{
    private final UserRepository userRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipMapper mentorshipMapper;
    private final MentorshipEventPublisher mentorshipEventPublisher;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto){

//        User mentor=mentorshipRequestDto.getReceiver();
//        User mentee=mentorshipRequestDto.getRequester();

        if(mentorshipRequestDto.getRequesterId()==mentorshipRequestDto.getReceiverId()){
            log.warn("You cannot be mentor to yourself!");
            throw new DataValidationException("You cannot be mentor to yourself!");
        }

        long mentor_id=mentorshipRequestDto.getReceiverId();
        long mentee_id=mentorshipRequestDto.getRequesterId();

        checkIfUserExists(mentor_id);
        checkIfUserExists(mentee_id);

        if(!isAllowedToMakeRequest(mentee_id, mentor_id)){
            throw new DataValidationException("previous request was made earlier than 3 months!");
        }

        MentorshipRequest mentorshipRequest=mentorshipRequestRepository.save(mentorshipMapper
                .toEntity(mentorshipRequestDto));

        return mentorshipMapper.toDto(mentorshipRequest);
    }

    public void acceptRequest(long id){

        /*

        TODO:   change mentorshiprequestrepo status to Accepted
                add to mentorship bd

        */
        MentorshipRequest mentorshipRequest=mentorshipRequestRepository.findById(id)
                .orElseThrow(()->{
                    log.warn("No request with such id "+id);
                    return new EntityNotFoundException("No request with such id "+id);

                });

        User mentee=mentorshipRequest.getRequester();
        User mentor=mentorshipRequest.getReceiver();
        List<User> mentees=mentor.getMentees();

        if(mentees.contains(mentee)){
            log.warn("Already mentor for user: "+mentee.getId());
            throw new DataValidationException("Already mentor for user: "+mentee.getId());
        }else{
            mentees.add(mentee);
            mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
            MentorshipStartEvent mentorshipStartEvent=MentorshipStartEvent
                    .builder().mentee_id(mentee.getId())
                    .mentor_id(mentor.getId()).build();
            mentorshipEventPublisher.publish(mentorshipStartEvent);
        }

    }

    private boolean isAllowedToMakeRequest(long mentee_id, long mentor_id){

        LocalDateTime threeMonthsAgo=LocalDateTime.now().minus(3, ChronoUnit.MONTHS);
        Optional<MentorshipRequest> latestRequest=mentorshipRequestRepository
                .findLatestRequest(mentee_id, mentor_id);

        if (latestRequest.isPresent() && latestRequest.get().getCreatedAt().isAfter(threeMonthsAgo)) {
            log.warn("Error: previous request was made earlier than 3 months");
            return false;
        }

        return true;
    }

    private void checkIfUserExists(long userId){

        userRepository.findById(userId)
                .orElseThrow(()->{
                    log.warn("No user with such id "+userId);
                    return new EntityNotFoundException("No user with such id "+userId);

                });
    }

}
