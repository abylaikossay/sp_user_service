package kz.smart.plaza.users.methods;

import kz.smart.plaza.users.models.entities.User;
import kz.smart.plaza.users.models.responses.sms.SmsResponse;
import kz.smart.plaza.users.models.responses.sms.SmsUserInfoResponse;
import kz.smart.plaza.users.models.responses.sms.UserToSmsResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@AllArgsConstructor
public class SmsSender {
    private KafkaProducerMethods kafkaProducerMethods;

    public void SendSmsToUser(UserToSmsResponse userToSmsResponse, String smsType) {
        SmsResponse smsResponse = new SmsResponse();
        switch (smsType) {
            case "register":
                smsResponse.setMessage("Ваш код регистрации: " + userToSmsResponse.getSmsConfirmCode() + " . Не сообщайте его никому");
                break;
            case "resendSms":
                smsResponse.setMessage("Ваш повторный код подтверждения: " + userToSmsResponse.getSmsConfirmCode() + " . Не сообщайте его никому");
                break;
            case "forgotPassword":
                smsResponse.setMessage("Ваш код для восстановления пароля: " + userToSmsResponse.getSmsConfirmCode() + " Не говорите никому пожалуйста!");
                break;
            case "register-cinemax":
                smsResponse.setMessage("Ваш билет в Cinemax доступен в Smart Plaza - http://onelink.to/smart-plaza. Ваш логин - номер телефона, Ваш временный пароль - " + userToSmsResponse.getSmsConfirmCode());
                break;
        }
        List<SmsUserInfoResponse> smsUserInfoResponses = new ArrayList<>();
        SmsUserInfoResponse smsUserInfoResponse = SmsUserInfoResponse.builder()
                .user_id(userToSmsResponse.getUserId().toString())
                .telnumber(userToSmsResponse.getUserPhone()).build();
        smsUserInfoResponses.add(smsUserInfoResponse);
        smsResponse.setUsers(smsUserInfoResponses);
        if (smsType.equals("register-cinemax")) {
            System.out.println("SMS USER RESPONSE" + smsResponse);
        }
        kafkaProducerMethods.sendSmsToConfirm(smsResponse);
    }

}
