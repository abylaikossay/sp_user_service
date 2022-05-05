package kz.smart.plaza.users.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MainPageResponse {

    private Long buttonId;
    private String buttonName;
    private String buttonImg;
    private Integer platform;
    private Boolean active;
}
