package school.faang.user_service.service;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Mentee {
    @Id
    private long id;
    private String name;

    // Геттеры и сеттеры
}
