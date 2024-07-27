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

import static school.faang.user_service.validation.mentorship.mentorshipRequestValidator.*;

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
        long requesterId = mentorshipRequest.getRequester().getId();
        long receiverId = mentorshipRequest.getReceiver().getId();

//        Redundant tho, because the same check is done when checking if both users exists, but its more clear error
        validateSelfRequest(requesterId, receiverId);
        validateDescription(mentorshipRequest);

//        Ensure both users exist
        Collection<User> users = (Collection<User>) userRepository.findAllById(List.of(requesterId, receiverId));
        validateRequestUsers(users);

//        Check if USER made request in last 3 months, correct me if it should check for requests of requester responder pair

        Optional<MentorshipRequest> latestRequest = mentorshipRequestRepository.findLatestRequestByRequester(requesterId);
        validateLastRequestDate(latestRequest, MONTHS_COOLDOWN);

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
        List<MentorshipRequestDto> response = requests.stream().map(mentorshipRequestMapper::toDto).toList();

        validateRequestsCount(response);

        return response;
    }

    @Override
    public MentorshipRequestDto acceptRequest(long id) {
        MentorshipRequest request = findMembershipById(id);

        validateRequestAccepted(request);

        boolean mentorshipExists = mentorshipRepository.findByMentorAndMentee(request.getReceiver().getId(), request.getRequester().getId());

        validateMentorshipExistance(mentorshipExists);

        List<User> mentors = request.getRequester().getMentors();
        mentors.add(request.getReceiver());
        request.getRequester().setMentors(mentors);

        List<User> mentees = request.getReceiver().getMentors();
        mentees.add(request.getRequester());
        request.getReceiver().setMentees(mentees);

        request.setStatus(RequestStatus.ACCEPTED);
        MentorshipRequest response = mentorshipRequestRepository.save(request);
        return mentorshipRequestMapper.toDto(response);
    }

    @Override
    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest request = findMembershipById(id);
        validateRequestAccepted(request);
        validateRequestRejected(request);

        request.setStatus(RequestStatus.REJECTED);
        request.setDescription(rejection.getReason());

        MentorshipRequest response = mentorshipRequestRepository.save(request);
        return mentorshipRequestMapper.toDto(response);
    }

    public MentorshipRequest findMembershipById(long id) {
        return mentorshipRequestRepository.findById(id)
                                          .orElseThrow(() -> new NoSuchElementException("Mentorship request with id " + id + " doesnt exist"));
    }
}
