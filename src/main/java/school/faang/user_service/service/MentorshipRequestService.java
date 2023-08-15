package school.faang.user_service.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import school.faang.user_service.service.mentorship.filter.MentorshipRequestFilter;
import school.faang.user_service.validation.MentorshipRequestValidator;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@RequiredArgsConstructor
@Validated
public class MentorshipRequestService {
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;
    private final UserRepository userRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mapper;


    public MentorshipRequestDto requestMentorship(@Valid MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequest mentorshipRequest = mapper.toEntity(mentorshipRequestDto);

        Long requesterId = mentorshipRequest.getRequester().getId();
        Long receiverId = mentorshipRequest.getReceiver().getId();
        String description = mentorshipRequest.getDescription();

        if (!userRepository.existsById(requesterId)) {
            throw new IndexOutOfBoundsException("Requester must be registered");
        }
        if (!userRepository.existsById(receiverId)) {
            throw new IndexOutOfBoundsException("Receiver must be registered");
        }
        if (requesterId == receiverId) {
            throw new IllegalArgumentException("A requester cannot be a receiver fo itself");
        }

        Optional<MentorshipRequest> optionalLatestRequest = mentorshipRequestRepository.findLatestRequest(requesterId, receiverId);

        if (optionalLatestRequest.isPresent()) {
            MentorshipRequest latestRequest = optionalLatestRequest.get();
            if (latestRequest.getUpdatedAt().isAfter(LocalDateTime.now().minusMonths(3))) {
                throw new RuntimeException("Request can only be made once every 3 months");
            }
        }

        MentorshipRequest newRequest = mentorshipRequestRepository.create(requesterId, receiverId, description);
        return mapper.toDto(newRequest);
   }

   public List<MentorshipRequestDto> getRequests(RequestFilterDto filters) {
       List<MentorshipRequest> allRequests = new ArrayList<>();
       mentorshipRequestRepository.findAll().forEach(allRequests :: add);
       Stream<MentorshipRequest> requestsStream = allRequests.stream();

       return mentorshipRequestFilters.stream()
               .filter(filter -> filter.isApplicable(filters))
               .flatMap(filter -> filter.apply(requestsStream, filters))
               .map(request -> mapper.toDto(request))
               .toList();
   }
}
