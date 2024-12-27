package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipStartEvent;
import school.faang.user_service.dto.mentorship.MentorshipRequestedEvent;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapstruct.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipStartEventPublisher;
import school.faang.user_service.publisher.MentorshipRequestedEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private static final int THREE_MONTHS = 3;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipStartEventPublisher mentorshipStartEventPublisher;
    private final MentorshipRequestedEventPublisher mentorshipRequestedEventPublisher;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getRequesterId().equals(mentorshipRequestDto.getReceiverId())) {
            throw new IllegalArgumentException("You cannot request mentorship from yourself");
        }

        User requester = userRepository.findById(mentorshipRequestDto.getRequesterId())
                .orElseThrow(() -> new EntityNotFoundException("User not found by Id: " + mentorshipRequestDto.getRequesterId()));

        if (!requester.getSentMentorshipRequests().isEmpty()) {
            List<MentorshipRequest> sortedRequests = requester.getSentMentorshipRequests().stream()
                    .sorted((mentorshipRequest1, mentorshipRequest2) ->
                            mentorshipRequest2.getCreatedAt().compareTo(mentorshipRequest1.getCreatedAt()))
                    .toList();
            LocalDateTime maxDateLastRequest = LocalDateTime.now().minusMonths(3);
            if (sortedRequests.get(0).getCreatedAt().isAfter(maxDateLastRequest)) {
                throw new IllegalArgumentException("You can't request mentorship for less than 3 months");
            }
        }

        User receiver = userRepository.findById(mentorshipRequestDto.getReceiverId())
                .orElseThrow(() -> new EntityNotFoundException("User not found by Id: " + mentorshipRequestDto.getReceiverId()));

        MentorshipRequest mappedMentorshipRequest = mentorshipRequestMapper.mapToEntity(mentorshipRequestDto);

        mappedMentorshipRequest.setRequester(requester);
        mappedMentorshipRequest.setReceiver(receiver);
        mappedMentorshipRequest.setStatus(RequestStatus.PENDING);
        mappedMentorshipRequest.setDescription(mentorshipRequestDto.getDescription());
        mappedMentorshipRequest.setCreatedAt(LocalDateTime.now());

        MentorshipRequest savedMentorshipRequest = mentorshipRequestRepository.save(mappedMentorshipRequest);

        mentorshipRequestedEventPublisher.publish(
                MentorshipRequestedEvent.builder()
                        .menteeId(mentorshipRequestDto.getRequesterId())
                        .mentorId(mentorshipRequestDto.getReceiverId())
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        return mentorshipRequestMapper.mapToDto(savedMentorshipRequest);
    }


    @Transactional(readOnly = true)
    public List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilterDto) {
        List<MentorshipRequest> allRequests = mentorshipRequestRepository.findAll();
        List<MentorshipRequest> filteredRequests = new ArrayList<>();

        if (requestFilterDto.getStatus() != null) {
            filteredRequests.addAll(allRequests.stream()
                    .filter(res -> res.getStatus().equals(requestFilterDto.getStatus()))
                    .toList());
        }

        if (requestFilterDto.getRequesterId() != null) {
            filteredRequests.addAll(allRequests.stream()
                    .filter(res -> res.getRequester().getId().equals(requestFilterDto.getRequesterId()))
                    .toList());
        }

        if (requestFilterDto.getReceiverId() != null) {
            filteredRequests.addAll(allRequests.stream()
                    .filter(res -> res.getReceiver().getId().equals(requestFilterDto.getReceiverId()))
                    .toList());
        }

        if (requestFilterDto.getDescription() != null) {
            filteredRequests.addAll(allRequests.stream()
                    .filter(res -> res.getDescription().equals(requestFilterDto.getDescription()))
                    .toList());
        }

        return filteredRequests.stream()
                .distinct()
                .map(mentorshipRequestMapper::mapToDto)
                .toList();
    }

    @Transactional
    public MentorshipRequestDto acceptMentorship(Long id) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mentorship request not found" + id));

        if (mentorshipRequest.getStatus() == RequestStatus.ACCEPTED) {
            throw new IllegalStateException("This mentorship request has already been accepted.");
        } else if (mentorshipRequest.getStatus() == RequestStatus.REJECTED) {
            throw new IllegalStateException("This mentorship request has already been rejected.");
        }

        User requester = mentorshipRequest.getRequester();
        User receiver = mentorshipRequest.getReceiver();

        if (requester.getMentors().contains(receiver)) {
            throw new IllegalArgumentException("User is already a mentor");
        }

        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequest.setUpdatedAt(LocalDateTime.now());
        mentorshipRequestRepository.save(mentorshipRequest);

        requester.getMentors().add(receiver);
        userRepository.save(requester);

        mentorshipStartEventPublisher.publish(
                MentorshipStartEvent.builder()
                        .menteeId(requester.getId())
                        .mentorId(receiver.getId())
                        .build()
        );

        return mentorshipRequestMapper.mapToDto(mentorshipRequest);
    }

    @Transactional
    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MentorshipRequest not found"));
        if (mentorshipRequest.getStatus() == RequestStatus.REJECTED) {
            throw new IllegalStateException("This mentorship request has already been rejected.");
        }
        if (mentorshipRequest.getStatus() == RequestStatus.ACCEPTED) {
            throw new IllegalStateException("This mentorship request has already been accepted.");
        }
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.getReason());
        mentorshipRequest.setUpdatedAt(LocalDateTime.now());
        mentorshipRequestRepository.save(mentorshipRequest);
        return mentorshipRequestMapper.mapToDto(mentorshipRequest);
    }
}
