package kz.smart.plaza.users.clients;

import kz.smart.plaza.users.models.responses.authService.UserInfoResponse;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient("auth-service")
@RibbonClient("auth-service")
public interface AuthServiceClient {

    @PostMapping("/token/check")
    ResponseEntity<UserInfoResponse> getClientInfoByToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);

}