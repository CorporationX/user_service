package school.faang.user_service.service.mentorship;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.MentorshipStartEvent;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipStartEventPublisher;
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
@Data
@AllArgsConstructor
public class MentorshipRequestService {

    private MentorshipRequestRepository mentorshipRequestRepository;

    private MentorshipRequestValidator mentorshipRequestValidator;

    private MentorshipRequestMapper mentorshipRequestMapper;

    private UserRepository userRepository;

    private List<RequestFilter> requestFilters;

    private MentorshipStartEventPublisher mentorshipStartEventPublisher;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestValidator.validateMentorshipRequest(mentorshipRequestDto);

        mentorshipRequestDto.setStatus(RequestStatus.PENDING);
        MentorshipRequest request = mentorshipRequestMapper.toEntity(mentorshipRequestDto);

        mentorshipRequestRepository.save(request);
        return mentorshipRequestMapper.toDto(request);
    }

    @Transactional(readOnly = true)
    public List<MentorshipRequestDto> findAll(RequestFilterDto requestFilterDto) {
        return StreamSupport
                .stream(mentorshipRequestRepository.findAll().spliterator(), false)
                .filter(mentorshipRequest -> isRequestApplicable(mentorshipRequest, requestFilterDto))
                .map(mentorshipRequestMapper::toDto)
                .toList();
    }

    @Transactional
    public void acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = getMentorshipRequest(id);

        User user = userRepository.findById(mentorshipRequest.getRequester().getId())
                .orElseThrow(() -> new DataValidationException(NO_USER_IN_DB.getMessage()));

        List<User> mentors = user.getMentors();
        long receiverId = mentorshipRequest.getReceiver().getId();

        if (mentors.stream().anyMatch(mentor -> mentor.getId() == receiverId)) {
            throw new DataValidationException(USER_ALREADY_HAS_SUCH_MENTOR.getMessage());
        }
        mentors.add(userRepository.findById(receiverId)
                .orElseThrow(() -> new DataValidationException(NO_USER_IN_DB.getMessage())));

        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);

        mentorshipStartEventPublisher.convertAndSend(new MentorshipStartEvent(mentorshipRequest.getRequester().getId()
                , mentorshipRequest.getReceiver().getId()));

        mentorshipRequestRepository.save(mentorshipRequest);
    }

    @Transactional
    public void rejectRequest(long id, RejectionDto rejectionDto) {
        MentorshipRequest mentorshipRequest = getMentorshipRequest(id);

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejectionDto.getRejectionReason());
        mentorshipRequestRepository.save(mentorshipRequest);
    }

    private boolean isRequestApplicable(MentorshipRequest mentorshipRequest, RequestFilterDto requestFilterDto) {
        long applicableCount = requestFilters.stream()
                .filter(filter -> filter.isApplicable(requestFilterDto))
                .count();
        long matchCount = requestFilters.stream()
                .filter(filter -> filter.isApplicable(requestFilterDto))
                .flatMap(filter -> filter.apply(mentorshipRequest, requestFilterDto))
                .count();
        return matchCount == applicableCount;
    }

    private MentorshipRequest getMentorshipRequest(long id) {
        return mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(NO_REQUEST_IN_DB.getMessage()));
    }
}
