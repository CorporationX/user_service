package school.faang.user_service.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.util.Objects;;

@Component
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final UserRepository userRepository;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        validateMentorshipRequest(mentorshipRequestDto);
        MentorshipRequest mentorshipRequestEntity = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        return mentorshipRequestMapper.toDto(mentorshipRequestRepository.save(mentorshipRequestEntity));
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
