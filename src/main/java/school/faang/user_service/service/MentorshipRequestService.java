package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship_request.MentorshipResponseDto;
import school.faang.user_service.dto.mentorship_request.RejectionDto;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.RequestToResponseDto;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.filter.mentorship_request.RequestFilter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final RequestToResponseDto responseMapper;
    private final List<RequestFilter> filters;

    @Transactional
    public MentorshipResponseDto requestMentorship(MentorshipRequestDto request) {
        if (Objects.equals(request.requesterId(), request.receiverId())) {
            throw new IllegalArgumentException("The user cannot send a request to himself");
        }

        Optional<MentorshipRequest> optionalMentorshipRequest = mentorshipRequestRepository
                .findLatestRequest(request.requesterId(), request.receiverId());

        if (optionalMentorshipRequest.isPresent()) {
            MentorshipRequest mentorshipRequest = optionalMentorshipRequest.get();
            if (LocalDateTime.now().minusMonths(3L).isAfter(mentorshipRequest.getUpdatedAt())) {
                throw new IllegalArgumentException("It's been less than three months since the last request");
            }
        }

        MentorshipRequest newMentorshipRequest = mentorshipRequestRepository
                .create(request.requesterId(), request.receiverId(), request.description());


        return responseMapper.toDto(newMentorshipRequest);
    }

    public List<MentorshipResponseDto> getRequests(MentorshipRequestFilterDto filter) {
        List<MentorshipRequest> listRequest = new ArrayList<>();
        Iterable<MentorshipRequest> iterable = mentorshipRequestRepository.findAll();
        iterable.forEach(listRequest::add);

        return getFilteredRequest(listRequest, filter).map(responseMapper::toDto).toList();
    }

    @Transactional
    public void acceptRequest(Long id) {
        MentorshipRequest request = getMentorshipRequestById(id);

        User requester = request.getRequester();
        User receiver = request.getReceiver();

        if (receiver.getMentees().contains(requester)) {
            throw new IllegalArgumentException("You already have such a mentor.");
        }

        receiver.getMentees().add(requester);
        requester.getMentors().add(receiver);

        request.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(request);
    }

    @Transactional
    public void rejectRequest(Long id, RejectionDto rejection) {
        MentorshipRequest request = getMentorshipRequestById(id);

        if (!request.getStatus().equals(RequestStatus.PENDING)) {
            throw new IllegalArgumentException("The request has already been rejected");
        }
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.reason());
    }

    private Stream<MentorshipRequest> getFilteredRequest
            (List<MentorshipRequest> listRequest, MentorshipRequestFilterDto filterDto) {
        Stream<MentorshipRequest> requestStream = listRequest.stream();
        for (RequestFilter filter : filters) {
            if (filter.isApplicable(filterDto)) {
                requestStream = filter.apply(requestStream, filterDto);
            }
        }
        return requestStream;
    }

    private MentorshipRequest getMentorshipRequestById(Long id) {
        return mentorshipRequestRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("there is no such id"));
    }
}
