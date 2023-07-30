package school.faang.user_service.service.mentorshipRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshipRequest.RejectionDto;
import school.faang.user_service.dto.mentorshipRequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.MentorshipRequestFilter;
import school.faang.user_service.mapper.mentorshipRequest.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.util.mentorshipRequest.exception.NoRequestsException;
import school.faang.user_service.util.mentorshipRequest.exception.RequestIsAlreadyAcceptedException;
import school.faang.user_service.util.mentorshipRequest.exception.RequestIsAlreadyRejectedException;
import school.faang.user_service.util.mentorshipRequest.validator.MentorshipRequestValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final UserRepository userRepository;
    private final List<MentorshipRequestFilter> filters;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto dto) {
        mentorshipRequestValidator.validate(dto);
        MentorshipRequest request = mentorshipRequestMapper.toEntity(dto);
        mentorshipRequestRepository.save(request);

        return mentorshipRequestMapper.toDto(request);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {
        Stream<MentorshipRequest> requests = mentorshipRequestRepository.findAll().stream();

        for (MentorshipRequestFilter f : filters) {
            if (f.isApplicable(filter)) {
                requests = f.apply(requests, filter);
            }
        }

        return requests.map(mentorshipRequestMapper::toDto).toList();
    }

    @Transactional
    public MentorshipRequestDto acceptRequest(long id) {
        MentorshipRequest foundRequest =
                mentorshipRequestRepository.findById(id).orElseThrow(NoRequestsException::new);

        User receiver = foundRequest.getReceiver();
        User requester = foundRequest.getRequester();
        boolean doesContain = receiver.getMentees().contains(requester);

        if (foundRequest.getStatus().equals(RequestStatus.ACCEPTED) || doesContain) {
            throw new RequestIsAlreadyAcceptedException();
        }

        foundRequest.setStatus(RequestStatus.ACCEPTED);
        receiver.getMentees().add(requester);
        requester.getMentors().add(receiver);

        mentorshipRequestRepository.save(foundRequest);
        userRepository.save(receiver);
        userRepository.save(requester);

        return mentorshipRequestMapper.toDto(foundRequest);
    }

    @Transactional
    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejectionDto) {
        MentorshipRequest foundRequest = mentorshipRequestRepository.findById(id)
                .orElseThrow(NoRequestsException::new);

        User receiver = foundRequest.getReceiver();
        User requester = foundRequest.getRequester();

        if (foundRequest.getStatus().equals(RequestStatus.REJECTED)) {
            throw new RequestIsAlreadyRejectedException();
        }

        foundRequest.setStatus(RequestStatus.REJECTED);
        receiver.getMentees().remove(requester);
        requester.getMentors().remove(receiver);
        foundRequest.setRejectionReason(rejectionDto.getReason());

        mentorshipRequestRepository.save(foundRequest);
        userRepository.save(receiver);
        userRepository.save(requester);

        return mentorshipRequestMapper.toDto(foundRequest);
    }
}
