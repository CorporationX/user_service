package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorshipRequest.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.mentorshipRequestFilter.MentorshipRequestFilter;
import school.faang.user_service.mapper.mentorshipRequest.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<MentorshipRequestFilter> mentorshipRequestFilterList;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestValidator.descriptionValidation(mentorshipRequestDto);
        mentorshipRequestValidator.requesterReceiverValidation(mentorshipRequestDto);
        mentorshipRequestValidator.selfRequestValidation(mentorshipRequestDto);
        mentorshipRequestValidator.lastRequestDateValidation(mentorshipRequestDto);

        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        mentorshipRequestRepository.save(mentorshipRequest);
        log.info("Получен запрос на менторство от пользователя с ID: {}", mentorshipRequestDto.getRequesterId());
        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

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

    public MentorshipRequestDto acceptRequest(Long id) {
        mentorshipRequestValidator.requestValidation(id);
        MentorshipRequest request = mentorshipRequestRepository.findById(id).orElseThrow();
        mentorshipRequestValidator.requesterReceiverValidation(mentorshipRequestMapper.toDto(request));
        User requestMentee = userRepository.findById(request.getRequester().getId()).orElseThrow();
        User requestMentor = userRepository.findById(request.getReceiver().getId()).orElseThrow();
        if (requestMentee.getMentors().stream().noneMatch(mentor -> mentor.equals(requestMentor))) {
            requestMentee.getMentors().add(requestMentor);
            log.info("Запрос на менторство id{} принят", id);
        } else {
            log.info("Пользователь id{} уже является ментором отправителя id{}", requestMentor.getId(), requestMentee.getId());
            throw new DataValidationException("Пользователь id" + requestMentor.getId() + " уже является ментором отправителя id" + requestMentee.getId());
        }
        return mentorshipRequestMapper.toDto(request);
    }

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