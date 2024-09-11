package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.MentorshipAcceptedEvent;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.event.MentorshipRequestEvent;
import school.faang.user_service.event.MentorshipStartEvent;
import school.faang.user_service.event.MentorshipOfferedEvent;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.mapper.mentorship.MentorshipRequestEventMapper;
import school.faang.user_service.mapper.mentorship.MentorshipOfferedEventMapper;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipRequestEventPublisher;
import school.faang.user_service.redisPublisher.MentorshipAcceptedEventPublisher;
import school.faang.user_service.publisher.MentorshipStartEventPublisher;
import school.faang.user_service.publishier.MentorshipOfferedEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipRequestEventMapper mentorshipRequestEventMapper;
    private final MentorshipOfferedEventMapper mentorshipOfferedEventMapper;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipStartEventPublisher mentorshipStartEventPublisher;
    private final List<MentorshipRequestFilter> mentorshipRequestFilterList;
    private final MentorshipRequestEventPublisher mentorshipRequestEventPublisher;
    private final MentorshipAcceptedEventPublisher mentorshipAcceptedEventPublisher;
    private final MentorshipOfferedEventPublisher mentorshipOfferedEventPublisher;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        long requesterId = mentorshipRequestDto.getRequesterId();
        long receiverId = mentorshipRequestDto.getReceiverId();
        String description = mentorshipRequestDto.getDescription();

        mentorshipRequestValidator.validateParticipantsAndRequestFrequency(requesterId, receiverId,
                mentorshipCreationDate);
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.create(requesterId, receiverId, description);

        MentorshipRequestEvent mentorshipRequestEvent = mentorshipRequestEventMapper.toEvent(mentorshipRequest);
        mentorshipRequestEventPublisher.publish(mentorshipRequestEvent);
        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    @Transactional(readOnly = true)
    public List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto filtersDto) {
        Stream<MentorshipRequest> allMatchedMentorshipRequests = mentorshipRequestRepository.findAll().stream();
        List<MentorshipRequestFilter> applicableMentorshipRequestFilters =
                mentorshipRequestFilterList.stream().filter(filter -> filter.isApplicable(filtersDto)).toList();

        for (MentorshipRequestFilter mentorshipRequestFilter : applicableMentorshipRequestFilters) {
            allMatchedMentorshipRequests = mentorshipRequestFilter.filter(allMatchedMentorshipRequests, filtersDto);
        }

        return mentorshipRequestMapper.toDtoList(allMatchedMentorshipRequests.toList());
    }

    @Transactional
    public MentorshipRequestDto acceptRequest(long requestId) {
        MentorshipRequest processedMentorshipRequest = processRequest(requestId, (mentorshipRequest) -> {
            User requester = mentorshipRequest.getRequester();
            User receiver = mentorshipRequest.getReceiver();
            mentorshipRequestValidator.validateReceiverIsNotMentorOfRequester(requester, receiver);
            requester.getMentors().add(receiver);
            receiver.getMentees().add(requester);
            mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        });

        mentorshipAcceptedEventPublisher.publish(new MentorshipAcceptedEvent(
                        processedMentorshipRequest.getRequester().getId(),
                        processedMentorshipRequest.getReceiver().getId(),
                        processedMentorshipRequest.getId())
        );

        return mentorshipRequestMapper.toDto(processedMentorshipRequest);
    }

    @Transactional
    public MentorshipRequestDto rejectRequest(long requestId, RejectionDto rejectionDto) {
        MentorshipRequest processedMentorshipRequest = processRequest(requestId, (mentorshipRequest) -> {
            mentorshipRequest.setRejectionReason(rejectionDto.getRejectionReason());
            mentorshipRequest.setStatus(RequestStatus.REJECTED);
        });

        return mentorshipRequestMapper.toDto(processedMentorshipRequest);
    }

    private MentorshipRequest processRequest(long requestId, Consumer<MentorshipRequest> requestStatusConsumer) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(requestId).orElseThrow(() ->
                new IllegalArgumentException("Could not find Mentorship Request in database by id: " + requestId));
        mentorshipRequestValidator.validateRequestStatusIsPending(mentorshipRequest.getStatus());

        requestStatusConsumer.accept(mentorshipRequest);

        mentorshipRequest.setUpdatedAt(LocalDateTime.now());
        return mentorshipRequestRepository.save(mentorshipRequest);
    }
}
