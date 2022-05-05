package kz.smart.plaza.users.models.responses.sms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsResponse {
    private String message;
    private List<SmsUserInfoResponse> users;
}
