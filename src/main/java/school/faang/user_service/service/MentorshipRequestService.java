package school.faang.user_service.service;

import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

@Service
public class MentorshipRequestService {
    MentorshipRequestRepository repository;
    MentorshipRequestMapper mapper;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {

        // Валидация параметров



        MentorshipRequest mentorshipRequest = mapper.toEntity(mentorshipRequestDto);

        User requester = repository.getReferenceById(mentorshipRequestDto.getUserRequesterId()).getRequester();
        mentorshipRequest.setRequester(requester);
        User receiver = repository.getReferenceById(mentorshipRequestDto.getUserReceiverId()).getReceiver();
        mentorshipRequest.setRequester(receiver);

        mentorshipRequest = repository.save(mentorshipRequest);

        return mapper.toDto(repository.save(mentorshipRequest));
    }
}
