package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;

@Component
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;


    @Autowired
    public MentorshipRequestService(MentorshipRequestRepository mentorshipRequestRepository, UserRepository userRepository, MentorshipRequestMapper mentorshipRequestMapper) {
        this.mentorshipRequestRepository = mentorshipRequestRepository;
        this.userRepository = userRepository;
        this.mentorshipRequestMapper = mentorshipRequestMapper;
    }

    @Transactional
    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) throws Exception {
        Long requesterId = mentorshipRequestDto.getRequesterId();
        Long receiverId = mentorshipRequestDto.getReceiverId();

        if (requesterId.equals(receiverId)) {
            throw new Exception("Нельзя назначить себя ментором!");
        }

        if (!userRepository.existsById(requesterId) || !userRepository.existsById(receiverId)) {
            throw new Exception("Пользователь не найден");
        }

        mentorshipRequestRepository.findLatestRequest(requesterId, receiverId).ifPresent((n) -> {
            if (LocalDateTime.now().getMonthValue() - n.getUpdatedAt().getMonthValue() < 3) {
                try {
                    throw new Exception("Запросить ментора можно только раз в 3 месяца");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        mentorshipRequestRepository.save(mentorshipRequest);
    }
}
