package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.MentorshipRequestFilter.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.publisher.mentorship.MentorshipAcceptEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestServiceImpl implements MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;
    private final MentorshipAcceptEventPublisher mentorshipAcceptEventPublisher;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto dto) {
        if (dto.getRequesterId().equals(dto.getReceiverId())) {
            throw new DataValidationException("Cannot request mentorship from yourself");
        }
        User requester = userRepository.findById(dto.getRequesterId())
                .orElseThrow(() -> new EntityNotFoundException("Requester not found"));
        User receiver = userRepository.findById(dto.getReceiverId())
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found"));

        Optional<MentorshipRequest> latest = mentorshipRequestRepository.findLatestRequest(dto.getRequesterId(), dto.getReceiverId());
        if (latest.isPresent() && latest.get().getCreatedAt().isAfter(LocalDateTime.now().minusMonths(3))) {
            throw new DataValidationException("Mentorship request can only be sent once every 3 months");
        }

        MentorshipRequest request = new MentorshipRequest();
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setDescription(dto.getDescription());
        request.setStatus(RequestStatus.PENDING);

        MentorshipRequest created = mentorshipRequestRepository.save(request);
        return mentorshipRequestMapper.toDto(created);
    }

    @Transactional(readOnly = true)
    public List<MentorshipRequestDto> getRequests(RequestFilterDto requestFilterDto) {
        Stream<MentorshipRequest> filteredRequestForMentorship = mentorshipRequestRepository.findAll().stream();

        for (MentorshipRequestFilter mentorshipRequestFilter : mentorshipRequestFilters) {
            if (mentorshipRequestFilter.isApplicable(requestFilterDto)) {
                filteredRequestForMentorship = mentorshipRequestFilter.apply(filteredRequestForMentorship, requestFilterDto);
            }
        }

        return filteredRequestForMentorship.map(mentorshipRequestMapper::toDto).toList();
    }

    @Transactional
    public void acceptRequest(long id) {
        MentorshipRequest request = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));
        User mentee = request.getRequester();
        User mentor = request.getReceiver();

        if (mentee.getMentors().contains(mentor) && request.getStatus().equals(RequestStatus.ACCEPTED)) {
            throw new DataValidationException("User is already your mentor");
        }

        mentee.getMentors().add(mentor);
        request.setStatus(RequestStatus.ACCEPTED);
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.save(request);
        log.info("Менторство принято. Ментор id: {}, Ученик id: {}.", mentor.getId(), mentee.getId());
        log.info("Статус: {}", mentorshipRequest.getStatus());
        MentorshipRequestDto mentorshipRequestDto = mentorshipRequestMapper.toDto(mentorshipRequest);
        mentorshipAcceptEventPublisher.publish(mentorshipRequestDto);
        log.info("Сообщение о принятии менторства отправлено успешно.");
    }

    @Transactional
    public void rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest request = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());
        mentorshipRequestRepository.save(request);
    }
}

