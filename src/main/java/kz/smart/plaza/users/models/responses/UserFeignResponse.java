package kz.smart.plaza.users.models.responses;

import kz.smart.plaza.users.models.entities.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFeignResponse {
    private Long id;
    private String phone;
    private String name;
    private String photoUrl;
    private List<TagResponse> tags;
    private String platform;
    private Integer gender;
    private String email;
    private Date birthDate;
    private Integer cityId;
    private Integer age;
    private String deviceToken;
}
