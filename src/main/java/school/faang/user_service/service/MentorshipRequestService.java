package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorshipRequest.MentorshipEventDto;
import school.faang.user_service.dto.mentorshipRequest.MentorshipAcceptedDto;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshipRequest.RejectionDto;
import school.faang.user_service.dto.mentorshipRequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityStateException;
import school.faang.user_service.exception.notFoundExceptions.MentorshipRequestNotFoundException;
import school.faang.user_service.filter.mentorshiprequest.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.messaging.MentorshipEventPublisher.MentorshipEventPublisher;
import school.faang.user_service.messaging.MentorshipAcceptedEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.util.validator.MentorshipRequestValidator;

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
    private final MentorshipAcceptedEventPublisher mentorshipAcceptedEventPublisher;
    private final List<MentorshipRequestFilter> filters;
    private final MentorshipEventPublisher mentorshipEventPublisher;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto dto) {
        mentorshipRequestValidator.validate(dto);
        MentorshipRequest request = mentorshipRequestMapper.toEntity(dto);
        mentorshipRequestRepository.save(request);
        mentorshipEventPublisher.publish(new MentorshipEventDto(dto.getRequesterId(), dto.getReceiverId()));
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
                mentorshipRequestRepository.findById(id).orElseThrow(MentorshipRequestNotFoundException::new);

        User receiver = foundRequest.getReceiver();
        User requester = foundRequest.getRequester();
        boolean doesContain = receiver.getMentees().contains(requester);

        if (foundRequest.getStatus().equals(RequestStatus.ACCEPTED) || doesContain) {
            throw new EntityStateException("Request already accepted");
        }

        foundRequest.setStatus(RequestStatus.ACCEPTED);
        receiver.getMentees().add(requester);
        requester.getMentors().add(receiver);

        MentorshipRequest saved = mentorshipRequestRepository.save(foundRequest);
        userRepository.save(receiver);
        userRepository.save(requester);

        MentorshipAcceptedDto toPublish = mentorshipRequestMapper.toAcceptedDto(saved);
        mentorshipAcceptedEventPublisher.publish(toPublish);

        return mentorshipRequestMapper.toDto(foundRequest);
    }

    @Transactional
    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejectionDto) {
        MentorshipRequest foundRequest = mentorshipRequestRepository.findById(id)
                .orElseThrow(MentorshipRequestNotFoundException::new);

        User receiver = foundRequest.getReceiver();
        User requester = foundRequest.getRequester();

        if (foundRequest.getStatus().equals(RequestStatus.REJECTED)) {
            throw new EntityStateException("Request already rejected");
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
