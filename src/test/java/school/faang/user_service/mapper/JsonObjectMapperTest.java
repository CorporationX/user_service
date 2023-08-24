package school.faang.user_service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JsonObjectMapperTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private JsonObjectMapper jsonMapper;

    @Test
    void testToJson_SuccessfulSerialization() throws JsonProcessingException {
        Object objectToSerialize = new Object();

        String expectedJson = "EXPECTED_JSON";
        when(objectMapper.writeValueAsString(objectToSerialize)).thenReturn(expectedJson);

        String resultJson = jsonMapper.toJson(objectToSerialize);

        verify(objectMapper).writeValueAsString(objectToSerialize);
        assertEquals(expectedJson, resultJson);
    }

    @Test
    void testToJson_FailedSerialization() throws JsonProcessingException {
        Object objectToSerialize = new Object();

        when(objectMapper.writeValueAsString(objectToSerialize)).thenThrow(JsonProcessingException.class);

        String resultJson = jsonMapper.toJson(objectToSerialize);

        verify(objectMapper).writeValueAsString(objectToSerialize);
        assertNull(resultJson);
    }
}