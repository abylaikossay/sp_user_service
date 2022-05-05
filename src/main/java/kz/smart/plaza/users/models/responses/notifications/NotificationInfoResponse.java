package kz.smart.plaza.users.models.responses.notifications;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationInfoResponse {
    private Long brand_id;
    private Long external_id;
    private String img_url;
    private String brand_name;
}