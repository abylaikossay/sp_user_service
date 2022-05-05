package kz.smart.plaza.users.configurations;

import kz.smart.plaza.users.services.v1.CertificateServiceV1;
import kz.smart.plaza.users.services.v1.UserAttemptServiceV1;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class CronConfig {

    @Autowired
    private UserAttemptServiceV1 userAttemptServiceV1;

    @Autowired
    private CertificateServiceV1 certificateServiceV1;

    @Autowired
    @Scheduled(cron = " 0 */1 * * * *")
    public void scheduleTaskUsingCronExpressionForNewUpdating() throws IOException, NoSuchAlgorithmException
            {
        userAttemptServiceV1.checkAllUsersTime();
    }

    @Scheduled(cron = "0 0/30 * * * ?")
    public void scheduleTaskUsingCronExpressionForCheckingCertificates() {
        System.out.println("CRON CHECKING EXPIRATION");
        certificateServiceV1.checkUserCertificatesAndUpdateThemByExpiration();
    }
}
