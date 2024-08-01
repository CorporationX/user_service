package school.faang.user_service.entity.person;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

//@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonPropertyOrder({
//        "street",
//        "city",
//        "state",
//        "country",
//        "postalCode"
//})
@Data
//@Generated("jsonschema2pojo")
public class Address {

    //@JsonProperty("street")
    private String street;

    //@JsonProperty("city")
    private String city;

    //@JsonProperty("state")
    private String state;

    //@JsonProperty("country")
    private String country;

    //@JsonProperty("postalCode")
    private String postalCode;

//    public String getStreet() {
//        return street;
//    }
//
//    public void setStreet(String street) {
//        this.street = street;
//    }
//
//    public String getCity() {
//        return city;
//    }
//
//    public void setCity(String city) {
//        this.city = city;
//    }
//
//    public String getState() {
//        return state;
//    }
//
//    public void setState(String state) {
//        this.state = state;
//    }
//
//    public String getCountry() {
//        return country;
//    }
//
//    public void setCountry(String country) {
//        this.country = country;
//    }
//
//    public String getPostalCode() {
//        return postalCode;
//    }

//    public void setPostalCode(String postalCode) {
//        this.postalCode = postalCode;
//    }



//    @JsonIgnore
//    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
//
//    @JsonAnyGetter
//    public Map<String, Object> getAdditionalProperties() {
//        return this.additionalProperties;
//    }
//
//    @JsonAnySetter
//    public void setAdditionalProperty(String name, Object value) {
//        this.additionalProperties.put(name, value);
//    }
}

