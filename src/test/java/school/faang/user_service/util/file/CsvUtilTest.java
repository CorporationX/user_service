package school.faang.user_service.util.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.pojo.student.Person;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class CsvUtilTest {

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String GROUP = "A";
    private static final String STUDENT_ID = "123456";
    private static final String EMAIL = "johndoe@example.com";
    private static final String PHONE = "+1-123-456-7890";
    private static final String STREET = "123 Main Street";
    private static final String CITY = "New York";
    private static final String STATE = "NY";
    private static final String COUNTRY = "USA";
    private static final String POSTAL_CODE = "10001";
    private static final String FACULTY = "Computer Science";
    private static final String MAJOR = "Software Engineering";
    private static final String STATUS = "Active";
    private static final String ADMISSION_DATE = "2016-09-01";
    private static final String GRADUATION_DATE = "2020-05-30";
    private static final String DEGREE = "High School Diploma";
    private static final String INSTITUTION = "XYZ High School";
    private static final String EMPLOYER = "XYZ Technologies";
    private static final String NAME_MOCK = "TEST";

    private static final int YEAR_OF_BIRTH = 1998;
    private static final int YEAR_OF_STUDY = 3;
    private static final int COMPLETION_YEAR = 2016;
    private static final int LIST_SIZE_ZERO = 0;

    private static final double GPA = 3.8;

    @InjectMocks
    private CsvUtil csvUtil;

    private static MultipartFile file = null;

    @Nested
    class ParseCsvMultipartFile {

        @Test
        @DisplayName("Assert that person is equals expected after parseCsvMultipartFile parsing")
        void whenCorrectCsvDataThenPersonEqualsToExpected() {
            file = new MockMultipartFile(NAME_MOCK, generateHeader().concat(generateData()).getBytes());

            Person person = csvUtil.parseCsvMultipartFile(file, Person.class).get(0);

            assertEquals(FIRST_NAME, person.getFirstName());
            assertEquals(LAST_NAME, person.getLastName());
            assertEquals(YEAR_OF_BIRTH, person.getYearOfBirth());
            assertEquals(GROUP, person.getGroup());
            assertEquals(STUDENT_ID, person.getStudentID());
            assertEquals(EMAIL, person.getContactInfo().getEmail());
            assertEquals(PHONE, person.getContactInfo().getPhone());
            assertEquals(STREET, person.getContactInfo().getAddress().getStreet());
            assertEquals(CITY, person.getContactInfo().getAddress().getCity());
            assertEquals(STATE, person.getContactInfo().getAddress().getState());
            assertEquals(COUNTRY, person.getContactInfo().getAddress().getCountry());
            assertEquals(POSTAL_CODE, person.getContactInfo().getAddress().getPostalCode());
            assertEquals(FACULTY, person.getEducation().getFaculty());
            assertEquals(MAJOR, person.getEducation().getMajor());
            assertEquals(YEAR_OF_STUDY, person.getEducation().getYearOfStudy());
            assertEquals(GPA, person.getEducation().getGpa());
            assertEquals(STATUS, person.getStatus());
            assertEquals(ADMISSION_DATE, person.getAdmissionDate());
            assertEquals(GRADUATION_DATE, person.getGraduationDate());
            assertEquals(DEGREE, person.getPreviousEducation().getDegree());
            assertEquals(INSTITUTION, person.getPreviousEducation().getInstitution());
            assertEquals(COMPLETION_YEAR, person.getPreviousEducation().getCompletionYear());
            assertEquals(DEGREE, person.getPreviousEducation().getDegree());
            assertTrue(person.getScholarship());
            assertEquals(EMPLOYER, person.getEmployer());
        }

        @Test
        @DisplayName("Assert that persons length is 0 when data in multipart file is null after parseCsvToPojo parsing")
        void whenNullDataThenListSizeIsZero() {
            file = new MockMultipartFile(NAME_MOCK, generateHeader().getBytes());

            List<Person> person = csvUtil.parseCsvMultipartFile(file, Person.class);
            assertEquals(LIST_SIZE_ZERO, person.size());
        }
    }

    @Nested
    class ParseCsvToPojo {

        @Test
        @DisplayName("Assert that person is equals expected after parseCsvToPojo parsing")
        void whenCorrectCsvDataThenPersonEqualsToExpected() {
            file = new MockMultipartFile(NAME_MOCK, generateHeader().concat(generateData()).getBytes());

            Person person = csvUtil.parseCsvToPojo(file, Person.class).get(0);

            assertEquals(FIRST_NAME, person.getFirstName());
            assertEquals(LAST_NAME, person.getLastName());
            assertEquals(YEAR_OF_BIRTH, person.getYearOfBirth());
            assertEquals(GROUP, person.getGroup());
            assertEquals(STUDENT_ID, person.getStudentID());
            assertEquals(EMAIL, person.getContactInfo().getEmail());
            assertEquals(PHONE, person.getContactInfo().getPhone());
            assertEquals(STREET, person.getContactInfo().getAddress().getStreet());
            assertEquals(CITY, person.getContactInfo().getAddress().getCity());
            assertEquals(STATE, person.getContactInfo().getAddress().getState());
            assertEquals(COUNTRY, person.getContactInfo().getAddress().getCountry());
            assertEquals(POSTAL_CODE, person.getContactInfo().getAddress().getPostalCode());
            assertEquals(FACULTY, person.getEducation().getFaculty());
            assertEquals(MAJOR, person.getEducation().getMajor());
            assertEquals(YEAR_OF_STUDY, person.getEducation().getYearOfStudy());
            assertEquals(GPA, person.getEducation().getGpa());
            assertEquals(STATUS, person.getStatus());
            assertEquals(ADMISSION_DATE, person.getAdmissionDate());
            assertEquals(GRADUATION_DATE, person.getGraduationDate());
            assertEquals(DEGREE, person.getPreviousEducation().getDegree());
            assertEquals(INSTITUTION, person.getPreviousEducation().getInstitution());
            assertEquals(COMPLETION_YEAR, person.getPreviousEducation().getCompletionYear());
            assertEquals(DEGREE, person.getPreviousEducation().getDegree());
            assertTrue(person.getScholarship());
            assertEquals(EMPLOYER, person.getEmployer());
        }

        @Test
        @DisplayName("Assert that persons length is 0 when data in multipart file is null after parseCsvToPojo parsing")
        void whenNullDataThenListSizeIsZero() {
            file = new MockMultipartFile(NAME_MOCK, generateHeader().getBytes());

            List<Person> person = csvUtil.parseCsvToPojo(file, Person.class);
            assertEquals(LIST_SIZE_ZERO, person.size());
        }
    }

    private static String generateHeader() {
        StringBuilder headerBuilder = new StringBuilder();

        headerBuilder.append("firstName").append(",")
                .append("lastName").append(",")
                .append("yearOfBirth").append(",")
                .append("group").append(",")
                .append("studentID").append(",")
                .append("email").append(",")
                .append("phone").append(",")
                .append("street").append(",")
                .append("city").append(",")
                .append("state").append(",")
                .append("country").append(",")
                .append("postalCode").append(",")
                .append("faculty").append(",")
                .append("yearOfStudy").append(",")
                .append("major").append(",")
                .append("GPA").append(",")
                .append("status").append(",")
                .append("admissionDate").append(",")
                .append("graduationDate").append(",")
                .append("degree").append(",")
                .append("institution").append(",")
                .append("completionYear").append(",")
                .append("scholarship").append(",")
                .append("employer").append("\n");

        return headerBuilder.toString();
    }

    public static String generateData() {
        StringBuilder dataBuilder = new StringBuilder();

        dataBuilder.append(FIRST_NAME).append(",")
                .append(LAST_NAME).append(",")
                .append(YEAR_OF_BIRTH).append(",")
                .append(GROUP).append(",")
                .append(STUDENT_ID).append(",")
                .append(EMAIL).append(",")
                .append(PHONE).append(",")
                .append(STREET).append(",")
                .append(CITY).append(",")
                .append(STATE).append(",")
                .append(COUNTRY).append(",")
                .append(POSTAL_CODE).append(",")
                .append(FACULTY).append(",")
                .append(YEAR_OF_STUDY).append(",")
                .append(MAJOR).append(",")
                .append(GPA).append(",")
                .append(STATUS).append(",")
                .append(ADMISSION_DATE).append(",")
                .append(GRADUATION_DATE).append(",")
                .append(DEGREE).append(",")
                .append(INSTITUTION).append(",")
                .append(COMPLETION_YEAR).append(",")
                .append(Boolean.TRUE).append(",")
                .append(EMPLOYER);

        return dataBuilder.toString();
    }
}
