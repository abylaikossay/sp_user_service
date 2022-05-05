package kz.smart.plaza.users.models.responses.sms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsUserInfoResponse {
    private String user_id;
    private String telnumber;
}
