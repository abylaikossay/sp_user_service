package kz.smart.plaza.users.models.responses.sms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserToSmsResponse {
    private Long userId;
    private String userPhone;
    private String smsConfirmCode;
}
