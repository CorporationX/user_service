package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.config.properties.MentorshipProperties;
import school.faang.user_service.config.redis.MentorshipRedisConfig;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.event.Mentorship;
import school.faang.user_service.entity.event.MentorshipRequestedEvent;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.MentorshipAcceptRejectException;
import school.faang.user_service.exception.MentorshipRejectException;
import school.faang.user_service.exception.RejectMentorshipRequestByDateException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipProperties mentorshipProperties;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final UserContext userContext;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipRequestedEvent event;

    private  final MentorshipRedisConfig mentorshipRedisConfig;

    private final ChannelTopic mentorshipTopic() {
        return new ChannelTopic("MENTORSHIP_REQUESTS");
    }


    public MentorshipRequestDto create(CreateMentorshipRequestDto requestDto) {
        if (requestDto == null) {
            log.info("Request not found");
            throw new IllegalArgumentException();
        }
        Long requesterId = getUserIdFromContext();
        Long receiverId = requestDto.mentorId();
        if (receiverId == null) {
            log.info("Receiver not found");
            throw new UserNotFoundException(String.format("User with %d not found", receiverId));
        }
        if (requesterId.equals(receiverId)) {
            log.info("User send request himself {}", requesterId);
            throw new DataValidationException("User cannot request himself");
        }
        Optional<User> userById = userRepository.findById(userContext.getUserId());
        if (userById.isEmpty()) {
            log.info("User is empty {}", userById);
            throw new UserNotFoundException(
                    String.format("User not found %s", userById));
        }

        mentorshipRequestRepository.findLatestRequest(requesterId, receiverId)
                .ifPresent(lastRequest -> {
                    if (lastRequest.getCreatedAt().plusMonths(
                            mentorshipProperties.monthsToSubtract()).isAfter(LocalDateTime.now())
                    ) {
                        throw new RejectMentorshipRequestByDateException(
                                String.format("Request expired %d months",
                                        mentorshipProperties.monthsToSubtract()
                                ));
                    }
                });

        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.createRequest(
                requesterId, receiverId, requestDto.description());
        event.setReceiverId(requesterId);
        event.setMentorId(receiverId);
        return mentorshipRequestMapper.toMentorshipRequestDto(mentorshipRequest);
    }


    public List<MentorshipRequestDto> getByFilters(MentorshipRequestFilterDto filter) {
        if (filter == null) {
            throw new EntityNotFoundException("Filter cannot be empty");
        }
        List<MentorshipRequest> mentorshipRequestList = (List<MentorshipRequest>) mentorshipRequestRepository
                .findByFilters(filter.requesterId(), filter.receiverId(), filter.status());
        if (mentorshipRequestList.isEmpty()) {
            throw new EntityNotFoundException(
                    String.format("Not found requests with parameters: %s", filter
                    ));
        }
        return mentorshipRequestList.stream()
                .map(mentorshipRequestMapper::toMentorshipRequestDto)
                .toList();
    }


    public void accept(long requestId) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(requestId).orElseThrow(
                () -> new EntityNotFoundException("Request not found")
        );
        if (!mentorshipRequest.getReceiver().getId().equals(getUserIdFromContext())) {
            throw new MentorshipAcceptRejectException("Only special user can accept request");
        }
        List<Mentorship> assignedMentorships = mentorshipRepository.findMasterAndPadavanIds(
                getUserIdFromContext(), mentorshipRequest.getRequester().getId()
        );
        if (!assignedMentorships.isEmpty()) {
            throw new MentorshipAcceptRejectException(String.format(
                    "User %s is already your mentee", getUserIdFromContext()
            ));
        }

        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(mentorshipRequest);
        mentorshipRepository.createMentorship(
                mentorshipRequest.getReceiver().getId(), mentorshipRequest.getRequester().getId()
        );
    }


    public void reject(long requestId, RejectionDto rejectionDto) {
        Optional<MentorshipRequest> mentorshipRequestById = mentorshipRequestRepository.findById(requestId);
        if (mentorshipRequestById.isEmpty()) {
            throw new MentorshipRejectException("Request not found");
        }
        if (!mentorshipRequestById.get().getReceiver().getId().equals(getUserIdFromContext())) {
            throw new MentorshipRejectException("Denied");
        }
        mentorshipRequestById.get().setStatus(RequestStatus.REJECTED);
        mentorshipRequestById.get().setRejectionReason(rejectionDto.rejectionReason());
        mentorshipRequestRepository.save(mentorshipRequestById.get());
    }

    private Long getUserIdFromContext() {
        return userContext.getUserId();
    }

}
