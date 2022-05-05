package kz.smart.plaza.users.controllers.rest.v1;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import kz.smart.plaza.users.controllers.BaseController;
import kz.smart.plaza.users.models.entities.User;
import kz.smart.plaza.users.models.errors.ServiceException;
import kz.smart.plaza.users.models.requests.*;
import kz.smart.plaza.users.models.responses.success.SuccessResponse;
import kz.smart.plaza.users.services.v1.UserAttemptServiceV1;
import kz.smart.plaza.users.services.v1.UserPromoServiceV1;
import kz.smart.plaza.users.services.v1.UserServiceV1;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class UserControllerV1 extends BaseController {
    private UserServiceV1 userServiceV1;
    private UserAttemptServiceV1 userAttemptServiceV1;

    @GetMapping("/all")
    public ResponseEntity<?> getAll(@RequestParam Optional<Integer> page,
                                    @RequestParam Optional<Integer> size,
                                    @RequestParam Optional<String[]> sortBy,
                                    @RequestParam(value = "search") Optional<String> search) {
        return buildResponse(userServiceV1.getAll(search, page, size, sortBy), HttpStatus.OK);
    }

    @GetMapping("/test-all")
    public ResponseEntity<?> getAllIds() {
        return buildResponse(userServiceV1.getAllIdsTest(), HttpStatus.OK);
    }

    @GetMapping("/getFile")
    @ApiOperation("Получить excelfile")
    public ResponseEntity<?> getAlls(HttpServletRequest request,
                                     HttpServletResponse response,
                                     @RequestParam Optional<String> search) throws IOException, SQLException, NoSuchMethodException {
        Resource resource = userServiceV1.getFile(search);
        String contentType = null;
        response.setContentType("application/xlsx");
        response.addHeader("Content-Disposition", "attachment; filename=" + resource.getFilename());
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getCanonicalPath());
        } catch (IOException ex) {
            System.out.println("could not determine file");
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(Files.readAllBytes(resource.getFile().toPath()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRequest userRequest) throws ServiceException {
        userServiceV1.register(userRequest);
        return buildSuccessResponse(SuccessResponse.builder().message("Смс с кодом подтверждения успешно отправлен").build());
    }

    @PostMapping("/register-cinemax")
    public ResponseEntity<?> registerCinemaxUsers(@RequestParam String phone, @RequestParam String email, @RequestParam Integer cityId) {
        return buildResponse(userServiceV1.registerCinemaxUsers(phone, email,cityId), HttpStatus.OK);
    }

    @PutMapping("/register-cinemax/confirm")
    public ResponseEntity<?> confirmCinemaxUserRegister(@RequestParam Long userId, @RequestParam Integer status) {
        userServiceV1.confirmCinemaxUser(userId, status);
        return buildSuccessResponse(SuccessResponse.builder().message("Успешно подтвержден!").build());
    }

    @PutMapping("/register/confirm")
    public ResponseEntity<?> confirmRegister(@RequestParam String phone, @RequestParam String registrationCode) {
        userServiceV1.confirmRegister(phone, registrationCode);
        return buildSuccessResponse(SuccessResponse.builder().message("Успешно зарегистрирован!").build());
    }

    @PutMapping("/register/resend-sms")
    public ResponseEntity<?> resendSmsToConfirm(@RequestParam String phone) {
        userServiceV1.resendSmsToConfirm(phone);
        return buildSuccessResponse(SuccessResponse.builder().message("Смс успешно отправлено!").build());
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader(value = "userId") Long userId) {
        return buildResponse(userServiceV1.getUserProfile(userId), HttpStatus.OK);
    }

    @PutMapping("/profile/edit")
    public ResponseEntity<?> changeUserProfile(@RequestBody UserEditRequest userEditRequest,
                                               @RequestHeader(value = "userId") Long userId) {
        userServiceV1.changeUserProfile(userEditRequest, userId);
        return buildSuccessResponse(SuccessResponse.builder().message("Профиль пользователя успешно отредактирован").build());
    }

    @PutMapping("/profile/city")
    public ResponseEntity<?> changeUserCity(@RequestParam Integer cityId,
                                            @RequestHeader(value = "userId") Long userId) {
        userServiceV1.changeUserCity(cityId, userId);
        return buildSuccessResponse(SuccessResponse.builder().message("Город пользователя успешно изменен").build());
    }

    @PutMapping(value = "/profile/avatar", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> changeUserAvatar(@ModelAttribute MultipartFile image,
                                              @RequestHeader(value = "userId") Long userId) {
        userServiceV1.changeUserAvatar(image, userId);
        return buildSuccessResponse(SuccessResponse.builder().message("Аватарка пользователя успешно изменен").build());
    }

    @PutMapping("/profile/admin/{id}")
    public ResponseEntity<?> changeUserFromAdmin(@RequestBody UserEditRequest userEditRequest,
                                                 @PathVariable(value = "id") Long id,
                                                 @RequestHeader(value = "userId") Long employeeId) {
        userServiceV1.changeUserFromAdmin(userEditRequest, id, employeeId);
        return buildSuccessResponse(SuccessResponse.builder().message("Профиль пользователя успешно отредактирован").build());
    }

    @PutMapping("/forgot/password")
    public ResponseEntity<?> forgotPasswordSendSms(@RequestParam String phone) {
        userServiceV1.forgotPasswordSendConfirm(phone);
        return buildSuccessResponse(SuccessResponse.builder().message("Забыл пароль! успешно отправлено!").build());
    }


    @PutMapping("/forgot/password/confirm")
    public ResponseEntity<?> forgotPasswordConfirm(@RequestParam String phone, @RequestParam String registrationCode) {
        userServiceV1.confirmForgotPassword(phone, registrationCode);
        return buildSuccessResponse(SuccessResponse.builder().message("Код для восстановления пароля совпадает!" +
                " Пожалуйста введите новый пароль.").build());
    }

    @PutMapping("/forgot/password/confirm-change")
    public ResponseEntity<?> forgotPasswordConfirmChange(@RequestParam String phone,
                                                         @RequestParam String registrationCode,
                                                         @RequestParam String newPassword) {
        userServiceV1.confirmForgotPasswordAndChange(phone, registrationCode, newPassword);
        return buildSuccessResponse(SuccessResponse.builder().message("Код для восстановления пароля совпадает!" +
                " Пароль успешно восстановлен.").build());
    }

    @PutMapping("/forgot/password/change")
    public ResponseEntity<?> changeAndResetPassword(@RequestParam String phone,
                                                    @RequestParam String registrationCode,
                                                    @RequestParam String newPassword) {
        userServiceV1.confirmForgotPasswordAndChange(phone, registrationCode, newPassword);
        return buildSuccessResponse(SuccessResponse.builder().message("Код для восстановления пароля совпадает!" +
                "Пароль успешно восстановлен").build());
    }

    @PutMapping("/password/change")
    @ApiOperation("Замена пароля!")
    public ResponseEntity<?> changeExistingPassword(@RequestParam String oldPassword, @RequestParam String newPassword,
                                                    @RequestHeader(value = "userId") Long userId) {
        userServiceV1.changeExistingPassword(oldPassword, newPassword, userId);
        return buildSuccessResponse(SuccessResponse.builder().message("Пароль успешно заменен!").build());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest) throws ServiceException {
        return buildResponse(userServiceV1.loginUser(userLoginRequest), HttpStatus.OK);
    }

    @PostMapping("/login/check")
    public ResponseEntity<?> loginCheck(@RequestBody UserLoginRequest userLoginRequest) throws ServiceException {
        userServiceV1.checkUserLogin(userLoginRequest);
        return buildSuccessResponse(SuccessResponse.builder().message("Пользователь зарегистрирован").build());
    }

    @PostMapping("/pin/check")
    public ResponseEntity<?> changePinStatus(@RequestHeader(value = "userId") Long userId) throws ServiceException {
        userServiceV1.changePinStatus(userId);
        return buildSuccessResponse(SuccessResponse.builder().message("Статус пина успешно изменен").build());
    }


    @GetMapping("/find")
    public ResponseEntity<?> getUserByQrOrPhone(@RequestParam Optional<String> phone, @RequestParam Optional<String> qr) {
        return buildResponse(userServiceV1.findByQrOrPhone(phone, qr), HttpStatus.OK);
    }

    @GetMapping("/find/lara")
    public ResponseEntity<?> getUserByQrOrPhoneLara(@RequestParam Optional<String> phone,
                                                    @RequestParam Optional<String> qr,
                                                    @RequestHeader(value = "userId") Long employeeId) {
        return buildResponse(userServiceV1.findByQrOrPhoneLara(phone, qr, employeeId), HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        return buildResponse(userServiceV1.findById(userId), HttpStatus.OK);
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<?> getUserProfileByIdAdmin(@PathVariable Long id) {
        return buildResponse(userServiceV1.getUserProfile(id), HttpStatus.OK);
    }

    @GetMapping("/userIds")
    public ResponseEntity<?> getUsersByIds(@RequestParam List<Long> userIds) {
        return buildResponse(userServiceV1.findUsersByIds(userIds), HttpStatus.OK);
    }

    @PostMapping("/userIds-body")
    public ResponseEntity<?> getUsersByIdsV2(@RequestBody UserBodyRequest userBodyRequest) {
        return buildResponse(userServiceV1.findUsersByIdsV2(userBodyRequest), HttpStatus.OK);
    }

//    @PostMapping("/userIds")
//    public ResponseEntity<?> getUsersByIdsPost(@RequestBody List<Long> userIds) {
//        return buildResponse(userServiceV1.findUsersByIds(userIds), HttpStatus.OK);
//    }

    @PostMapping("/feedback")
    public ResponseEntity<?> postUsersQuestions(@RequestParam String phone,
                                                @RequestParam String comment) {
        return buildResponse(userServiceV1.postUsersQuestions(phone, comment), HttpStatus.OK);
    }

    @GetMapping("/feedback/all")
    public ResponseEntity<?> getAllFeedbackRequests(@RequestParam Optional<Integer> page,
                                                    @RequestParam Optional<Integer> size,
                                                    @RequestParam Optional<String[]> sortBy,
                                                    @RequestHeader(value = "userId") Long userId,
                                                    @RequestParam Optional<Boolean> solved) {
        return buildResponse(userServiceV1.getAllFeedbackRequests(page, size, sortBy, userId, solved), HttpStatus.OK);
    }

    @GetMapping("/feedback/solved/{id}")
    public ResponseEntity<?> changeUserRequestStatus(@PathVariable Long id, @RequestHeader(value = "userId") Long userId) {
        userServiceV1.changeRequestStatus(id, userId);
        return buildResponse("Обращение успешно решено", HttpStatus.OK);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<?> changeQr(@PathVariable Long id, @RequestParam String qr) {
//        userServiceV1.changeQr(id, qr);
//        return buildSuccessResponse(SuccessResponse.builder().message("Success!").build());
//    }

    @PutMapping("/qr")
    public ResponseEntity<?> changeUserQr(@RequestHeader(value = "userId") Long userId) {
        return buildResponse(userServiceV1.changeQr(userId), HttpStatus.OK);
    }

    @GetMapping("/get-qr/{id}")
    public ResponseEntity<?> getUserQr(@PathVariable Long id) {
        return buildResponse(userServiceV1.getUserQrById(id), HttpStatus.OK);
    }

    @PutMapping("/qr/all")
    public ResponseEntity<?> updateUserQrs(@RequestHeader(value = "userId") Long userId) {
        userServiceV1.updateUserQrs(userId);
        return buildResponse(SuccessResponse.builder().message("QRs updated!").build(), HttpStatus.OK);
    }

    @PutMapping("/tech-work")
    public ResponseEntity<?> techWorkChange(@RequestHeader(value = "userId") Long employeeId,
                                            @RequestParam Boolean status) {
        userServiceV1.techWorkChange(employeeId, status);
        if (status) {
            return buildSuccessResponse(SuccessResponse.builder().message("`Технические работы начались!").build());
        } else {
            return buildSuccessResponse(SuccessResponse.builder().message("`Технические работы завершены!").build());
        }
    }

    @GetMapping("/tech-work")
    public ModelAndView getTechWork() {
        return new ModelAndView("techWork");
    }

    @PatchMapping("/pushable")
    public ResponseEntity<?> pushStatusChange(@RequestHeader(value = "userId") Long userId) {
        return buildResponse(userServiceV1.pushStatusChange(userId), HttpStatus.OK);
    }

    @PutMapping("/admin/ban/{id}")
    public ResponseEntity<?> banUserFromAdmin(@RequestHeader(value = "userId") Long employeeId,
                                              @PathVariable Long id,
                                              @RequestParam Integer ban) {
        userServiceV1.banUserFromAdmin(employeeId, id, ban);
        if (ban.equals(1)) {
            return buildSuccessResponse(SuccessResponse.builder().message("Пользователь успешно заблокирован").build());
        } else {
            return buildSuccessResponse(SuccessResponse.builder().message("Пользователь успешно разблокирован").build());
        }
    }

    @GetMapping("/registered-total")
    public ResponseEntity<?> getTotalUsersCount() {
        Integer totalUsers = userServiceV1.getTotalUsersCount();
        Integer totalRegisteredMonth = userServiceV1.getCountOfUsersInAMonth();
        HashMap<String, Integer> map = new HashMap<>();
        map.put("total", totalUsers);
        map.put("month", totalRegisteredMonth);
        return buildResponse(map, HttpStatus.OK);
    }

    @PutMapping("/admin/change-password/{id}")
    public ResponseEntity<?> changeUserPasswordFromAdmin(@RequestHeader(value = "userId") Long employeeId,
                                                         @PathVariable Long id,
                                                         @RequestParam String password) {
        userServiceV1.changeUserPasswordFromAdmin(employeeId, id, password);
        return buildSuccessResponse(SuccessResponse.builder().message("Пароль успешно обновлен").build());
    }


    @GetMapping("/send-kukaga")
    public ResponseEntity<?> sendKuke() {
        userServiceV1.sendKuke();
        return buildSuccessResponse("sa");
    }


    @GetMapping("/chat/users")
    public ResponseEntity<?> getChatUsers() {

        return buildResponse(userServiceV1.getChatUsers(), HttpStatus.OK);
    }


}
