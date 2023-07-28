package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.MentorshipRequestNotFoundException;
import school.faang.user_service.exception.RequestAlreadyAcceptedException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.filter.MentorshipRequestFilter;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipRequestMapper requestMapper;
    private MentorshipRequestFilter requestFilter;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto requestDto) {
        Long requesterId = requestDto.getRequester().getId();
        Long receiverId = requestDto.getReceiver().getId();

        dataValidate(requesterId, receiverId, requestDto);

        MentorshipRequest newRequest = mentorshipRequestRepository.create(requesterId, receiverId, requestDto.getDescription());
        return requestMapper.toDto(newRequest);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {
        List<MentorshipRequestDto> allRequestDto = new ArrayList<>();

        mentorshipRequestRepository.findAll()
                .forEach(request -> allRequestDto.add(requestMapper.toDto(request)));

        requestFilter = MentorshipRequestFilter.builder()
                .requestDtoList(allRequestDto)
                .filter(filter)
                .build();
        return requestFilter.requestFiltering();
    }

    public void acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = findRequestInDB(id);

        User receiver = mentorshipRequest.getReceiver();
        User requester = mentorshipRequest.getRequester();

        checkAcceptedRequest(mentorshipRequest, RequestStatus.ACCEPTED);

        if (requester.getMentors() == null) {
            requester.setMentors(List.of(receiver));
            if (receiver.getMentees() == null) {
                receiver.setMentees(List.of(requester));
            } else {
                receiver.getMentees().add(requester);
            }
            mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        } else if (!requester.getMentors().contains(receiver)) {
            requester.getMentors().add(receiver);
            receiver.getMentees().add(requester);
            mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        } else {
            throw new IllegalArgumentException("Данный пользователь уже является ментором отправителя");
        }
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = findRequestInDB(id);

        User requester = mentorshipRequest.getRequester();
        User receiver = mentorshipRequest.getReceiver();

        checkAcceptedRequest(mentorshipRequest, RequestStatus.REJECTED);

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.getReason());
        requester.getMentors().remove(receiver);
        receiver.getMentees().remove(requester);
    }

    private void checkAcceptedRequest(MentorshipRequest request, RequestStatus status ) {
        if (request.getStatus() == status) {
            throw new RequestAlreadyAcceptedException("Данный запрос уже обработан");
        }
    }

    private MentorshipRequest findRequestInDB(long id) {
        return mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new MentorshipRequestNotFoundException("Данного запроса на менторство не существует"));
    }

    private void dataValidate(long requesterId, long receiverId, MentorshipRequestDto requestDto) {
        userValidate(requesterId, receiverId);

        Optional<MentorshipRequest> possibleRequest = mentorshipRequestRepository.findLatestRequest(requesterId, receiverId);

        if (possibleRequest.isPresent()) {
            MentorshipRequest latestRequest = possibleRequest.get();
            if (latestRequest.getUpdatedAt().plusMonths(3).isAfter(LocalDateTime.now())) {
                throw new DataValidationException("Нельзя отправить запрос на менторство данному пользователю, т.к. должно " +
                        "пройти не менее 3-ех месяцев с момента последнего запроса");
            }
        }
    }

    private void userValidate(long requesterId, long receiverId) {
        if (!mentorshipRepository.existsById(requesterId)) {
            throw new UserNotFoundException("Пользователя, отправляющего запрос не существует");
        }
        if (!mentorshipRepository.existsById(receiverId)) {
            throw new UserNotFoundException("Пользователя, у которого запрашивают менторство не существует");
        }
        if (requesterId == receiverId) {
            throw new DataValidationException("Пользователь не может отправлять себе запрос на менторство");
        }
    }
}