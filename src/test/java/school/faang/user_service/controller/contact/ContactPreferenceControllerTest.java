package school.faang.user_service.controller.contact;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.dto.contact.ContactPreferenceDto;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.service.contact.ContactPreferenceService;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ContactPreferenceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ContactPreferenceService contactPreferenceService;

    @InjectMocks
    private ContactPreferenceController contactPreferenceController;

    private ContactPreferenceDto contactPreferenceDto;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        contactPreferenceDto = ContactPreferenceDto.builder()
                .userId(1L)
                .preference(PreferredContact.TELEGRAM)
                .build();

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(contactPreferenceController).build();
    }

    @Test
    void getContactPreference() throws Exception {
        String json = objectMapper.writeValueAsString(contactPreferenceDto);
        String username = "username";

        when(contactPreferenceService.getContact(username)).thenReturn(contactPreferenceDto);

        mockMvc.perform(get("/contacts/preferences/" + username))
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        InOrder inOrder = inOrder(contactPreferenceService);
        inOrder.verify(contactPreferenceService).getContact(username);
    }
}