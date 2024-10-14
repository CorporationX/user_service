package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.mapper.RejectionMapper;
import school.faang.user_service.dto.MentorshipRequestEvent;
import school.faang.user_service.publisher.MentorshipRequestPublisher;
import school.faang.user_service.repository.MentorshipRequestRepository;
import school.faang.user_service.service.MentorshipRequestService;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;
    private final MentorshipRequestValidator validator;
    private final RejectionMapper rejectionMapper;
    private final MentorshipRequestPublisher mentorshipRequestPublisher;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        validator.mentorshipRequestValidation(mentorshipRequestDto);
        mentorshipRequestDto = validator.checkingUsersInRepository(mentorshipRequestDto);
        validator.checkingForIdenticalIdsUsers(mentorshipRequestDto);
        validator.spamCheck(mentorshipRequestDto);
        publishEvent(mentorshipRequestDto);
        return mentorshipRequestDto;
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
            mentorshipRequestRepository.save(mentorshipRequest);

        } else {
            throw new NoSuchElementException("This user is already your mentor");
        }
    }

    public RejectionDto rejectRequest(Long id, RejectionDto rejectionDto) {
        MentorshipRequest entity = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Request does not exist"));

        if (entity.getDescription() == null || entity.getDescription().trim().isEmpty()) {
            throw new NoSuchElementException("Need rejection description");
        }

        entity.setRejectionReason(rejectionDto.getRejectionReason());
        entity.setStatus(RequestStatus.REJECTED);

        mentorshipRequestRepository.save(entity);

        return rejectionMapper.toDto(entity);
    }

    private void publishEvent(MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequestEvent event = new MentorshipRequestEvent();
        event.setRequesterId(mentorshipRequestDto.getRequesterId());
        event.setReceiverId(mentorshipRequestDto.getReceiverId());
        event.setRequestTime(mentorshipRequestDto.getCreatedAt());
        mentorshipRequestPublisher.publish(event);
    }
}
