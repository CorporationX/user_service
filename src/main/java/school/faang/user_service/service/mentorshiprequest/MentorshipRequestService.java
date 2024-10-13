package school.faang.user_service.service.mentorshiprequest;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorshiprequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshiprequest.RejectionDto;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;
import school.faang.user_service.dto.message.MentorshipRequestMessage;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.filter.mentorshiprequest.MentorshipRequestFilter;
import school.faang.user_service.mapper.mentorshiprequest.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipRequestEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.mentorshiprequst.MentorshipRequestValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository menReqRepository;
    private final MentorshipRequestMapper menReqMapper;
    private final MentorshipRequestValidator menReqValidator;
    private final List<MentorshipRequestFilter> filtersRequests;
    private final MentorshipRequestEventPublisher publisher;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto menReqDto) {

        var message = MentorshipRequestMessage.builder()
                .requesterId(menReqDto.getRequesterId())
                .receiverId(menReqDto.getReceiverId())
                .createdAt(menReqDto.getCreatedAt())
                .build();

        publisher.publish(message);

        menReqValidator.validateReceiverNoEqualsRequester(menReqDto);
        menReqValidator.validateAvailabilityUsersDB(menReqDto);

        Optional<MentorshipRequest> menReqOptional =
                menReqRepository.findLatestRequest(menReqDto.getRequesterId(), menReqDto.getReceiverId());

        menReqOptional.ifPresent(menReqValidator::validateDataCreateRequest);

        MentorshipRequest menReq = menReqRepository.create(menReqDto.getRequesterId(),
                menReqDto.getReceiverId(),
                menReqDto.getDescription());

        return menReqMapper.toDto(menReq);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {
        Stream<MentorshipRequest> requests = menReqRepository.findAll().stream();

        return filtersRequests.stream()
                .filter(filterRequest -> filterRequest.isApplicable(filter))
                .flatMap(filterRequest -> filterRequest.apply(requests, filter))
                .map(menReqMapper::toDto)
                .toList();
    }

    public MentorshipRequestDto acceptRequest(long id) {
        MentorshipRequest menReq = menReqRepository.findById(id).map(mentorshipRequest -> {

            menReqValidator.validateMentorsContainsReceiver(mentorshipRequest);

            mentorshipRequest.getReceiver().getMentors().add(mentorshipRequest.getRequester());
            mentorshipRequest.setStatus(RequestStatus.ACCEPTED);

            return mentorshipRequest;
        }).orElseThrow(() -> {

            entityNotFoundException(id);
            return null;
        });

        return menReqMapper.toDto(menReqRepository.save(menReq));
    }

    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest menReq = menReqRepository.findById(id).map(mentorshipRequest -> {

            mentorshipRequest.setStatus(RequestStatus.REJECTED);
            mentorshipRequest.setRejectionReason(rejection.getReason());


            return mentorshipRequest;
        }).orElseThrow(() -> {
            entityNotFoundException(id);
            return null;
        });

        return menReqMapper.toDto(menReqRepository.save(menReq));
    }

    private void entityNotFoundException(long id) {
        throw new EntityNotFoundException("No such request was found " + id);
    }
}
