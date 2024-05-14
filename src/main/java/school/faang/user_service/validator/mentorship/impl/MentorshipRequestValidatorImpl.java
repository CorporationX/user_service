package school.faang.user_service.validator.mentorship.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidatorImpl implements MentorshipRequestValidator {
    private static final LocalDateTime TIME_PERIOD_FOR_SINGLE_REQUEST = LocalDateTime.now().minusMonths(3L);
    private final MentorshipRequestRepository mentorshipRequestRepository;

    @Override
    public void validateMentorshipRequest(MentorshipRequestDto dto) {
        validateRequesterAndReceiverDifferentiate(dto);
        validateFrequencyOfMentorshipRequest(dto);
    }

    @Override
    @Transactional(readOnly = true)
    public MentorshipRequest validateMentorshipRequestExistence(long id) {
        var optional = mentorshipRequestRepository.findById(id);
        return optional.orElseThrow(() -> {
            var message = String.format("a mentorship request with %d does not exist", id);

            return new DataValidationException(message);
        });
    }

    @Override
    public void validateMentor(MentorshipRequest entity) {
        var receiver = entity.getReceiver();
        var requester = entity.getRequester();
        var mentorsList = requester.getMentors();
        var isAlreadyMentor = mentorsList.stream().anyMatch(user -> user.equals(receiver));

        if (isAlreadyMentor) {
            var message = String.format("a user with id %d already a mentor to user with id %d",
                    receiver.getId(), requester.getId());

            throw new DataValidationException(message);
        }
    }

    private void validateRequesterAndReceiverDifferentiate(MentorshipRequestDto dto) {
        var requesterId = dto.getRequesterId();
        var receiverId = dto.getReceiverId();
        if (requesterId.equals(receiverId)) {
            var message = "a request id and a receiver id can not be same";

            throw new DataValidationException(message);
        }
    }

    @Transactional(readOnly = true)
    private void validateFrequencyOfMentorshipRequest(MentorshipRequestDto dto) {
        var optional = mentorshipRequestRepository.findLatestRequest(dto.getRequesterId(), dto.getReceiverId());

        optional.ifPresent(this::validateItIsFirstMentorshipRequestInLastThreeMonth);
    }

    private void validateItIsFirstMentorshipRequestInLastThreeMonth(MentorshipRequest mentorshipRequest) {
        var mentorshipRequestCreatedDate = mentorshipRequest.getCreatedAt();
        var isAfter = mentorshipRequestCreatedDate.isAfter(TIME_PERIOD_FOR_SINGLE_REQUEST);
        if (isAfter) {
            var receiverId = mentorshipRequest.getReceiver().getId();
            var message = String.format("a mentorship request to user with id %d" +
                    " already has been made in last three months", receiverId);

            throw new DataValidationException(message);
        }
    }

}
