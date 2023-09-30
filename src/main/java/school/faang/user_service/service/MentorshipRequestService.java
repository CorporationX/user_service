package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.redis.MentorshipAcceptedEventDto;
import school.faang.user_service.dto.redis.MentorshipRequestedEventDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipAcceptedEventPublisher;
import school.faang.user_service.publisher.MentorshipRequestedEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipRequestedEventPublisher mentorshipRequestedEventPublisher;
    private final MentorshipAcceptedEventPublisher mentorshipAcceptedEventPublisher;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequest request = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        request.setStatus(RequestStatus.PENDING);
        MentorshipRequestDto savedRequestDto = mentorshipRequestMapper.toDto(mentorshipRequestRepository.save(request));

        mentorshipRequestedEventPublisher.publish(MentorshipRequestedEventDto.builder()
                .requesterId(savedRequestDto.getRequesterId())
                .receiverId(mentorshipRequestDto.getReceiverId())
                .createdAt(LocalDateTime.now())
                .build());

        mentorshipAcceptedEventPublisher.publish(MentorshipAcceptedEventDto.builder()
                .requesterId(savedRequestDto.getRequesterId())
                .receiverId(mentorshipRequestDto.getReceiverId())
                .requestId(savedRequestDto.getId())
                .createdAt(LocalDateTime.now())
                .build());
        return savedRequestDto;
    }
}
