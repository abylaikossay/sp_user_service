package kz.smart.plaza.users.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    @NotNull
    private String phone;
    private String password;
    private Integer cityId;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    private Date birthdate;
    @NotNull
    private String email;
    private Integer gender;
    private String language;
    private String device_token;
    private String platform;
}