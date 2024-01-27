package school.faang.user_service.service.mentorship;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipRejectDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.filter.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Data
@Component
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final Map<List<Long>, List<Long>> mentorsAndUsers = new HashMap<>();
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        validateRequestMentorship(mentorshipRequestDto);

        mentorshipRequestRepository.create(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId(), mentorshipRequestDto.getDescription());

        MentorshipRequest mentorshipRequestEntity = mentorshipRequestMapper.MentorshipRequestToEntity(mentorshipRequestDto);
        mentorshipRequestEntity = mentorshipRequestRepository.save(mentorshipRequestEntity);
        return mentorshipRequestMapper.toMentorshipRequestDto(mentorshipRequestEntity);
    }

    private void validateRequestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (!isMoreThanThreeMonths(mentorshipRequestDto)) {
            throw new IllegalArgumentException("Less than 3 months have passed since last request");
        } else if (!userRepository.existsById(mentorshipRequestDto.getReceiverId())) {
            throw new IllegalArgumentException("There are no this receiver in data base");
        } else if (!userRepository.existsById(mentorshipRequestDto.getRequesterId())) {
            throw new IllegalArgumentException("There are no this requester in data base");
        } else if (!mentorshipRequestDto.getRequesterId().equals(mentorshipRequestDto.getReceiverId())) {
            throw new IllegalArgumentException("You can not send a request to yourself");
        }
    }

    private boolean isMoreThanThreeMonths(MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestRepository.findLatestRequest(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId())
                .map(mentorshipRequest -> LocalDateTime.now().isAfter(mentorshipRequest.getUpdatedAt().plusMonths(3)))
                .orElseThrow(() -> new IllegalArgumentException("There are not find request"));
    }

    public MentorshipRejectDto rejectRequest(long id, MentorshipRejectDto rejection) {
        if(!(mentorshipRequestRepository.existsById(id))){
            throw new IllegalArgumentException("There is no request in db with this ID");
        }

        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is blank request"));

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.getReason());
        mentorshipRequestRepository.save(mentorshipRequest);

        return mentorshipRequestMapper.toRejectionDto(mentorshipRequest);
    }

    public void acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no request in DB with this ID"));

        long mentorId = mentorshipRequest.getReceiver().getId();
        long senderId = mentorshipRequest.getRequester().getId();
        List<Long> mentorList = List.of(mentorId);

        if (mentorsAndUsers.containsKey(mentorList) && mentorsAndUsers.get(mentorList).contains(senderId)) {
            throw new IllegalArgumentException("The mentor is already the sender's mentor");
        }

        List<Long> senderList = mentorsAndUsers.getOrDefault(mentorList, new ArrayList<>());
        senderList.add(senderId);
        mentorsAndUsers.put(mentorList, senderList);

        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(mentorshipRequest);
    }

    public List<RequestFilterDto> getRequests(RequestFilterDto requestFilterDto) {
        Stream<MentorshipRequest> requestStream = mentorshipRequestRepository.findAll().stream();
        Stream<MentorshipRequest> filteredRequests = mentorshipRequestFilters.stream()
                .filter(mentorshipRequestFilter -> mentorshipRequestFilter.isApplicable(requestFilterDto))
                .flatMap(filter -> requestStream.flatMap(request -> filter.apply(Stream.of(request), requestFilterDto)));

        return mentorshipRequestMapper.toRequestFilterDtoList(filteredRequests.toList());
    }
}
