package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.util.exception.NoRequestsException;
import school.faang.user_service.util.exception.RequestIsAlreadyAcceptedException;
import school.faang.user_service.util.exception.UserNotFoundException;
import school.faang.user_service.util.validator.FilterRequestStatusValidator;
import school.faang.user_service.util.validator.MentorshipRequestValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final FilterRequestStatusValidator filterRequestStatusValidator;
    private final UserRepository userRepository;

    @Transactional
    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto, this);

        long requesterId = mentorshipRequest.getRequester().getId();
        long receiverId = mentorshipRequest.getReceiver().getId();

        Optional<User> requester = userRepository.findById(requesterId);
        Optional<User> receiver = userRepository.findById(receiverId);
        Optional<MentorshipRequest> latestRequest = mentorshipRequestRepository.findLatestRequest(requesterId, receiverId);

        mentorshipRequestValidator.validate(requester, receiver, latestRequest);

        mentorshipRequestRepository.save(mentorshipRequest);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {
        MentorshipRequest entity = mentorshipRequestMapper.toEntity(filter, this,
                filterRequestStatusValidator);

        return StreamSupport.stream(mentorshipRequestRepository.findAll().spliterator(), false)
                .filter(mentorshipRequest -> filterRequests(entity, mentorshipRequest))
                .map(mentorshipRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    private boolean filterRequests(MentorshipRequest entity, MentorshipRequest requestFromDB) {
        if (entity.getDescription() != null && !requestFromDB.getDescription().equals(entity.getDescription())) {
            return false;
        }
        if (entity.getRequester() != null && !requestFromDB.getRequester().equals(entity.getRequester())) {
            return false;
        }
        if (entity.getReceiver() != null && !requestFromDB.getReceiver().equals(entity.getReceiver())) {
            return false;
        }
        if (entity.getStatus() != null && !requestFromDB.getStatus().equals(entity.getStatus())) {
            return false;
        }

        return true;
    }

    public User findUserById(long id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public void acceptRequest(long id) {
        MentorshipRequest foundRequest =
                mentorshipRequestRepository.findById(id).orElseThrow(NoRequestsException::new);

        boolean doesContain = foundRequest.getReceiver().getMentees().contains(foundRequest.getRequester());

        if (foundRequest.getStatus().equals(RequestStatus.ACCEPTED) || doesContain) {
            throw new RequestIsAlreadyAcceptedException();
        }

        foundRequest.setStatus(RequestStatus.ACCEPTED);

        mentorshipRequestRepository.save(foundRequest);
    }
}
