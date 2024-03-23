package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.controller.event.EventParticipationController;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventParticipationService;
import school.faang.user_service.validator.event.EventParticipationValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class EventParticipationControllerTest {
    private MockMvc mockMvc;
    @Mock
    private EventParticipationService eventParticipationService;
    @InjectMocks
    EventParticipationController controller;
    @Mock
    private UserContext userContext;
    @Mock
    private EventParticipationValidator eventValidator;

    private long userId;
    private long eventId;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup( controller ).build();
        eventId = 1L;
        userId = 2L;
    }

    @Test
    public void testRegisterParticipant() throws Exception {
        when( userContext.getUserId() ).thenReturn( userId );

        when( eventParticipationService.registerParticipant( eventId, userId ) )
                .thenReturn( ResponseEntity.ok( "Participant registered successfully" ) );

        mockMvc.perform( post( "/events/{eventId}", eventId ) )
                .andExpect( status().isOk() )
                .andExpect( content().string( "Participant registered successfully" ) );

        verify( eventValidator ).checkEventIdForNull( eventId);
        verify( eventParticipationService, times( 1 ) ).registerParticipant( eventId, userId );
    }

    @Test
    public void registerParticipant_NullEventId_ValidationException() throws Exception {
        long badEventId = 0L;
        when(userContext.getUserId()).thenReturn(2L);

        doThrow(new DataValidationException("Event id is null"))
                .when(eventValidator).checkEventIdForNull(anyLong());

        mockMvc.perform(post( "/events/{eventId}", badEventId ))
                .andExpect(status().isNotFound());

        verify(eventParticipationService, never()).registerParticipant(anyLong(), anyLong());
    }

    @Test
    public void testUnregisterParticipantSuccess() throws Exception {
        when( userContext.getUserId() ).thenReturn( userId );
        when( eventParticipationService.unregisterParticipant( eventId, userId ) )
                .thenReturn( ResponseEntity.noContent().build() );
        mockMvc.perform( delete( "/events/{eventId}", eventId ) )
                .andExpect( status().isNoContent() );
        verify( eventValidator ).checkEventIdForNull( eventId );
        verify( eventParticipationService ).unregisterParticipant( eventId, userId );
    }

    @Test
    public void unregisterParticipant_NullEventId_ValidationException() throws Exception {

        when(userContext.getUserId()).thenReturn(2L);
        doThrow(new DataValidationException("Event id is null"))
                .when(eventValidator).checkEventIdForNull(anyLong());

        mockMvc.perform(delete("/events/{eventId}", 0L ))
                .andExpect(status().isNotFound());

        verify(eventValidator).checkEventIdForNull(anyLong());
        verify(eventParticipationService, never()).unregisterParticipant(anyLong(), anyLong());
    }


    @Test
    public void testGetParticipants() throws Exception {
        List<User> users = List.of( User.builder().id( 1L ).build(), User.builder().id( 2L ).build() );
        when( eventParticipationService.getParticipants( eventId ) ).thenReturn( ResponseEntity.status( HttpStatus.OK ).body( users ) );
        mockMvc.perform( get( "/events/{eventId}", eventId ) ).andExpect( status().isOk() );
        verify( eventValidator ).checkEventIdForNull( eventId );
        verify( eventParticipationService ).getParticipants( eventId );
    }


    @Test
    public void testGetParticipantsCount() throws Exception {
        Long eventId = 1L;
        int expectedCount = 5;

        when(eventParticipationService.getParticipantsCount(eventId))
                .thenReturn(expectedCount);
        MvcResult mvcResult = mockMvc.perform(get("/events")
                        .param("eventId", String.valueOf(eventId)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        int actualCount = Integer.parseInt(responseBody);
        assertEquals(expectedCount, actualCount);
        verify(eventValidator).checkEventIdForNull(eventId);
        verify(eventParticipationService).getParticipantsCount(eventId);
    }
}






