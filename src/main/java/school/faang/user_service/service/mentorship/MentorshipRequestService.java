package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.mentorship.MentorshipFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipResponseDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.mentorship.MentorshipResponseMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.Validator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipResponseMapper mentorshipResponseMapper;
    private final List<Validator<MentorshipRequestDto>> validators;
    private final List<Filter<MentorshipFilterDto, MentorshipRequest>> filters;

    @Transactional
    public MentorshipResponseDto requestMentorship(MentorshipRequestDto dto) {

        validators.forEach(validator -> validator.validate(dto));

        MentorshipRequest request = mentorshipResponseMapper.toRequestEntity(dto);
        MentorshipRequest savedRequest = mentorshipRequestRepository.create(
                request.getRequester().getId(),
                request.getReceiver().getId(),
                request.getDescription()
        );

        return mentorshipResponseMapper.toResponseDto(savedRequest);
    }

    @Transactional
    public List<MentorshipResponseDto> getRequests(MentorshipFilterDto filterDto) {
        Stream<MentorshipRequest> filteredRequests = mentorshipRequestRepository.findAll().stream();

        for (Filter<MentorshipFilterDto, MentorshipRequest> mentorshipFilter : filters) {
            if (mentorshipFilter.isApplicable(filterDto)) {
                filteredRequests = mentorshipFilter.apply(filteredRequests, filterDto);
            }
        }

        return filteredRequests.map(mentorshipResponseMapper::toResponseDto).toList();
    }

    @Transactional
    public void acceptRequest(Long requestId) {
        MentorshipRequest request = mentorshipRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Id not found"));
        if (request.getStatus().equals(RequestStatus.ACCEPTED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user is already a mentor of the requester.");
        }
        request.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(request);
    }

    @Transactional
    public void rejectRequest(Long id, RejectionDto dto) {
        MentorshipRequest request = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Id not found"));
        if (request.getStatus().equals(RequestStatus.REJECTED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The request is already rejected.");
        }
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(dto.reason());
        mentorshipRequestRepository.save(request);
    }
}