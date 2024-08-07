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
import school.faang.user_service.validator.MentorshipRequestValidator;

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
    private final MentorshipRequestValidator mentorshipRequestValidator;


    @Autowired
    public MentorshipRequestService(MentorshipRequestRepository mentorshipRequestRepository, UserRepository userRepository,
                                    MentorshipRequestMapper mentorshipRequestMapper, MentorshipRequestValidator mentorshipRequestValidator) {

        this.mentorshipRequestRepository = mentorshipRequestRepository;
        this.userRepository = userRepository;
        this.mentorshipRequestMapper = mentorshipRequestMapper;
        this.mentorshipRequestValidator = mentorshipRequestValidator;
    }

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) throws Exception {
        Long requesterId = mentorshipRequestDto.getRequesterId();
        Long receiverId = mentorshipRequestDto.getReceiverId();

        mentorshipRequestValidator.validateRequestMentorship(mentorshipRequestDto);

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
        List<MentorshipRequest> requests = StreamSupport.stream(mentorshipRequestRepository.findAll().spliterator(), false)
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
        mentorshipRequestValidator.validateAcceptRequest(requestOptional);
        MentorshipRequest request = requestOptional.get();
        request.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(request);
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        Optional<MentorshipRequest> requestOptional = mentorshipRequestRepository.findById(id);

        mentorshipRequestValidator.validateRejectRequest(requestOptional);
        MentorshipRequest request = requestOptional.get();
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());
        mentorshipRequestRepository.save(request);
    }
}
