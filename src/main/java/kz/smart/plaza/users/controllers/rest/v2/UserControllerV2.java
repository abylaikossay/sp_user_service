package kz.smart.plaza.users.controllers.rest.v2;

import kz.smart.plaza.users.controllers.BaseController;
import kz.smart.plaza.users.models.entities.CovidUser;
import kz.smart.plaza.users.services.v1.UserServiceV1;
import kz.smart.plaza.users.services.v2.UserServiceV2;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2")
@AllArgsConstructor
public class UserControllerV2 extends BaseController {
    private UserServiceV1 userServiceV1;
    private UserServiceV2 userServiceV2;

    @GetMapping("/cinemax/identify")
    public ResponseEntity<?> getUserByQrOrPhone(@RequestParam String qr_code) {
        return buildResponse(userServiceV1.findByQrCinemx(qr_code), HttpStatus.OK);
    }


    @GetMapping("/external/init")
    public ResponseEntity<?> initUserCinemax(@RequestParam Map<String, String> formData,
                                             @RequestHeader(value = "userId") Long userId){
        return buildResponse(userServiceV1.initUserCinemax(formData, userId), HttpStatus.OK);
    }

    @GetMapping("/external/identify")
    public ResponseEntity<?> identifyUserCinemax(@RequestParam Map<String, String> formData){
        return buildResponse(userServiceV1.identifyUserCinemax(formData), HttpStatus.OK);
    }


    @PostMapping("/covid/save")
    public ResponseEntity<?> addCovidUser(@RequestBody CovidUser covidUser){
        return buildResponse(userServiceV2.addCovidUser(covidUser), HttpStatus.OK);
    }

    @GetMapping("/covid/all")
    public ResponseEntity<?> getAllCovidUsers() {
        return buildResponse(userServiceV2.getCovidUsers(), HttpStatus.OK);
    }


}
