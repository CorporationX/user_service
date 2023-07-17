package school.faang.user_service.mentorship;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.mentorship.dto.MentorshipRequestDto;
import school.faang.user_service.mentorship.dto.RejectionDto;
import school.faang.user_service.mentorship.dto.RequestFilterDto;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    @Autowired
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Transactional
    public void requestMentorship(MentorshipRequestDto dto) throws Exception {
        if (dto.getRequester() == dto.getReceiver()) {
            throw new Exception("request was not create, your mentor is you");
        }
        if (dto.getRequester().getSentMentorshipRequests() != null) {
            int sizeRequest = dto.getRequester().getSentMentorshipRequests().size();
            if (sizeRequest >= 3) {
                if (!dto.getRequester().getSentMentorshipRequests().
                        get(sizeRequest - 3).getCreatedAt().
                        isBefore(LocalDateTime.now().minusMonths(1))) {
                    throw new Exception("request was not create, so many requests in this month");
                }
            }
        }
        mentorshipRequestRepository.create(dto.getRequester().getId(), dto.getReceiver().getId(), dto.getDescription());
    }

    @Transactional
    public Optional<MentorshipRequest> getRequests(RequestFilterDto filter) throws Exception {
        Optional<MentorshipRequest> mentorshipRequests;
        mentorshipRequests = Optional.ofNullable(mentorshipRequestRepository.getRequests(filter.getReceiverId()).orElseThrow(() -> new Exception("This receiver is not exist")));
        if (mentorshipRequests == null || mentorshipRequests.isEmpty()) {
            throw new RuntimeException("You don't have request");
        } else {
            //я получаю общий список всех заявок на менторство, чтобы отфильтровать можно для каждой кнопки сделать свой метод
            // и в каждом методе реализовать свою фильтрацию
            return mentorshipRequests;
        }
    }

    @Transactional
    public void acceptRequest(long id) throws Exception {
        MentorshipRequest request = mentorshipRequestRepository.findById(id)
                .orElseThrow(Exception::new);

        if (request.getStatus().equals(RequestStatus.ACCEPTED)) {
            throw new Exception();
        }

        request.setStatus(RequestStatus.ACCEPTED);

        List<User> newMentees = request.getReceiver().getMentees();
        newMentees.add(request.getRequester());
        request.getRequester().setMentees(newMentees);

        List<User> newMentors = request.getRequester().getMentors();
        newMentors.add(request.getReceiver());
        request.getReceiver().setMentees(newMentors);
    }

    @Transactional
    public void rejectRequest(long id, RejectionDto rejection) throws Exception {
        MentorshipRequest request = mentorshipRequestRepository.findById(id)
                .orElseThrow(Exception::new);

        if (request.getStatus().equals(RequestStatus.REJECTED)) {
            throw new Exception();
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());
    }
}
