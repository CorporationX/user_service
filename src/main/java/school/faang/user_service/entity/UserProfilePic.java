package school.faang.user_service.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class UserProfilePic {
    private String fileId;
    private String smallFileId;

    public UserProfilePic() {
    }

    // Конструктор, принимающий fileId
    public UserProfilePic(String fileId) {
        this.fileId = fileId;
    }

    // Геттеры и сеттеры
    public String getFileId() {
        return fileId;
    }

    public String getSmallFileId() {
        return smallFileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public void setSmallFileId(String smallFileId) {
        this.smallFileId = smallFileId;
    }
}