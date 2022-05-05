package kz.smart.plaza.users.models.responses.authService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserInfoResponse {
    private List<String>authorities;
    private String details;
    private String principal;
    private String credentials;
    private Long userId;
    private String name;
    private String role;

}
