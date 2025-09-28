package school.faang.user_service.service.mentorship;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.system_event.MentorshipRequestedEvent;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipRequestedEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {

    private static final Period REQUEST_COOLDOWN = Period.ofMonths(3);

    private final MentorshipRepository mentorshipRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final UserContext userContext;
    private final UserRepository userRepository;
    private final MentorshipRequestedEventPublisher mentorshipRequestedEventPublisher;

    @Override
    public MentorshipRequestDto create(CreateMentorshipRequestDto dto) {
        long requesterId = userContext.getUserId();
        long receiverId = dto.mentorId();

        checkCooldown(requesterId);
        validateNotSelfRequest(requesterId, receiverId);
        checkActiveRequestExists(requesterId, receiverId);

        MentorshipRequest request = mentorshipRequestRepository.create(
                requesterId, receiverId, dto.description()
        );
        mentorshipRequestedEventPublisher.publish(
                new MentorshipRequestedEvent(requesterId, receiverId, LocalDateTime.now())
        );

        request.setRequester(userRepository.findById(requesterId)
                .orElseThrow(() -> new EntityNotFoundException("Менти не найден")));
        request.setReceiver(userRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("Ментор не найден")));

        log.info("Пользователь {} отправил запрос на менторство к {}", requesterId, receiverId);
        return mentorshipRequestMapper.toMentorshipRequestDto(request);
    }

    private void checkCooldown(long requesterId) {
        mentorshipRequestRepository.findTopByRequesterIdOrderByCreatedAtDesc(requesterId)
                .ifPresent(request -> {
                    if (request.getCreatedAt().isAfter(LocalDateTime.now().minus(REQUEST_COOLDOWN))) {
                        throw new DataValidationException(
                                "Запрос можно отправить не чаще одного раза в "
                                        + REQUEST_COOLDOWN.getMonths() + " месяца(ев)"
                        );
                    }
                });
    }

    private void validateNotSelfRequest(long requesterId, long receiverId) {
        if (requesterId == receiverId) {
            throw new DataValidationException("Нельзя отправить запрос самому себе");
        }
    }

    private void checkActiveRequestExists(long requesterId, long receiverId) {
        mentorshipRequestRepository.findLatestRequest(requesterId, receiverId)
                .ifPresent(req -> {
                    if (req.getStatus() == RequestStatus.PENDING) {
                        throw new DataValidationException("Уже существует активный запрос");
                    }
                });
    }

    @Override
    public List<MentorshipRequestDto> getByFilters(MentorshipRequestFilterDto filterDto) {
        if (filterDto.getRequesterId() == null && filterDto.getReceiverId() == null) {
            throw new DataValidationException(
                    "Хотя бы один из параметров: requesterId или receiverId должен быть задан"
            );
        }

        return mentorshipRequestRepository.findAll().stream()
                .filter(r -> filterDto.getRequesterId() == null
                        || filterDto.getRequesterId().equals(r.getRequester().getId()))
                .filter(r -> filterDto.getReceiverId() == null
                        || filterDto.getReceiverId().equals(r.getReceiver().getId()))
                .filter(r -> filterDto.getStatus() == null || filterDto.getStatus() == r.getStatus())
                .map(mentorshipRequestMapper::toMentorshipRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public void accept(long requestId) {
        long currentUserId = userContext.getUserId();
        MentorshipRequest request = validateRequest(requestId, currentUserId);

        if (isAlreadyMentor(currentUserId, request.getRequester().getId())) {
            throw new DataValidationException(
                    "Ментор " + currentUserId + " уже является ментором пользователя "
                            + request.getRequester().getId()
            );
        }

        request.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(request);
        log.info("Пользователь {} принял запрос от {}", currentUserId, request.getRequester().getId());
    }

    @Override
    public void reject(long requestId, RejectionDto rejectionDto) {
        long currentUserId = userContext.getUserId();
        MentorshipRequest request = validateRequest(requestId, currentUserId);

        if (rejectionDto == null || rejectionDto.getReason() == null || rejectionDto.getReason().isBlank()) {
            throw new DataValidationException("Причина отказа должна быть указана");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejectionDto.getReason());
        mentorshipRequestRepository.save(request);

        log.info("Пользователь {} отклонил запрос от {}. Причина: {}",
                currentUserId, request.getRequester().getId(), rejectionDto.getReason());
    }

    private MentorshipRequest validateRequest(long requestId, long currentUserId) {
        MentorshipRequest request = mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос на менторство не найден"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new DataValidationException("Можно обрабатывать только запросы в статусе PENDING");
        }

        if (!request.getReceiver().getId().equals(currentUserId)) {
            throw new ForbiddenException("Вы не можетее обработать чужой запрос");
        }

        return request;
    }

    private boolean isAlreadyMentor(long mentorId, long menteeId) {
        return mentorshipRequestRepository.existsByRequesterIdAndReceiverIdAndStatus(
                menteeId, mentorId, RequestStatus.ACCEPTED
        );
    }
}