package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDisplayDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestServiceImpl implements MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final UserContext userContext;

    @Value("${mentorship.request.cooldown.months:3}")
    private int cooldownMonths;

    @Override
    @Transactional
    public MentorshipRequestDisplayDto create(CreateMentorshipRequestDto requestDto) {
        Long requesterId = userContext.getUserId();
        Long mentorId = requestDto.mentorId();

        checkSelfRequest(requesterId, mentorId);
        checkExistingMentorship(mentorId, requesterId);
        checkCooldownPeriod(requesterId, mentorId);

        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.create(
                requesterId, mentorId, requestDto.description());

        return mentorshipRequestMapper.toMentorshipRequestDisplayDto(mentorshipRequest);
    }

    @Override
    public List<MentorshipRequestDisplayDto> getByFilters(MentorshipRequestFilterDto filter) {
        List<MentorshipRequest> requests = mentorshipRequestRepository.findByFilters(
                filter.requesterId(), filter.receiverId(), filter.status());

        return requests.stream()
                .map(mentorshipRequestMapper::toMentorshipRequestDisplayDto)
                .toList();
    }

    @Override
    @Transactional
    public void accept(Long requestId) {
        MentorshipRequest request = mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос на менторство не найден"));

        Long currentUserId = userContext.getUserId();
        if (!request.getReceiver().getId().equals(currentUserId)) {
            throw new ForbiddenException("Недостаточно прав для принятия данного запроса");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new DataValidationException("Можно принять только запросы в статусе PENDING");
        }

        if (mentorshipRepository.existsMentorship(request.getReceiver().getId(), request.getRequester().getId())) {
            throw new DataValidationException("Пользователь уже является ментором");
        }

        mentorshipRepository.addMentorshipNative(request.getReceiver().getId(), request.getRequester().getId());
        request.setStatus(RequestStatus.ACCEPTED);
    }

    @Override
    @Transactional
    public void reject(Long requestId, RejectionDto rejectionDto) {
        MentorshipRequest request = mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос на менторство не найден"));

        Long currentUserId = userContext.getUserId();
        if (!request.getReceiver().getId().equals(currentUserId)) {
            throw new ForbiddenException("Недостаточно прав для отклонения данного запроса");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new DataValidationException("Можно отклонить только запросы в статусе PENDING");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejectionDto.reason());
    }

    private void checkSelfRequest(Long requesterId, Long mentorId) {
        if (requesterId.equals(mentorId)) {
            throw new DataValidationException("Нельзя отправить запрос на менторство самому себе");
        }
    }

    private void checkExistingMentorship(Long mentorId, Long menteeId) {
        if (mentorshipRepository.existsMentorship(mentorId, menteeId)) {
            throw new DataValidationException("Пользователь уже является вашим ментором");
        }
    }

    private void checkCooldownPeriod(Long requesterId, Long mentorId) {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(cooldownMonths);
        Optional<MentorshipRequest> recentRequest = mentorshipRequestRepository
                .findLatestRequestWithinPeriod(requesterId, mentorId, threeMonthsAgo);
        
        if (recentRequest.isPresent()) {
            throw new DataValidationException(
                    String.format("Нельзя отправлять запросы чаще чем раз в %d месяцев", cooldownMonths));
        }
    }
}
