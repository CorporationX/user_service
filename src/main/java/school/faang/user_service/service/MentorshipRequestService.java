package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
        mentorshipRequestRepository.create(requesterId, receiverId, mentorshipRequestDto.getDescription());
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filterDto) {
        Iterable<MentorshipRequest> iterable = mentorshipRequestRepository.findAll();
        List<MentorshipRequest> requests = StreamSupport.stream(iterable.spliterator(), false)
                .toList();

        return requests.stream()
                .filter(request -> filterDto.getDescription() == null || request.getDescription().contains(filterDto.getDescription()))
                .filter(request -> filterDto.getRequesterId() == null || request.getRequester().getId() == (filterDto.getRequesterId()))
                .filter(request -> filterDto.getReceiverId() == null || request.getReceiver().getId() == (filterDto.getReceiverId()))
                .filter(request -> filterDto.getStatus() == null || request.getStatus().equals(filterDto.getStatus()))
                .map(mentorshipRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public void acceptRequest(long id) {
        Optional<MentorshipRequest> requestOptional = mentorshipRequestRepository.findById(id);

        if (requestOptional.isEmpty()) {
            throw new RuntimeException("Запрос не найден");
        }

        MentorshipRequest request = requestOptional.get();

        if (request.getStatus() == RequestStatus.ACCEPTED){
            throw new RuntimeException("Запрос уже получен");
        }

        request.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(request);
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        Optional<MentorshipRequest> requestOptional = mentorshipRequestRepository.findById(id);

        if (requestOptional.isEmpty()) {
            throw new RuntimeException("Запрос не найден");
        }

        MentorshipRequest request = requestOptional.get();

        if (request.getStatus() == RequestStatus.REJECTED) {
            throw new RuntimeException("Запрос уже отклонен");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());
        mentorshipRequestRepository.save(request);
    }
}
