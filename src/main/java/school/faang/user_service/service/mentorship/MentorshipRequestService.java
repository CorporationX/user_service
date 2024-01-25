package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;


@Component
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final UserService userService;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        User receiver = userService.findUserById(mentorshipRequestDto.getReceiver());
        User requester = userService.findUserById(mentorshipRequestDto.getRequester());

        mentorshipRequestValidator.sameUserValidation(receiver, requester);
        mentorshipRequestValidator.dateCheckValidation(receiver, requester);

        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        mentorshipRequestRepository.save(mentorshipRequest);
        return mentorshipRequestMapper.toDTO(mentorshipRequest);
    }
}
