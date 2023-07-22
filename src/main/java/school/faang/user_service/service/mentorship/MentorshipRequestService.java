package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.MentorshipRequestNotFoundException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipRequestMapper requestMapper;
    private MentorshipRequestFilter requestFilter;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto requestDto) {
        long requesterId = requestDto.getRequester().getId();
        long receiverId = requestDto.getReceiver().getId();

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
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(id).orElseThrow(() -> {
            throw new MentorshipRequestNotFoundException("Данного запроса на менторство не существует");
        });

        User receiver = mentorshipRequest.getReceiver();
        User requester = mentorshipRequest.getRequester();

        if (requester.getMentors() == null) {
            requester.setMentors(List.of(receiver));
            mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        } else if (!requester.getMentors().contains(receiver)) {
            requester.getMentors().add(receiver);
            mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        } else {
            throw new IllegalArgumentException("Данный пользователь уже является ментором отправителя");
        }
    }

    private void dataValidate(long requesterId, long receiverId, MentorshipRequestDto requestDto) {
        userValidate(requesterId, receiverId);

        if (mentorshipRequestRepository.findLatestRequest(requesterId, receiverId).isPresent()) {
            MentorshipRequest latestRequest = mentorshipRequestRepository.findLatestRequest(requesterId, receiverId).get();
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