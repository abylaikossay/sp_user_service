package kz.smart.plaza.users.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {

    @NotNull
    @NotEmpty
    private String phone;

    @NotNull
    @NotEmpty
    private String password;
    private String platform;
    private String deviceToken;
    private String version;
}
