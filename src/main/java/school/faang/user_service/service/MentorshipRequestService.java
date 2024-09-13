package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto_mentorship.MentorshipRequestDto;
import school.faang.user_service.dto_mentorship.RejectionDto;
import school.faang.user_service.dto_mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.filter.mentorship.MentorshipRequestRepository;
import school.faang.user_service.filter.mentorship.filter.MentorshipRequestFilter;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor

public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;

    public void mentorshipRequestValidation(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription() == null || mentorshipRequestDto.getDescription().trim().isEmpty()) {
            throw new NoSuchElementException("Need request description");
        }
    }

    public MentorshipRequest checkingUsersInRepository(MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequest requestEntity = mentorshipRequestMapper.toEntity(mentorshipRequestDto);

        requestEntity.setRequester(userRepository.findById(mentorshipRequestDto.getRequesterId())
                .orElseThrow(() -> new NoSuchElementException("Requester %s not found".formatted(mentorshipRequestDto
                        .getRequesterId()))));

        requestEntity.setReceiver(userRepository.findById(mentorshipRequestDto.getReceiverId())
                .orElseThrow(() -> new NoSuchElementException("Receiver %s not found".formatted(mentorshipRequestDto
                        .getRequesterId()))));

        return requestEntity;
    }

    public void checkingForIdenticalIdsUsers(MentorshipRequest requestEntity) {
        if (requestEntity.getRequester()
                .equals(requestEntity.getReceiver())) {
            throw new NoSuchElementException("Your request cannot be accepted");
        }
    }

    public void spamCheck(MentorshipRequestDto mentorshipRequestDto) {
        List<MentorshipRequest> mentorshipRequestList = mentorshipRequestRepository
                .findAllByRequesterId(mentorshipRequestDto.getRequesterId());

        MentorshipRequest lastMentorshipRequest = mentorshipRequestList.stream()
                .sorted(Comparator.comparing(MentorshipRequest::getCreatedAt)).findFirst().get();

        LocalDateTime lastRequestDate = lastMentorshipRequest.getCreatedAt();

        if (!LocalDateTime.now().isAfter(lastRequestDate.plusMonths(3))) {
            throw new RuntimeException("Request limit exceeded");
        }
    }

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestValidation(mentorshipRequestDto);
        MentorshipRequest requestEntity = checkingUsersInRepository(mentorshipRequestDto);
        checkingForIdenticalIdsUsers(requestEntity);
        spamCheck(mentorshipRequestDto);
        return mentorshipRequestMapper.toDto(mentorshipRequestRepository.save(requestEntity));
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filters) {
        Stream<MentorshipRequest> mentorshipRequestStream = mentorshipRequestRepository.findAll().stream();
        return mentorshipRequestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(mentorshipRequestStream,
                        (requestStream, filter) -> filter.apply(requestStream, filters),
                        (s1, s2) -> s1)
                .map(mentorshipRequestMapper::toDto)
                .toList();
    }

    public MentorshipRequest getRequestById(long id) {
        return mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Request does not exist"));
    }

    public void acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = getRequestById(id);

        if (!mentorshipRequestRepository
                .existAcceptedRequest(mentorshipRequest.getRequester().getId(),
                        mentorshipRequest.getReceiver().getId())) {

            User user = mentorshipRequest.getRequester();
            user.getMentors().add(mentorshipRequest.getReceiver());
            mentorshipRequest.setStatus(RequestStatus.ACCEPTED);

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
