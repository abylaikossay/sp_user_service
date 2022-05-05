package kz.smart.plaza.users.controllers.rest.v1;

import kz.smart.plaza.users.controllers.BaseController;
import kz.smart.plaza.users.models.requests.UserPromoRequest;
import kz.smart.plaza.users.services.v1.UserPromoServiceV1;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/promo")
@AllArgsConstructor
public class UserPromoControllerV1 extends BaseController {
    private UserPromoServiceV1 userPromoServiceV1;

    @PostMapping("/add")
    public ResponseEntity<?> addPromoToUser(@RequestBody UserPromoRequest userPromoRequest) {
        userPromoServiceV1.addPromoToUser(userPromoRequest);
        return buildSuccessResponse("Промо акция успешно добавлена");
    }

    @GetMapping("/user")
    public ResponseEntity<?> getPromoByUserToken(@RequestHeader(value = "userId") Long userId) {
        return buildResponse(userPromoServiceV1.getPromoByUserId(userId), HttpStatus.OK);
    }
}
