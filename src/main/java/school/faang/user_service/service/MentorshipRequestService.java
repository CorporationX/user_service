package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDro;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.mentorship_request.MentorshipRequestFilter;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    @Transactional
    public void requestMentorship(MentorshipRequestDto requestDto) {
        long requesterId = requestDto.getRequesterId();
        long receiverId = requestDto.getReceiverId();

        validateExistsUsers(requesterId, receiverId);
        mentorshipRequestValidator.validateUserIds(requesterId, receiverId);
        mentorshipRequestValidator.validateRequestTime(requesterId, receiverId);

        mentorshipRequestRepository.create(requesterId, receiverId, requestDto.getDescription());
    }

    @Transactional(readOnly = true)
    public List<MentorshipRequestDto> getRequests(RequestFilterDro filters) {
        List<MentorshipRequest> requests = mentorshipRequestRepository.findAll();

        for (MentorshipRequestFilter filter : mentorshipRequestFilters) {
            if (filter.isApplicable(filters)) {
                requests = filter.apply(requests.stream(), filters).toList();
            }
        }

        return mentorshipRequestMapper.toDto(requests);
    }

    public void validateExistsUsers(long requesterId, long receiverId) {
        if (!userRepository.existsById(requesterId) || !userRepository.existsById(receiverId)) {
            throw new DataValidationException("Нет пользователя с таким айди");
        }
    }
}
