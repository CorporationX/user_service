package school.faang.user_service.converter.starter;

import com.json.student.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.converter.logic.MapToPerson;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConverterCsvToPersonTest {

    @InjectMocks
    private ConverterCsvToPerson converterCsvToPerson;

    @Mock
    private MapToPerson mapToPerson;

    @Test
    public void testConvertCsvToPerson() {
        MultipartFile file = new MockMultipartFile("file", "Hello, World!".getBytes());

        Person person1 = new Person();
        Person person2 = new Person();
        List<Person> persons = Arrays.asList(person1, person2);

        when(mapToPerson.mapToPersons(anyList())).thenReturn(persons);

        List<Person> result = converterCsvToPerson.convertCsvToPerson(file);

        assertEquals(persons, result);
        verify(mapToPerson, times(1)).mapToPersons(anyList());
    }

    @Test
    public void testConvertCsvToPersonWithIOException() {
        MultipartFile file = new MockMultipartFile("file", new byte[0]);

        assertThrows(RuntimeException.class, () -> converterCsvToPerson.convertCsvToPerson(file));

        verify(mapToPerson, never()).mapToPersons(anyList());
    }
}
