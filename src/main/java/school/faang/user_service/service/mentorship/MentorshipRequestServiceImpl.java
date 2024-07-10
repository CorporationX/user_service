package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MentorshipRequestServiceImpl implements MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final static int MONTHS_COOLDOWN = 3;

    @Override
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        long requesterId = mentorshipRequest.getRequester()
                .getId();
        long receiverId = mentorshipRequest.getReceiver()
                .getId();

//        Redundant tho, because the same check is done when checking if both users exists, but its more clear error
        if (requesterId == receiverId) {
            throw new IllegalArgumentException("Cannot request from yourself");
        }

        if (mentorshipRequest.getDescription()
                .length() < 4) {
            throw new IllegalArgumentException("Mentorship description is too short, it should be at least 4 characters");
        }

//        Ensure both users exist
        Iterable<User> users = userRepository.findAllById(List.of(requesterId, receiverId));
        if (((Collection<User>) users).size() != 2) {
            throw new NoSuchElementException("One or both users not found");
        }

//        Check if USER made request in last 3 months, correct me if it should check for requests of requester responder pair
        LocalDateTime cooldownThreshold = LocalDateTime.now()
                .minusMonths(MONTHS_COOLDOWN);
        Optional<MentorshipRequest> latestRequest = mentorshipRequestRepository.findLatestRequestByRequester(requesterId);

        if (latestRequest.isPresent() && latestRequest.get()
                .getCreatedAt()
                .isAfter(cooldownThreshold)) {
            throw new IllegalArgumentException("A mentorship request has already been made within the last " + MONTHS_COOLDOWN + " months");
        }
        MentorshipRequest response = mentorshipRequestRepository.save(mentorshipRequest);
        return mentorshipRequestMapper.toDto(response);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {

        Iterable<MentorshipRequest> filteredRequests = mentorshipRequestRepository.findAllByFilter(filter.getDescription(), filter.getRequesterId(), filter.getResponderId(), filter.getStatus()
                .ordinal());
        List<MentorshipRequest> requests = new ArrayList<>();
        filteredRequests.forEach(requests::add);
        List<MentorshipRequestDto> response = requests.stream()
                .map(mentorshipRequestMapper::toDto)
                .toList();

        if(response.isEmpty()) {
            throw new NoSuchElementException("No mentorship requests found");
        }

        return response;
    }

    @Override
    public MentorshipRequestDto acceptRequest(long id) {
        MentorshipRequest request = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Mentorship request with id " + id + " doesnt exist"));

        if (request.getStatus() == RequestStatus.ACCEPTED) {
            throw new IllegalArgumentException("Mentorship request with id " + id + " already accepted");
        }

        boolean mentorshipExists = mentorshipRepository.findByMentorAndMentee(request.getReceiver()
                                                                                      .getId(), request.getRequester()
                                                                                      .getId());

        if (mentorshipExists) {
            throw new IllegalArgumentException("Mentorship already exists");
        }

        request.getRequester()
                .setMentors(List.of(request.getReceiver()));

        request.setStatus(RequestStatus.ACCEPTED);
        MentorshipRequest response = mentorshipRequestRepository.save(request);
        return mentorshipRequestMapper.toDto(response);
    }

    @Override
    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest request = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Mentorship request with id " + id + " doesnt exist"));
        if (request.getStatus() == RequestStatus.ACCEPTED) {
            throw new IllegalArgumentException("Mentorship request with id " + id + " already accepted");
        } else if (request.getStatus() == RequestStatus.REJECTED) {
            throw new IllegalArgumentException("Mentorship request with id " + id + " already rejected");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setDescription(rejection.getReason());

        MentorshipRequest response = mentorshipRequestRepository.save(request);
        System.out.println(response.getStatus());
        System.out.println(mentorshipRequestMapper.toDto(response));
        return mentorshipRequestMapper.toDto(response);
    }
}
