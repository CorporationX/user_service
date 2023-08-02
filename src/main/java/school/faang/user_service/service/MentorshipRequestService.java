package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.filter.MentorshipRequestFilter;
import school.faang.user_service.validation.MentorshipRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class MentorshipRequestService {
    private final MentorshipRequestRepository repository;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;
    private final MentorshipRequestMapper mapper;
    private final MentorshipRequestValidator validator;

   public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequest mentorshipRequest = mapper.toEntity(mentorshipRequestDto);
        validator.validate(mentorshipRequest);

        Long requesterId = mentorshipRequest.getRequester().getId();
        Long receiverId = mentorshipRequest.getReceiver().getId();
        String description = mentorshipRequest.getDescription();

        MentorshipRequest newRequest = repository.create(requesterId, receiverId, description);
        return mapper.toDto(newRequest);
   }

   public List<MentorshipRequestDto> getRequests(RequestFilterDto filters) {
       List<MentorshipRequest> allRequests = new ArrayList<>();
       repository.findAll().forEach(allRequests :: add);
       Stream<MentorshipRequest> requestsStream = allRequests.stream();

       return mentorshipRequestFilters.stream()
               .filter(filter -> filter.isApplicable(filters))
               .flatMap(filter -> filter.apply(requestsStream, filters))
               .map(request -> mapper.toDto(request))
               .toList();
   }
}
