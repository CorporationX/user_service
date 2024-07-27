package school.faang.user_service.service;


import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship_request_filter.MentorshipRequestFilter;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mapper;
    private final UserRepository userRepository;
    private final List<MentorshipRequestFilter> requestFilters;
    private final MentorshipRepository mentorshipRepository;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        validateMentorshipRequest(mentorshipRequestDto);
        MentorshipRequest entity = mapper.toEntity(mentorshipRequestDto);
        return mapper.toDto(mentorshipRequestRepository.save(entity));
    }

    @Transactional
    public List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilter) {
        List<MentorshipRequest> mentorshipRequest = IterableUtils.toList(mentorshipRequestRepository.findAll());
        List<MentorshipRequest> filteredMentorshipRequest = new LinkedList<>(mentorshipRequest);
        List<MentorshipRequestFilter> filters = new LinkedList<>();
        for (MentorshipRequestFilter filter : requestFilters) {
            if (filter.isApplecable(requestFilter)) {
                filters.add(filter);
            }
        }
        for (MentorshipRequestFilter filter : filters) {
            filteredMentorshipRequest = (filter.apply(filteredMentorshipRequest, requestFilter));
        }
        return filteredMentorshipRequest.stream()
                .map(mapper::toDto)
                .toList();
    }

    public MentorshipRequestDto acceptRequest(long id) {
        MentorshipRequest requestFromDb = mentorshipRequestRepository.findById(id).orElse(null);
        if (requestFromDb == null) {
            throw new IllegalArgumentException("Запроса на менторство нет в БД");
        }
        Optional<MentorshipRequest> lastRequests = mentorshipRequestRepository.findRequests(
                requestFromDb.getRequester().getId(), requestFromDb.getReceiver().getId()
        );
        if (lastRequests.isPresent()) {
            throw new IllegalArgumentException("Пользователь уже является ментором для данного пользователя");
        }
        // Не уверен, что это будет поиск по Id пользователя
        User user = mentorshipRepository.findById(requestFromDb.getRequester().getId()).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("Пользователя нет в таблице пользователей");
        }
        User mentor = mentorshipRepository.findById(requestFromDb.getReceiver().getId()).orElse(null);
        if (mentor == null) {
            throw new IllegalArgumentException("Ментора нет в таблице пользователей");
        }
        user.getMentors().add(mentor);
        mentorshipRepository.save(user);
        requestFromDb.setStatus(RequestStatus.ACCEPTED);
        return mapper.toDto(requestFromDb);
    }

    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        if (rejection == null || rejection.getRejectionReason().isBlank()) {
            throw new IllegalArgumentException("Пустая причина отказа");
        }
        MentorshipRequest request = mentorshipRequestRepository.findById(id).orElse(null);
        if (request == null) {
            throw new IllegalArgumentException("Переданного запроса нет в базе");
        }
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getRejectionReason());
        return mapper.toDto(mentorshipRequestRepository.save(request));
    }

    private void validateMentorshipRequest(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto == null) {
            throw new IllegalArgumentException("Дто не может быть пустым");
        }

        Long requesterId = mentorshipRequestDto.getRequesterId();
        Long receiverId = mentorshipRequestDto.getReceiverId();

        if (requesterId == null) {
            throw new IllegalArgumentException("Пользователь, который отправляет запрос на менторство не может быть" +
                                               " быть пустым");
        }

        if (receiverId == null) {
            throw new IllegalArgumentException("Пользователь, которому направляется запрос на менторство не может" +
                                               "быть пустым");
        }

        if (Objects.equals(requesterId, mentorshipRequestDto.getReceiverId())) {
            throw new IllegalArgumentException("Вы сделали запрос на менторство самому себе");
        }

        if (userRepository.findById(requesterId).isEmpty()) {
            throw new IllegalArgumentException("Пользователя, который запрашивает менторство, нет в бд");
        }

        if (userRepository.findById(receiverId).isEmpty()) {
            throw new IllegalArgumentException("Пользователя, которому направляют запрос на менторство, нет в бд");
        }

        MentorshipRequest mentorshipRequest = mentorshipRequestRepository
                .findLatestRequest(requesterId, receiverId).orElse(null);
        if (mentorshipRequest != null
            && mentorshipRequestDto.getCreatedAt().minusMonths(3).isBefore(mentorshipRequest.getCreatedAt())
        ) {
            throw new IllegalArgumentException("Запрос на менторство можно отправить только раз в 3 месяца");
        }
    }

}
