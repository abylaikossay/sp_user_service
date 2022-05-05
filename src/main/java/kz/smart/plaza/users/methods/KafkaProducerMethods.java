package kz.smart.plaza.users.methods;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.smart.plaza.users.models.requests.CertificateOrderRequest;
import kz.smart.plaza.users.models.responses.notifications.NotificationResponse;
import kz.smart.plaza.users.models.responses.sms.SmsResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;



@Service
@AllArgsConstructor
public class KafkaProducerMethods {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerMethods.class);
    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;

    public void sendSmsToConfirm(SmsResponse smsResponse) {
        logger.info(String.format("#### -> Sms send > %s", writeValueAsString(smsResponse), "sms.save"));
        this.kafkaTemplate.send("sms.save", "notification", writeValueAsString(smsResponse));
        System.out.println("KAFKA SMS SENDED!");
    }

    public void sendNotifications(NotificationResponse notification) {
        logger.info(String.format("#### -> Notification send -> %s", writeValueAsString(notification), "notification.save"));
        this.kafkaTemplate.send("notification.save", "notification", writeValueAsString(notification));
    }

    public void sendOrderCertificate(CertificateOrderRequest certificateOrderRequest) {
        logger.info(String.format("#### -> Order certificate send -> %s", writeValueAsString(certificateOrderRequest), "orders.certificate.save"));
        this.kafkaTemplate.send("orders.certificate.save", "orders", writeValueAsString(certificateOrderRequest));
    }


    private String writeValueAsString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Writing value to JSON failed: " + object.toString());
        }
    }
}
