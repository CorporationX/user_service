package school.faang.user_service.service;


import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import school.faang.user_service.service.mentorship_request_filter.MentorshipRequestDescrFilter;
import school.faang.user_service.service.mentorship_request_filter.MentorshipRequestFilter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship_request_filter.MentorshipRequestReceiverFilter;
import school.faang.user_service.service.mentorship_request_filter.MentorshipRequestRequesterFilter;
import school.faang.user_service.service.mentorship_request_filter.MentorshipRequestStatusFilter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;;

@Component
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final UserRepository userRepository;
    // Написал так, потому что тесты не работали - requestFilters был пустым
    private final List<MentorshipRequestFilter> requestFilters = new ArrayList<>(
            List.of(
                    new MentorshipRequestDescrFilter(),
                    new MentorshipRequestReceiverFilter(),
                    new MentorshipRequestRequesterFilter(),
                    new MentorshipRequestStatusFilter()
            )
    );;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        validateMentorshipRequest(mentorshipRequestDto);
        MentorshipRequest mentorshipRequestEntity = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        return mentorshipRequestMapper.toDto(mentorshipRequestRepository.save(mentorshipRequestEntity));
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
                .map(mentorshipRequestMapper::toDto)
                .toList();
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
