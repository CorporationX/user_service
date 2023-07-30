package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription() == null || mentorshipRequestDto.getDescription().isBlank()) {
            throw new DataValidationException("Добавьте описание к запросу на менторство");
        }
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {
        if (filter == null) {
            throw new DataValidationException("Фильтр запросов не может быть пустым");
        }
        return mentorshipRequestService.getRequests(filter);
    }

    public void acceptRequest(long id) {
        if (id < 1) {
            throw new DataValidationException("Некорректный ввод id");
        }
        mentorshipRequestService.acceptRequest(id);
    }
}
