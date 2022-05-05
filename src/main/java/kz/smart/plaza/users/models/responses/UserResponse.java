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
public class UserResponse {
    private Long id;
    private String phone;
    private String name;
    private String surname;
    private String email;
    private String avatar;
    private Integer gender;
    private String platform;
    private Date lastLogin;
    private Date created_at;
    private Date updated_at;
    private Boolean banned;
    private Integer age;
    private String language;
    private Date birthDate;
    private Boolean pincode;
    private Double bonuses;
    private Double activeBonuses;
    private Double ecoBonuses;
    private Double activeEcoBonuses;
    private Double blockedBonuses;
    private List<TagResponse> tags;
    private Integer cityId;
    private Boolean techWork;
    private Boolean pushable;
    private String comment;
}
