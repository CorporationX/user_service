package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipRequestedEvent;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDro;
import school.faang.user_service.entity.Mentorship;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.mentorship_request.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipRequestedEventPublisher;
import school.faang.user_service.publisher.MentorshipAcceptedEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;
import school.faang.user_service.validator.MentorshipValidator;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipValidator mentorshipValidator;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipRequestedEventPublisher mentorshipRequestedEventPublisher;
    private final MentorshipAcceptedEventPublisher mentorshipAcceptedEventPublisher;


    @Transactional
    public void requestMentorship(MentorshipRequestDto requestDto) {
        long requesterId = requestDto.getRequesterId();
        long receiverId = requestDto.getReceiverId();

        validateExistsUsers(requesterId, receiverId);
        mentorshipRequestValidator.validateUserIds(requesterId, receiverId);
        mentorshipRequestValidator.validateRequestTime(requesterId, receiverId);

        mentorshipRequestRepository.create(requesterId, receiverId, requestDto.getDescription());
        mentorshipRequestedEventPublisher.publish(new MentorshipRequestedEvent(requesterId, receiverId, LocalDateTime.now()));
    }

    @Transactional(readOnly = true)
    public List<MentorshipRequestDto> getRequests(RequestFilterDro filters) {
        List<MentorshipRequest> requests = mentorshipRequestRepository.findAll();

        for (MentorshipRequestFilter filter : mentorshipRequestFilters) {
            if (filter.isApplicable(filters)) {
                requests = filter.apply(requests.stream(), filters).toList();
            }
        }

        return mentorshipRequestMapper.toDto(requests);
    }

    @Transactional
    public MentorshipRequestDto acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Такого реквеста не существует"));

        User requester =  mentorshipRequest.getRequester();
        User receiver =  mentorshipRequest.getReceiver();

        mentorshipValidator.validationMentorship(requester.getId(), receiver.getId());
        receiver.getMentees().add(requester);

        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        MentorshipRequest savedMentorshipRequest = mentorshipRequestRepository.save(mentorshipRequest);

        mentorshipAcceptedEventPublisher.publish(mentorshipRequestMapper.toEventDto(savedMentorshipRequest));
        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    @Transactional
    public void rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Такого реквеста не существует"));

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.getReason());
    }

    public void validateExistsUsers(long requesterId, long receiverId) {
        if (!userRepository.existsById(requesterId) || !userRepository.existsById(receiverId)) {
            throw new DataValidationException("Нет пользователя с таким айди");
        }
    }
}