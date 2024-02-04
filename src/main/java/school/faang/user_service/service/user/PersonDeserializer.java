package school.faang.user_service.service.user;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.json.student.Address;
import com.json.student.ContactInfo;
import com.json.student.Education;
import com.json.student.Person;
import com.json.student.PreviousEducation;

import java.io.IOException;
import java.util.List;

public class PersonDeserializer extends StdDeserializer<Person> {

    public PersonDeserializer() {
        this(null);
    }

    public PersonDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Person deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        Address address = new Address();
        ContactInfo contactInfo = new ContactInfo();
        Education education = new Education();
        Person person = new Person();
        PreviousEducation previousEducation = new PreviousEducation();

        person.setFirstName(node.get("firstName").textValue());
        person.setLastName(node.get("lastName").textValue());
        person.setYearOfBirth(node.get("yearOfBirth").intValue());
        person.setGroup(node.get("group").textValue());
        person.setStudentID(node.get("studentID").textValue());

        contactInfo.setEmail(node.get("email").textValue());
        contactInfo.setPhone(node.get("phone").textValue());
        address.setStreet(node.get("street").textValue());
        address.setCity(node.get("city").textValue());
        address.setState(node.get("state").textValue());
        address.setCountry(node.get("country").textValue());
        address.setPostalCode(node.get("postalCode").textValue());
        contactInfo.setAddress(address);

        person.setContactInfo(contactInfo);

        education.setFaculty(node.get("faculty").textValue());
        education.setYearOfStudy(node.get("yearOfStudy").intValue());
        education.setMajor(node.get("major").textValue());
        education.setGpa(node.get("GPA").doubleValue());

        person.setEducation(education);
        person.setStatus(node.get("status").textValue());
        person.setAdmissionDate(node.get("admissionDate").textValue());
        person.setGraduationDate(node.get("graduationDate").textValue());

        previousEducation.setDegree(node.get("degree").textValue());
        previousEducation.setInstitution(node.get("institution").textValue());
        previousEducation.setCompletionYear(node.get("completionYear").intValue());

        person.setPreviousEducation(List.of(previousEducation));
        person.setScholarship(node.get("scholarship").booleanValue());
        person.setEmployer(node.get("employer").textValue());

        return person;
    }
}