package school.faang.user_service.controller.mentorship;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import school.faang.user_service.controller.BaseControllerTest;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MentorshipRequestController.class)
class MentorshipRequestControllerTest extends BaseControllerTest {

    @MockBean
    private MentorshipRequestService mentorshipRequestService;

    @Test
    void requestMentorship_should_return_created_status_with_valid_dto() throws Exception {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setDescription("Test Description");
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);

        MentorshipRequestDto savedDto = new MentorshipRequestDto();
        savedDto.setId(1L);
        savedDto.setDescription("Test Description");
        savedDto.setRequesterId(1L);
        savedDto.setReceiverId(2L);
        savedDto.setRequestStatus(RequestStatus.PENDING);

        when(mentorshipRequestService.requestMentorship(any(MentorshipRequestDto.class))).thenReturn(savedDto);

        mockMvc.perform(post(ApiPath.REQUEST_MENTORSHIP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header(BaseControllerTest.USER_HEADER, BaseControllerTest.DEFAULT_HEADER_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(savedDto.getId()))
                .andExpect(jsonPath("$.description").value(savedDto.getDescription()))
                .andExpect(jsonPath("$.requestStatus").value("PENDING"));
    }

    @Test
    void requestMentorship_should_return_bad_request_with_missing_desc() throws Exception {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);

        mockMvc.perform(post(ApiPath.REQUEST_MENTORSHIP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header(BaseControllerTest.USER_HEADER, BaseControllerTest.DEFAULT_HEADER_VALUE))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
