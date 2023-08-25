package school.faang.user_service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JsonObjectMapperTest {
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private JsonObjectMapper jsonObjectMapper;
    Object object;
    String json;

    @BeforeEach
    public void setUp() {
        object = new Object();
        json = "EXPECTED_JSON";
    }

    @Test
    void testToJson_SuccessfulSerialization() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(object)).thenReturn(json);

        String resultJson = jsonObjectMapper.toJson(object);

        verify(objectMapper).writeValueAsString(object);
        assertEquals(json, resultJson);
    }

    @Test
    void testToJson_FailedSerialization() throws JsonProcessingException {

        when(objectMapper.writeValueAsString(object)).thenThrow(JsonProcessingException.class);

        String resultJson = jsonObjectMapper.toJson(object);

        verify(objectMapper).writeValueAsString(object);
        assertNull(resultJson);

    }
}