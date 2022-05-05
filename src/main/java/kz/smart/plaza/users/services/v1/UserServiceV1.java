package kz.smart.plaza.users.services.v1;

import kz.smart.plaza.users.models.entities.User;
import kz.smart.plaza.users.models.entities.UserTemplate;
import kz.smart.plaza.users.models.requests.UserBodyRequest;
import kz.smart.plaza.users.models.requests.UserEditRequest;
import kz.smart.plaza.users.models.requests.UserLoginRequest;
import kz.smart.plaza.users.models.requests.UserRequest;
import kz.smart.plaza.users.models.responses.UserIdsResponse;
import kz.smart.plaza.users.models.responses.UserQrResponse;
import kz.smart.plaza.users.models.responses.UserResponse;
import kz.smart.plaza.users.models.responses.UserFeignResponse;
import kz.smart.plaza.users.models.responses.cinemaxFc.CinemaxDataResponse;
import kz.smart.plaza.users.models.responses.cinemaxSignature.CinemaxIdentifyResponse;
import kz.smart.plaza.users.models.responses.cinemaxSignature.CinemaxInitResponse;
import kz.smart.plaza.users.models.responses.laravel.UserLaraResponse;
import kz.smart.plaza.users.models.responses.paginator.PageResponse;
import kz.smart.plaza.users.models.responses.webCdm.CinemaxUserRegisteredResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserServiceV1 {

    User getUser(Long id);
    UserTemplate register(UserRequest userRequest);
    PageResponse getAll(Optional<String> search, Optional<Integer> page,
                        Optional<Integer> size,
                        Optional<String[]> sortBy);
    UserResponse findByQrOrPhone(Optional<String> phone, Optional<String> qr);
    CinemaxDataResponse findByQrCinemx(String qr);

    CinemaxInitResponse initUserCinemax(Map<String, String> formData, Long userId);

    CinemaxIdentifyResponse identifyUserCinemax(Map<String, String> formData);

    UserLaraResponse findByQrOrPhoneLara(Optional<String> phone, Optional<String> qr, Long employeeId);
    UserFeignResponse findById(Long userId);
    List<UserFeignResponse> findUsersByIds(List<Long> userId);
    List<UserFeignResponse> findUsersByIdsV2(UserBodyRequest userBodyRequest);

    String postUsersQuestions(String phone, String comment);

    PageResponse getAllFeedbackRequests(Optional<Integer> page,
                                        Optional<Integer> size,
                                        Optional<String[]> sortBy,
                                        Long employeeId,
                                        Optional<Boolean> solved);

    void changeRequestStatus(Long id, Long employeeId);

    UserQrResponse changeQr(Long id);
    void updateUserQrs(Long id);
    void techWorkChange(Long employeeId, Boolean status);
    ResponseEntity<?> pushStatusChange(Long userId);

    void banUserFromAdmin(Long employeeId, Long userId, Integer ban);

    Integer getTotalUsersCount();
    Integer getCountOfUsersInAMonth();

    void changeUserPasswordFromAdmin(Long employeeId, Long userId, String password);
    void confirmRegister(String phone, String registrationCode);
    void confirmForgotPassword(String phone, String registrationCode);
    void confirmForgotPasswordAndChange(String phone, String registrationCode, String password);
    void changeUserPassword(Long userId, String password);
    void resendSmsToConfirm(String phone);
    void forgotPasswordSendConfirm(String phone);
    UserResponse getUserProfile(Long userId);

    Resource getFile(Optional<String> search) throws IOException, SQLException, NoSuchMethodException;


    void changeUserProfile(UserEditRequest userEditRequest , Long userId);
    void changeUserCity(Integer cityId, Long userId);
    void changeUserAvatar(MultipartFile image, Long userId);

    void changeUserFromAdmin(UserEditRequest userEditRequest, Long userId, Long employeeId);
    void changeAndResetPassword(String phone, String newPassword);
    void changeExistingPassword(String oldPassword, String newPassword, Long userId);
    UserResponse loginUser(UserLoginRequest userLoginRequest);
    void checkUserLogin (UserLoginRequest userLoginRequest);
    void changePinStatus(Long userId);

    void sendKuke();

    List<User> getChatUsers();

    List<UserIdsResponse> getAllIdsTest();

    CinemaxUserRegisteredResponse registerCinemaxUsers(String phone, String email, Integer cityId);

    void confirmCinemaxUser(Long userId, Integer status);


    String getUserQrById(Long userId);
}
