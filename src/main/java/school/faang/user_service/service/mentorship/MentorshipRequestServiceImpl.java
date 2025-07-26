package school.faang.user_service.service.mentorship;

import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service("Spring")
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {

    @Value("${Mentorship.min.months-between-requests}")
    private int minMonthsBetweenRequests;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final UserRepository userRepository;
    private final UserContext userContext;

    @Override
    public MentorshipRequestDto create(CreateMentorshipRequestDto requestDto) {
        final Long requesterId = userContext.getUserId();
        final Long mentorId = requestDto.mentorId();

        checkMentorshipIsScheduled(requesterId, mentorId);
        checkResaverIsRequester(requesterId, mentorId);
        checkForMinMonthsBetweenRequest(requesterId, mentorId);

        MentorshipRequest mentorshipRequest = mentorshipRequestRepository
                .create(requesterId, mentorId, requestDto.description());

        log.info("Mentorship request {} created", mentorshipRequest.getId());
        return mentorshipRequestMapper.toMentorshipRequestDto(mentorshipRequest);
    }

    @Override
    public void accept(long requestId) {
        MentorshipRequest request = mentorshipRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Request not found, id: %s", requestId)));

        User mentor = request.getReceiver();
        User mentee = request.getRequester();

        mentor.getMentors().add(mentee);
        userRepository.save(mentor);

        mentee.getMentees().add(mentor);
        userRepository.save(mentee);

        request.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(request);
    }

    @Override
    public void reject(long requestId, RejectionDto rejectionDto) {
        MentorshipRequest request = mentorshipRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Request not found, id: %s", requestId)));
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejectionDto.reason());
        mentorshipRequestRepository.save(request);
    }

    private void checkForMinMonthsBetweenRequest(Long requesterId, Long receiverId) {
        MentorshipRequest latestRequest = mentorshipRequestRepository
                .findLatestRequest(requesterId, receiverId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Latest request not found, requesterId: %s, receiverId: %s",
                                requesterId, receiverId)
                        )
                );

        long monthsBetweenRequest = ChronoUnit.MONTHS.between(latestRequest.getCreatedAt(), LocalDateTime.now());

        if (monthsBetweenRequest <= minMonthsBetweenRequests) {
            throw new DataValidationException(
                    String.format("It hasn't been %s months for the request, it has: %s",
                            minMonthsBetweenRequests, monthsBetweenRequest)
            );
        }
    }

    private void checkResaverIsRequester(Long requesterId, Long mentorId) {
        if (requesterId.equals(mentorId)) {
            throw new ForbiddenException(
                    String.format("you can't appoint yourself as a mentor, id: %s", requesterId));
        }
    }

    private void checkMentorshipIsScheduled(Long requesterId, Long mentorId) {
        MentorshipRequest request = mentorshipRequestRepository
                .findLatestRequest(requesterId, mentorId)
                .orElseThrow(() -> new EntityNotFoundException(
                                String.format("Latest request not found, requesterId: %s, mentorId: %s",
                                        requesterId, mentorId)
                        )
                );

        if (Objects.equals(mentorId, request.getRequester().getId())) {
            throw new ForbiddenException(
                    String.format("Mentorship has already been scheduled, is: %s", request.getId())
            );
        }
    }

    @Override
    public List<MentorshipRequestDto> getByFilters(MentorshipRequestFilterDto filter) {
        List<MentorshipRequest> mentorshipRequestList = mentorshipRequestRepository.findAll();
        return mentorshipRequestList.stream()
                .filter(request -> requestFilter(filter, request))
                .map(mentorshipRequestMapper::toMentorshipRequestDto)
                .toList();
    }

    private boolean requestFilter(MentorshipRequestFilterDto filter, MentorshipRequest request) {
        Long requesterId = request.getRequester().getId();
        Long mentorId = request.getReceiver().getId();
        RequestStatus status = request.getStatus();

        boolean requesterFlag = true;
        boolean mentorFlag = true;
        boolean statusFlag = true;

        if (requesterId != null) {
            if (!Objects.equals(requesterId, filter.requesterId())) {
                requesterFlag = false;
            }
        }
        if (mentorId != null) {
            if (!Objects.equals(mentorId, filter.receiverId())) {
                mentorFlag = false;
            }
        }
        if (status != null) {
            if (!Objects.equals(status, filter.status())) {
                statusFlag = false;
            }
        }
        return requesterFlag && mentorFlag && statusFlag;
    }
}