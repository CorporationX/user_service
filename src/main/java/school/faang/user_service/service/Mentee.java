package school.faang.user_service.service;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Mentee {
    @Id
    private long id;
    private String name;

    public Mentee() {
        this.id = 0;
        this.name = "";
    }
}
