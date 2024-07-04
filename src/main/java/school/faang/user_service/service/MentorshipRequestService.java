package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

import school.faang.user_service.exceptions.DuplicateMentorshipRequestException;
import school.faang.user_service.exceptions.ValidationException;
import school.faang.user_service.mappers.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.util.Optional;

@CommonsLog

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (!userRepository.existsById(mentorshipRequestDto.getRequesterId())) {
            throw new ValidationException("Requester user does not exist!");
        }
        if (!userRepository.existsById(mentorshipRequestDto.getReceiverId())) {
            throw new ValidationException("Receiver user does not exist!");
        }
        mentorshipRequestRepository.findFreshRequest(mentorshipRequestDto.getRequesterId()).ifPresent((req)->{
            throw new ValidationException("User has one request for last 3 months!");
        });
        Optional<MentorshipRequest> earlierMentorshipRequest = mentorshipRequestRepository.findLatestRequest(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId());
        if (earlierMentorshipRequest.isPresent()) {
            throw new DuplicateMentorshipRequestException("Request already exists");
        }
        MentorshipRequest createdRequest;
        createdRequest = mentorshipRequestRepository
                .create(mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId(),
                        mentorshipRequestDto.getDescription());

        return mentorshipRequestMapper.toDto(createdRequest);

    }
}
