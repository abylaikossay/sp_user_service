package kz.smart.plaza.users.models.responses.laravel;

import kz.smart.plaza.users.models.responses.TagResponse;
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
public class UserLaraResponse {
    private Long id;
    private String phone;
    private String first_name;
    private String last_name;
    private Integer city_id;
    private String city_title;
    private Double points;
    private Double points_returnable;
    private String avatar_url;
    private String session;
}