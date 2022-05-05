package kz.smart.plaza.users.controllers.rest.v1;

import kz.smart.plaza.users.controllers.BaseController;
import kz.smart.plaza.users.models.responses.success.SuccessResponse;
import kz.smart.plaza.users.services.v1.UserClickServiceV1;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/click")
@AllArgsConstructor
public class UserClickControllerV1 extends BaseController {
    private UserClickServiceV1 userClickServiceV1;

    @PutMapping("/{type}")
    public ResponseEntity<?> updateClickCount(@RequestHeader(value = "userId") Long userId, @PathVariable String type) {
        userClickServiceV1.updateUserClick(userId, type);
        return buildSuccess("Success", "Success");
    }

    @GetMapping()
    public ResponseEntity<?> getClickCounts(@RequestHeader(value = "userId") Long userId) {
        return buildResponse(userClickServiceV1.getUserClicks(userId), HttpStatus.OK);
    }

    @PostMapping(value = "/main-page", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> addButtonInfo(@ModelAttribute MultipartFile buttonImg,
                                           @RequestParam Long buttonId,
                                           @RequestParam String buttonName,
                                           @RequestParam Integer platform,
                                           @RequestParam Boolean active,
                                           @RequestParam String lang,
                                           @RequestHeader(value = "userId") Long userId) {
        userClickServiceV1.addButtonInfo(buttonImg, buttonId, buttonName, platform, active, lang, userId);
        return buildSuccessResponse(SuccessResponse.builder().message("Добавлена информация о кнопке").build());
    }

//    @GetMapping("/main-page")
    public ResponseEntity<?> getMainPage(@RequestParam Integer platform) {
        return buildResponse(userClickServiceV1.getMainPage(platform), HttpStatus.OK);
    }

    @GetMapping("/button-info")
    public ResponseEntity<?> getButtonInfo(@RequestParam Integer platform,
                                           @RequestParam Long buttonId,
                                           @RequestHeader(value = "userId") Long userId) {
        return buildResponse(userClickServiceV1.getButtonInfo(platform, buttonId, userId), HttpStatus.OK);
    }

}
