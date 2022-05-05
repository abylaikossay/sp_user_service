package kz.smart.plaza.users.methods;

import kz.smart.plaza.users.models.responses.notifications.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class Notificate {

    private KafkaProducerMethods kafkaProducerMethods;

    public void notificate(List<Long> userIds) {
        List<NotificationsUserResponse> notificationsUserResponses = new ArrayList<>();
        userIds.forEach( userId -> {
            NotificationsUserResponse notificationsUserResponse = NotificationsUserResponse.builder()
                    .user_id(userId.toString()).build();
            notificationsUserResponses.add(notificationsUserResponse);
        });
        NotificationInfoResponse notificationInfoResponse = NotificationInfoResponse.builder().external_id(1L).build();
        NotificationResponse notificationResponse = NotificationResponse.builder()
                .brand_id(391L)
                .brand_name("Home Credit Bank")
                .img_url("https://api.smartplaza.kz/partners/api/file/logo/logo_1607686892542.jpeg")
                .info(notificationInfoResponse)
                .message("Дорогой друг, для тебя открыты двери нового семейного отделения Банка Хоум Кредит.\n" +
                        "Оцени высокий сервис обслуживания и по-настоящему семейную атмосферу отделения банка.\n" +
                        "Приходи и оформляй любой продукт Банка Хоум Кредит и получай гарантированные подарки.\n" +
                        "Подробности на сайте www.homecredit.kz в Мобильном приложении и в семейном отделении банка на 1-м этаже ТРЦ «Dostyk Plaza».\n" +
                        "Банк Хоум Кредит – Семьи семьям!")
                .title("Home Credit Bank")
                .type("news")
                .users(notificationsUserResponses)
                .build();
        kafkaProducerMethods.sendNotifications(notificationResponse);
    }
    public void notificateCertificate(NotificateCertificateResponse notificateCertificateResponse) {
        List<NotificationsUserResponse> notificationsUserResponses = new ArrayList<>();
        notificationsUserResponses.add(NotificationsUserResponse.builder()
                .user_id(notificateCertificateResponse.getUserId().toString())
                .build());
        NotificationInfoResponse notificationInfoResponse = NotificationInfoResponse.builder()
                .external_id(notificateCertificateResponse.getExternalId())
                .brand_id(notificateCertificateResponse.getBrandId())
                .brand_name(notificateCertificateResponse.getBrandName())
                .img_url("https://api.smartplaza.kz/partners/api/file/logo/"+notificateCertificateResponse.getBrandUrl())
                .build();
        NotificationResponse notificationResponse = NotificationResponse.builder()
                .brand_id(notificateCertificateResponse.getBrandId())
                .brand_name(notificateCertificateResponse.getBrandName())
                .img_url("https://api.smartplaza.kz/partners/api/file/logo/"+notificateCertificateResponse.getBrandUrl())
                .info(notificationInfoResponse)
                .message("Вы успешно активизировали сертификат номиналом в " + notificateCertificateResponse.getSum() + " тг.")
                .title(notificateCertificateResponse.getBrandName())
                .type("text")
                .users(notificationsUserResponses)
                .build();
        kafkaProducerMethods.sendNotifications(notificationResponse);
    }

    public void notificateGiftCertificate(GiftCertificateResponse giftCertificateResponse, String type) {
        String message = "";
        if (type.equals("gifted")) {
            message = "Вы подарили сертификат бренда " + giftCertificateResponse.getBrandName() + " номиналом в " + giftCertificateResponse.getSum() + " тг пользователю "  + giftCertificateResponse.getUserName();
        } else if (type.equals("gained")) {
            message = "Вы получили подарок от пользователя " + giftCertificateResponse.getUserName()+ " сертификат бренда " +
                    giftCertificateResponse.getBrandName() + " номиналом в " + giftCertificateResponse.getSum() + " тг.";
        }
        String typeNotif = "text";
        if (type.equals("gained")) {
            typeNotif = "certificate";
        } else if (type.equals("gifted")) {
            typeNotif = "certificate";
        }
        List<NotificationsUserResponse> notificationsUserResponses = new ArrayList<>();
        notificationsUserResponses.add(NotificationsUserResponse.builder()
                .user_id(giftCertificateResponse.getUserId().toString())
                .build());
        NotificationInfoResponse notificationInfoResponse = NotificationInfoResponse.builder()
                .external_id(giftCertificateResponse.getExternalId())
                .brand_id(giftCertificateResponse.getBrandId())
                .brand_name(giftCertificateResponse.getBrandName())
                .img_url("https://api.smartplaza.kz/partners/api/file/logo/"+giftCertificateResponse.getBrandUrl())
                .build();
        NotificationResponse notificationResponse = NotificationResponse.builder()
                .brand_id(giftCertificateResponse.getBrandId())
                .brand_name(giftCertificateResponse.getBrandName())
                .img_url("https://api.smartplaza.kz/partners/api/file/logo/"+giftCertificateResponse.getBrandUrl())
                .info(notificationInfoResponse)
                .message(message)
                .title(giftCertificateResponse.getBrandName())
                .type(typeNotif)
                .users(notificationsUserResponses)
                .build();
        kafkaProducerMethods.sendNotifications(notificationResponse);
    }

}
