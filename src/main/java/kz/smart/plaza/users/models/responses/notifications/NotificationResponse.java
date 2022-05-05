package kz.smart.plaza.users.models.responses.notifications;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NotificationResponse {
    private Long brand_id;
    private String title;
    private String type;
    private String brand_name;
    private String img_url;
    private String message;
    private List<NotificationsUserResponse> users;
    private NotificationInfoResponse info;
}
