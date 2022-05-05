package kz.smart.plaza.users.models.requests;

import kz.smart.plaza.users.models.entities.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEditRequest {
    private Integer cityId;
    private String firstName;
    private String lastName;
    private Date birthdate;
    private String email;
    private Integer gender;
    private String language;
    private String comment;
    private List<Long> tags;
}
