package kz.smart.plaza.users.clients;

import kz.smart.plaza.users.models.requests.BonusRequest;
import kz.smart.plaza.users.models.requests.EcoBonusRequest;
import kz.smart.plaza.users.models.requests.UserBonusRequest;
import kz.smart.plaza.users.models.responses.UserBonusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("bonus-service")
public interface BonusService {

    @GetMapping("/bonus/{userId}")
    ResponseEntity<BonusRequest> getBonus(@PathVariable Long userId);


    @GetMapping("/api/v1/eco/bonus/{userId}")
    ResponseEntity<EcoBonusRequest> getEcoBonus(@PathVariable Long userId);

    @PostMapping("/api/v1/bonuses/mixed")
    ResponseEntity<List<UserBonusResponse>> getUserBonuses(@RequestBody UserBonusRequest userBonusRequest);


}
