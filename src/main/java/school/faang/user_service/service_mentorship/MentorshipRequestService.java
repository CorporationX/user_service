package school.faang.user_service.service_mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto_mentorship.MentorshipRequestDto;
import school.faang.user_service.dto_mentorship.RejectionDto;
import school.faang.user_service.dto_mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper_mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
@RequiredArgsConstructor

public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipRepository mentorshipRepository;


    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription() == null || mentorshipRequestDto.getDescription().trim().isEmpty()) {
            throw new NoSuchElementException("Need request description");
        }

        MentorshipRequest requestEntity = mentorshipRequestMapper.toEntity(mentorshipRequestDto);

        requestEntity.setRequester(userRepository.findById(mentorshipRequestDto.getRequesterId())
                .orElseThrow(() -> new NoSuchElementException("User %s not found".formatted(mentorshipRequestDto
                        .getRequesterId()))));

        requestEntity.setReceiver(userRepository.findById(mentorshipRequestDto.getReceiverId())
                .orElseThrow(() -> new NoSuchElementException("User %s not found".formatted(mentorshipRequestDto
                        .getRequesterId()))));

        if (userRepository.findById(mentorshipRequestDto.getReceiverId())
                .equals(userRepository.findById(mentorshipRequestDto.getRequesterId()))) {
            throw new NoSuchElementException("Your request cannot be accepted");
        }

        List<MentorshipRequest> mentorshipRequestList = mentorshipRequestRepository
                .findAllByRequesterId(mentorshipRequestDto.getRequesterId());

        MentorshipRequest lastMentorshipRequest = mentorshipRequestList.stream()
                .sorted(Comparator.comparing(MentorshipRequest::getCreatedAt)).findFirst().get();

        LocalDateTime lastRequestDate = lastMentorshipRequest.getCreatedAt();

        if (!LocalDateTime.now().isAfter(lastRequestDate.plusMonths(3))) {
            throw new RuntimeException("Request limit exceeded");
        }

        return mentorshipRequestMapper.toDto(mentorshipRequestRepository.save(requestEntity));
    }

    public List<MentorshipRequest> getRequests(RequestFilterDto filter) {
        List<MentorshipRequest> mentorshipRequestList = mentorshipRequestRepository.findAll();

        if (filter == null) {
            return mentorshipRequestList;
        }

        return mentorshipRequestList.stream()
                .filter(req -> filter.getDescription() == null || req.getDescription().contains(filter.getDescription()))
                .filter(req -> filter.getRequester() == null || req.getRequester().equals(filter.getDescription()))
                .filter(req -> filter.getReceiver() == null || req.getReceiver().equals(filter.getReceiver()))
                .filter(req -> filter.getStatus() == null || req.getStatus().equals(filter.getStatus()))
                .toList();
    }

    public void acceptRequest(long id) {

        MentorshipRequest request = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Request does not exist"));

        if (!mentorshipRequestRepository
                .existAcceptedRequest(request.getRequester().getId(), request.getReceiver().getId())) {

            User user = request.getRequester();
            user.getMentors().add(request.getReceiver());
            request.setStatus(RequestStatus.ACCEPTED);

        } else {
            throw new NoSuchElementException("This user is already your mentor");
        }
    }

    public RejectionDto rejectRequest(long id, RejectionDto rejectionDto) {
        MentorshipRequest entity = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Request does not exist"));

        if (entity.getDescription() == null || entity.getDescription().trim().isEmpty()) {
            throw new NoSuchElementException("Need rejection description");
        }

        entity.setRejectionReason(rejectionDto.getRejectionReason());

        entity.setStatus(RequestStatus.REJECTED);

        return mentorshipRequestMapper.toDto(id, entity);
    }
}
