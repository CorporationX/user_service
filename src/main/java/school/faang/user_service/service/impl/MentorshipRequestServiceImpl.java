package school.faang.user_service.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.filter.mentorshipRequestFilter.MentorshipRequestFilter;
import school.faang.user_service.model.event.MentorshipAcceptedEvent;
import school.faang.user_service.model.dto.MentorshipRequestDto;
import school.faang.user_service.model.dto.RejectionDto;
import school.faang.user_service.model.event.MentorshipOfferedEvent;
import school.faang.user_service.model.filter_dto.MentorshipRequestFilterDto;
import school.faang.user_service.model.entity.MentorshipRequest;
import school.faang.user_service.model.enums.RequestStatus;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipOfferedEventPublisher;
import school.faang.user_service.publisher.MentorshipAcceptedEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.MentorshipRequestRepository;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {

    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<MentorshipRequestFilter> mentorshipRequestFilterList;
    private final MentorshipAcceptedEventPublisher mentorshipAcceptedPublisher;
    private final MentorshipOfferedEventPublisher mentorshipOfferedEventPublisher;

    @Override
    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestValidator.descriptionValidation(mentorshipRequestDto);
        mentorshipRequestValidator.requesterReceiverValidation(mentorshipRequestDto);
        mentorshipRequestValidator.selfRequestValidation(mentorshipRequestDto);
        mentorshipRequestValidator.lastRequestDateValidation(mentorshipRequestDto);

        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        User requester = userRepository.findById(mentorshipRequestDto.getRequesterId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("User with id = %d not found", mentorshipRequestDto.getRequesterId())));
        User receiver = userRepository.findById(mentorshipRequestDto.getReceiverId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("User with id = %d not found", mentorshipRequestDto.getReceiverId())));
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        MentorshipRequest savedRequest = mentorshipRequestRepository.save(mentorshipRequest);
        log.info("Получен запрос на менторство от пользователя с ID: {}", mentorshipRequestDto.getRequesterId());
        MentorshipOfferedEvent mentorshipOfferedEvent = new MentorshipOfferedEvent(
                mentorshipRequestDto.getRequesterId(),
                savedRequest.getId(),
                mentorshipRequestDto.getReceiverId());
        mentorshipOfferedEventPublisher.publish(mentorshipOfferedEvent);
        log.info("MentorshipOfferedEvent sent to redis {}", mentorshipOfferedEvent);
        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    @Override
    public List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto requestFilter) {
        if (mentorshipRequestFilterList.isEmpty()) {
            log.warn("Список фильтров пуст");
            return List.of();
        }
        List<MentorshipRequest> requestList = mentorshipRequestRepository.findAll();
        List<MentorshipRequestDto> result = mentorshipRequestFilterList.stream()
                .filter(filter -> filter.isApplicable(requestFilter))
                .flatMap(filter -> filter.apply(requestList, requestFilter))
                .map(mentorshipRequestMapper::toDto)
                .toList();
        log.info("Получен список запросов на менторство с фильтром: {}", requestFilter);
        return result;
    }

    @Override
    public MentorshipRequestDto acceptRequest(Long id) {
        mentorshipRequestValidator.requestValidation(id);
        MentorshipRequest request = mentorshipRequestRepository.findById(id).orElseThrow();
        mentorshipRequestValidator.requesterReceiverValidation(mentorshipRequestMapper.toDto(request));
        User requestMentee = userRepository.findById(request.getRequester().getId()).orElseThrow();
        User requestMentor = userRepository.findById(request.getReceiver().getId()).orElseThrow();
        if (requestMentee.getMentors().stream().noneMatch(mentor -> mentor.equals(requestMentor))) {
            requestMentee.getMentors().add(requestMentor);
            log.info("Запрос на менторство id{} принят", id);
            mentorshipAcceptedPublisher.publish(new MentorshipAcceptedEvent(
                    id, requestMentee.getId(), requestMentor.getId()));
            log.info("Публикация события MentorshipAcceptedEvent для запроса id{} между mentee id{} и mentor id{}",
                    id, requestMentee.getId(), requestMentor.getId());
        } else {
            log.info("Пользователь id{} уже является ментором отправителя id{}", requestMentor.getId(), requestMentee.getId());
            throw new DataValidationException("Пользователь id" + requestMentor.getId() + " уже является ментором отправителя id" + requestMentee.getId());
        }
        return mentorshipRequestMapper.toDto(request);
    }

    @Override
    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        mentorshipRequestValidator.requestValidation(id);
        MentorshipRequest request = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Запрос id" + id + " не найден"));
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getRejectionReason());
        mentorshipRequestRepository.save(request);
        log.info("Запрос на менторство id{} отклонен", id);
        return mentorshipRequestMapper.toDto(request);
    }
}
