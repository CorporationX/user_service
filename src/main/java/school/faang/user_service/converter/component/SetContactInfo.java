package school.faang.user_service.converter.component;

import com.json.student.Address;
import com.json.student.ContactInfo;
import org.springframework.stereotype.Component;

@Component
public class SetContactInfo {
    public ContactInfo setContactInfo(String email, String phone, String street, String city, String state, String country, String postalCode) {
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmail(email);
        contactInfo.setPhone(phone);

        Address address = new Address();
        address.setStreet(street);
        address.setCity(city);
        address.setState(state);
        address.setCountry(country);
        address.setPostalCode(postalCode);
        contactInfo.setAddress(address);

        return contactInfo;
    }
}
