package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.filter.RequestFilter;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;
import java.util.stream.StreamSupport;

import static school.faang.user_service.exception.message.ExceptionMessage.NO_REQUEST_IN_DB;
import static school.faang.user_service.exception.message.ExceptionMessage.USER_ALREADY_HAS_SUCH_MENTOR;
import static school.faang.user_service.exception.message.MessageForGoalInvitationService.NO_USER_IN_DB;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;

    private final MentorshipRequestValidator mentorshipRequestValidator;

    private final MentorshipRequestMapper mentorshipRequestMapper;

    private final UserRepository userRepository;

    private final List<RequestFilter> requestFilters;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestValidator.validateMentorshipRequest(mentorshipRequestDto);

        mentorshipRequestDto.setStatus(RequestStatus.PENDING);
        return mentorshipRequestMapper.toDto(mentorshipRequestRepository.save(mentorshipRequestMapper.toEntity(mentorshipRequestDto)));
    }

    public List<MentorshipRequestDto> findAll(RequestFilterDto requestFilterDto) {
        return StreamSupport
                .stream(mentorshipRequestRepository.findAll().spliterator(), false)
                .toList()
                .stream()
                .filter(mentorshipRequest -> requestFilters
                        .stream()
                        .filter(filter -> filter.isApplicable(requestFilterDto))
                        .flatMap(filter -> filter.apply(mentorshipRequest, requestFilterDto))
                        .count() == requestFilters.stream().filter(filter -> filter.isApplicable(requestFilterDto)).count())
                .map(mentorshipRequestMapper::toDto)
                .toList();
    }

    @Transactional
    public void acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = getMentorshipRequest(id);
        long receiverId = mentorshipRequest.getReceiver().getId();

        User user = userRepository.findById(mentorshipRequest.getRequester().getId())
                .orElseThrow(() -> new DataValidationException(NO_USER_IN_DB.getMessage()));
        List<User> mentors = user.getMentors();

        for (User mentor : mentors) {
            if (mentor.getId() == receiverId) {
                throw new DataValidationException(USER_ALREADY_HAS_SUCH_MENTOR.getMessage());
            } else {
                mentors.add(userRepository.findById(receiverId).get());
            }
        }
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(mentorshipRequest);
    }

    @Transactional
    public void rejectRequest(long id, RejectionDto rejectionDto) {
        MentorshipRequest mentorshipRequest = getMentorshipRequest(id);

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejectionDto.getRejectionReason());
        mentorshipRequestRepository.save(mentorshipRequest);
    }

    private MentorshipRequest getMentorshipRequest(long id) {
        return mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(NO_REQUEST_IN_DB.getMessage()));
    }
}
