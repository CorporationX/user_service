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
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.mentorship.MentorshipValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestService{

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipMapper mentorshipMapper;
    private final MentorshipEventPublisher mentorshipEventPublisher;
    private final MentorshipValidator mentorshipValidator;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto){

        if(mentorshipRequestDto.getRequesterId()==mentorshipRequestDto.getReceiverId()){
            log.warn("You cannot be mentor to yourself!");
            throw new DataValidationException("You cannot be mentor to yourself!");
        }

        long mentorId=mentorshipRequestDto.getReceiverId();
        long menteeId=mentorshipRequestDto.getRequesterId();

        mentorshipValidator.checkIfUserExists(mentorId);
        mentorshipValidator.checkIfUserExists(menteeId);

        if(!mentorshipValidator.isAllowedToMakeRequest(menteeId, mentorId)){
            log.warn("Error:previous request was made earlier than 3 months!");
            throw new DataValidationException("Error:previous request was made earlier than 3 months");
        }

        mentorshipRequestDto.setStatus(RequestStatus.PENDING);

        MentorshipRequest mentorshipRequest=mentorshipRequestRepository.save(mentorshipMapper
                .toEntity(mentorshipRequestDto));

        return mentorshipMapper.toDto(mentorshipRequest);
    }

    public void acceptRequest(long id){

        MentorshipRequest mentorshipRequest=checkMentorshipReqExists(id);

        User mentee=mentorshipRequest.getRequester();
        User mentor=mentorshipRequest.getReceiver();
        List<User> mentees=mentor.getMentees();

        boolean isAlreadyMentee = mentorshipRequestRepository
                .existsAcceptedRequest(mentee.getId(), mentor.getId());

        if( isAlreadyMentee){
            log.warn("Already mentor for user: " + mentee.getId());
            throw new DataValidationException("Already mentor for user: "+mentee.getId());
        }else{
            mentees.add(mentee);
            mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
            mentorshipRequestRepository.save(mentorshipRequest);
            log.info("You are mentor for user with id " + mentee.getId());
            MentorshipStartEvent mentorshipStartEvent=MentorshipStartEvent
                    .builder().menteeId(mentee.getId())
                    .mentorId(mentor.getId()).build();
            mentorshipEventPublisher.publish(mentorshipStartEvent);
        }

    }

    private MentorshipRequest checkMentorshipReqExists(long id){
        return mentorshipRequestRepository.findById(id)
                .orElseThrow(()->{
                    log.warn("No request with such id "+id);
                    return new EntityNotFoundException("No request with such id "+id);

                });
    }



}
