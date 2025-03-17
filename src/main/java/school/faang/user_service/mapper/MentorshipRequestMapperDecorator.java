package school.faang.user_service.mapper;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import school.faang.user_service.entity.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

@Slf4j
public class MentorshipRequestMapperDecorator implements MentorshipRequestMapper {

    @Autowired
    @Lazy
    private MentorshipRequestMapper delegate;

    @Autowired
    private UserRepository userRepository;

    @Override
    public MentorshipRequest toEntity(MentorshipRequestDto dto) {
        MentorshipRequest request = delegate.toEntity(dto);
        if (request.getRequester().getId().equals(request.getReceiver().getId())) {
            throw new IllegalArgumentException("Requester and receiver cannot be the same user");
        }
        log.info("Mentorship request created: {}", request);
        return request;
    }

    @Override
    public MentorshipRequest updateRequestFromDto(RejectionDto rejectionDto, MentorshipRequest mentorshipRequest) {
        MentorshipRequest updatedRequest = delegate.updateRequestFromDto(rejectionDto, mentorshipRequest);
        log.info("Mentorship request updated to REJECTED: {}", updatedRequest);
        return updatedRequest;
    }

    @Override
    public MentorshipRequestDto toDto(MentorshipRequest request) {
        return delegate.toDto(request);
    }

    @Named("mapUserIdToUser")
    public User mapUserIdToUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
    }
}