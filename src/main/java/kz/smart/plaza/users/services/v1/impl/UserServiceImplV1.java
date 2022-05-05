package kz.smart.plaza.users.services.v1.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import com.querydsl.core.types.dsl.BooleanExpression;
import kz.smart.plaza.users.clients.AuthServiceClient;
import kz.smart.plaza.users.methods.*;
import kz.smart.plaza.users.models.entities.*;
import kz.smart.plaza.users.models.errors.ErrorCode;
import kz.smart.plaza.users.models.errors.ServiceException;
import kz.smart.plaza.users.models.requests.*;
import kz.smart.plaza.users.models.responses.*;
import kz.smart.plaza.users.models.responses.cinemaxFc.CinemaxDataResponse;
import kz.smart.plaza.users.models.responses.cinemaxSignature.CinemaxIdentifyResponse;
import kz.smart.plaza.users.models.responses.cinemaxSignature.CinemaxInitResponse;
import kz.smart.plaza.users.models.responses.laravel.UserLaraResponse;
import kz.smart.plaza.users.models.responses.sms.UserToSmsResponse;
import kz.smart.plaza.users.models.responses.webCdm.CinemaxUserRegisteredResponse;
import kz.smart.plaza.users.repositories.FeedbackRepository;
import kz.smart.plaza.users.repositories.UserRepository;
import kz.smart.plaza.users.repositories.UserTagRepository;
import kz.smart.plaza.users.repositories.UserTemplateRepository;
import kz.smart.plaza.users.services.v1.FileServiceV1;
import kz.smart.plaza.users.services.v1.UserAttemptServiceV1;
import kz.smart.plaza.users.services.v1.UserServiceV1;
import kz.smart.plaza.users.models.responses.paginator.PageResponse;
import kz.smart.plaza.users.predicates.UserPredicateBuilder;
import kz.smart.plaza.users.services.v1.UserTagServiceV1;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class UserServiceImplV1 implements UserServiceV1 {

    private UserRepository userRepository;
    private UserTagRepository userTagRepository;
    private UserTagServiceV1 userTagServiceV1;
    private UserTemplateRepository userTemplateRepository;
    private Pagination pagination;
    private QRGenerator qrGenerator;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private RedisTemplate redisTemplate;
    private FileServiceV1 fileServiceV1;
    private SmsSender smsSender;
    private CallApi callApi;
    private FeedbackRepository feedbackRepository;
    private AuthServiceClient authServiceClient;
    private ExportDataToExcel exportDataToExcel;
    private UserAttemptServiceV1 userAttemptServiceV1;
    private Notificate notificate;


    @Override
    public User getUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.orElseThrow(() -> ServiceException.builder()
                .errorCode(ErrorCode.INVALID_PHONE)
                .httpStatus(HttpStatus.NOT_FOUND)
                .message("Пользователь не найден")
                .build());
    }

    @Override
    public UserTemplate register(UserRequest userRequest) {
        System.out.println("GETTING USER REGISTER" + userRequest);
        Optional<User> userOptional = userRepository.findByPhone(userRequest.getPhone());
        userOptional.ifPresent(s -> {
            throw ServiceException.builder()
                    .errorCode(ErrorCode.INVALID_EMPLOYEE)
                    .httpStatus(HttpStatus.CONFLICT)
                    .message("Пользователь уже зарегистрирован")
                    .build();
        });
        String password = "password";
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            password = userRequest.getPassword();
        }
        UserTemplate userTemplate = new UserTemplate();
        userTemplate.setPassword(bCryptPasswordEncoder.encode(password));
        userTemplate.setName(userRequest.getFirstName());
        userTemplate.setSurname(userRequest.getLastName());
        userTemplate.setPhone(userRequest.getPhone());
        userTemplate.setCityId(userRequest.getCityId());
        userTemplate.setBirthDate(userRequest.getBirthdate());
        userTemplate.setEmail(userRequest.getEmail());
        userTemplate.setPlatform(userRequest.getPlatform());
        userTemplate.setGender(userRequest.getGender());
        userTemplate.setLanguage(userRequest.getLanguage());
        String smsConfirmCode = Integer.toString(ThreadLocalRandom.current().nextInt(100000, 1000000));
        userTemplate.setActivationCode(smsConfirmCode);
        userTemplateRepository.save(userTemplate);
        UserToSmsResponse userToSmsResponse = new UserToSmsResponse();
        userToSmsResponse.setUserId(userTemplate.getId());
        userToSmsResponse.setUserPhone(userTemplate.getPhone());
        userToSmsResponse.setSmsConfirmCode(smsConfirmCode);
        smsSender.SendSmsToUser(userToSmsResponse, "register");
        return userTemplate;
    }

    @Override
    public void confirmRegister(String phone, String registrationCode) {
        UserTemplate userTemplate = userTemplateRepository.findFirstByPhoneOrderByCreatedAtDesc(phone);
        if (!userTemplate.getActivationCode().equals(registrationCode)) {
            throw ServiceException.builder().httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT)
                    .message("Не правильный код!")
                    .build();
        }
        Optional<User> userOptional = userRepository.findByPhone(phone);
        userOptional.ifPresent(s -> {
            throw ServiceException.builder()
                    .errorCode(ErrorCode.INVALID_EMPLOYEE)
                    .httpStatus(HttpStatus.CONFLICT)
                    .message("Пользователь уже зарегистрирован")
                    .build();
        });
        User user = new User();
        user.setBanned(false);
        user.setPassword(userTemplate.getPassword());
        user.setPlatform(userTemplate.getPlatform());
        user.setEmail(userTemplate.getEmail());
        user.setBirthDate(userTemplate.getBirthDate());
        user.setPhone(userTemplate.getPhone());
        user.setCityId(userTemplate.getCityId());
        user.setName(userTemplate.getName());
        user.setLanguage(userTemplate.getLanguage());
        user.setSurname(userTemplate.getSurname());
        user.setActivationCode(userTemplate.getActivationCode());
        user.setGender(userTemplate.getGender());
        user.setTechWork(false);
        user.setPushable(true);
        user.setPincode(false);
        userRepository.save(user);
    }

    @Override
    public void confirmForgotPassword(String phone, String registrationCode) {
        Optional<User> userOptional = userRepository.findByPhone(phone);
        if (!userOptional.isPresent()) {
            throw ServiceException.builder()
                    .errorCode(ErrorCode.INVALID_EMPLOYEE)
                    .httpStatus(HttpStatus.CONFLICT)
                    .message("Ошибка! Пользователь не найден")
                    .build();
        }
        User user = userOptional.get();
        UserAttempt userAttempt = userAttemptServiceV1.getUserAttemptByUserId(user);
        if (userAttempt.getSmsAttempts() > 5) {
            throw ServiceException.builder().httpStatus(HttpStatus.FORBIDDEN)
                    .errorCode(ErrorCode.TOO_MANY_ATTEMPTS)
                    .message("Слишком много попыток, попоробуйте через час")
                    .build();
        }
        userAttempt.setSmsAttempts(userAttempt.getSmsAttempts() + 1);
        userAttemptServiceV1.save(userAttempt);
        if (!user.getActivationCode().equals(registrationCode)) {
            throw ServiceException.builder().httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT)
                    .message("Не правильный код восстановления!")
                    .build();
        }
    }

    @Override
    public void confirmForgotPasswordAndChange(String phone, String registrationCode, String password) {
        System.out.println("Check 1");
        Optional<User> userOptional = userRepository.findByPhone(phone);
        if (!userOptional.isPresent()) {
            throw ServiceException.builder()
                    .errorCode(ErrorCode.INVALID_EMPLOYEE)
                    .httpStatus(HttpStatus.CONFLICT)
                    .message("Ошибка! Пользователь не найден")
                    .build();
        }
        User user = userOptional.get();
        UserAttempt userAttempt = userAttemptServiceV1.getUserAttemptByUserId(user);
        if (userAttempt.getSmsAttempts() > 5) {

            throw ServiceException.builder().httpStatus(HttpStatus.FORBIDDEN)
                    .errorCode(ErrorCode.TOO_MANY_ATTEMPTS)
                    .message("Слишком много попыток, попоробуйте через час")
                    .build();
        }
        userAttempt.setSmsAttempts(userAttempt.getSmsAttempts() + 1);
        userAttemptServiceV1.save(userAttempt);
        if (!user.getActivationCode().equals(registrationCode)) {
            throw ServiceException.builder().httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT)
                    .message("Не правильный код восстановления!")
                    .build();
        }
        if (password == null) {
            throw ServiceException.builder()
                    .errorCode(ErrorCode.INVALID_ARGUMENT)
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("Пустой пароль")
                    .build();
        }
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
    }

    public void resetPasswordForgetAttemts(User user) {
        UserAttempt userAttempt = userAttemptServiceV1.getUserAttemptByUserId(user);
        userAttempt.setSmsAttempts(0);
        userAttemptServiceV1.save(userAttempt);
    }


    @Override
    public void changeUserPassword(Long userId, String password) {
        Optional<User> userOptional = userRepository.findFirstById(userId);
    }

    @Override
    public void resendSmsToConfirm(String phone) {
        UserTemplate userTemplate = userTemplateRepository.findFirstByPhoneOrderByCreatedAtDesc(phone);
        String smsConfirmCode = Integer.toString(ThreadLocalRandom.current().nextInt(100000, 1000000));
        userTemplate.setActivationCode(smsConfirmCode);
        userTemplateRepository.save(userTemplate);
        UserToSmsResponse userToSmsResponse = new UserToSmsResponse();
        userToSmsResponse.setUserId(userTemplate.getId());
        userToSmsResponse.setUserPhone(userTemplate.getPhone());
        userToSmsResponse.setSmsConfirmCode(smsConfirmCode);
        smsSender.SendSmsToUser(userToSmsResponse, "resendSms");
    }

    @Override
    public void forgotPasswordSendConfirm(String phone) {
        Optional<User> userOptional = userRepository.findByPhone(phone);
        if (!userOptional.isPresent()) {
            throw ServiceException.builder()
                    .errorCode(ErrorCode.INVALID_EMPLOYEE)
                    .httpStatus(HttpStatus.CONFLICT)
                    .message("Ошибка! Пользователь не найден")
                    .build();
        }
        User user = userOptional.get();
        String smsConfirmCode = Integer.toString(ThreadLocalRandom.current().nextInt(100000, 1000000));
        user.setActivationCode(smsConfirmCode);
        userRepository.save(user);
        UserToSmsResponse userToSmsResponse = new UserToSmsResponse();
        userToSmsResponse.setUserId(user.getId());
        userToSmsResponse.setUserPhone(user.getPhone());
        userToSmsResponse.setSmsConfirmCode(smsConfirmCode);
        smsSender.SendSmsToUser(userToSmsResponse, "forgotPassword");
    }

    @Override
    public UserResponse getUserProfile(Long userId) {
        User user;
        user = userRepository.findById(userId).orElseThrow(() -> ServiceException.builder()
                .errorCode(ErrorCode.INVALID_PHONE)
                .httpStatus(HttpStatus.NOT_FOUND)
                .message("Пользователь не найден")
                .build());
        return pagination.collect(user);
    }

    @Override
    public Resource getFile(Optional<String> search) throws IOException, SQLException, NoSuchMethodException {
        int count = userRepository.getCountOfUsers();
        List<UserResponse> userResponses = (List<UserResponse>) getAll(search, Optional.empty(), Optional.of(count), Optional.empty()).getContent();

        String directory = exportDataToExcel.Export(userResponses, count);
        return fileServiceV1.getFile(directory, "excel");
    }

    @Override
    public void changeUserProfile(UserEditRequest userEditRequest, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> ServiceException.builder()
                .errorCode(ErrorCode.INVALID_PHONE)
                .httpStatus(HttpStatus.NOT_FOUND)
                .message("Пользователь не найден")
                .build());
        user.setLanguage(userEditRequest.getLanguage());
        user.setGender(userEditRequest.getGender());
        user.setSurname(userEditRequest.getLastName());
//        String password = "password";
//        if(userEditRequest.getPassword() != null && !userEditRequest.getPassword().isEmpty()) {
//            password = userEditRequest.getPassword();
//        }
//        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setName(userEditRequest.getFirstName());
        user.setBirthDate(userEditRequest.getBirthdate());
        user.setCityId(userEditRequest.getCityId());
        user.setEmail(userEditRequest.getEmail());
        userRepository.save(user);
    }

    @Override
    public void changeUserCity(Integer cityId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> ServiceException.builder()
                .errorCode(ErrorCode.USER_NOT_FOUND)
                .httpStatus(HttpStatus.NOT_FOUND)
                .message("Пользователь не найден")
                .build());
        user.setCityId(cityId);
        userRepository.save(user);
    }

    @Override
    public void changeUserAvatar(MultipartFile image, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> ServiceException.builder()
                .errorCode(ErrorCode.USER_NOT_FOUND)
                .httpStatus(HttpStatus.NOT_FOUND)
                .message("Пользователь не найден")
                .build());
        String avatar_url = fileServiceV1.storeFile(image, "avatar");
        user.setAvatar(avatar_url);
        userRepository.save(user);
    }

    @Override
    public void changeUserFromAdmin(UserEditRequest userEditRequest, Long userId, Long employeeId) {
        Map employeeMap = getEmployeeFromRedis(employeeId);
        User user = userRepository.findById(userId).orElseThrow(() -> ServiceException.builder()
                .errorCode(ErrorCode.INVALID_PHONE)
                .httpStatus(HttpStatus.NOT_FOUND)
                .message("Пользователь не найден")
                .build());
        user.setGender(userEditRequest.getGender());
        user.setSurname(userEditRequest.getLastName());
        user.setName(userEditRequest.getFirstName());
//        user.setBirthDate(userEditRequest.getBirthdate());
        user.setCityId(userEditRequest.getCityId());
        user.setEmail(userEditRequest.getEmail());
        user.setComment(userEditRequest.getComment());
        userTagServiceV1.editUserTags(user, userEditRequest.getTags());
        userRepository.save(user);

    }

    @Override
    public void changeAndResetPassword(String phone, String newPassword) {
        Optional<User> userOptional = userRepository.findByPhone(phone);
        if (!userOptional.isPresent()) {
            throw ServiceException.builder()
                    .errorCode(ErrorCode.INVALID_PHONE)
                    .httpStatus(HttpStatus.CONFLICT)
                    .message("Ошибка! Пользователь не найден")
                    .build();
        }
        User user = userOptional.get();
        if (newPassword == null) {
            throw ServiceException.builder()
                    .errorCode(ErrorCode.INVALID_ARGUMENT)
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("Пустой пароль")
                    .build();
        }
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void changeExistingPassword(String oldPassword, String newPassword, Long userId) {
        Optional<User> userOptional = userRepository.findFirstById(userId);
        if (!userOptional.isPresent()) {
            throw ServiceException.builder()
                    .errorCode(ErrorCode.USER_NOT_FOUND)
                    .httpStatus(HttpStatus.CONFLICT)
                    .message("Ошибка! Пользователь не найден")
                    .build();
        }
        User user = userOptional.get();
        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw ServiceException.builder().errorCode(ErrorCode.INVALID_ARGUMENT)
                    .httpStatus(HttpStatus.FORBIDDEN).message("Неверный пароль")
                    .build();
        }
        if (newPassword == null) {
            throw ServiceException.builder()
                    .errorCode(ErrorCode.EMPTY_CODE)
                    .httpStatus(HttpStatus.CONFLICT)
                    .message("Пустой пароль")
                    .build();
        }
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
    }


    @Override
    public PageResponse getAll(Optional<String> search, Optional<Integer> page, Optional<Integer> size, Optional<String[]> sortBy) {
        UserPredicateBuilder builder = new UserPredicateBuilder();
        if (search.isPresent()) {
            Pattern pattern = Pattern.compile("(\\w+?[.]\\w+?|\\w+?)" +
                    "(:|<|>|!:)" +
                    "(\\w+?\\s\\w+?|\\w+?|\\w+?\\s\\w+?\\s\\w+?|\\w+?[-]\\w+?[-]\\w+?\\w+?),", Pattern.UNICODE_CHARACTER_CLASS);
            Matcher matcher = pattern.matcher(search.get() + ",");
            while (matcher.find()) {
                builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
            }
        }
        builder.with("isConfirmed", ":", true);
        BooleanExpression exp = builder.build();
        Page<User> usersPage = userRepository.findAll(exp, pagination.paginate(page, size, sortBy));
        return pagination.userResponses(usersPage);
    }

    @Override
    public UserResponse findByQrOrPhone(Optional<String> phone, Optional<String> qr) {
        User user;
        if (phone.isPresent() && !phone.get().equals("") && phone.get().length() > 6) {
            user = userRepository.findByPhone(phone.get()).orElseThrow(() -> ServiceException.builder()
                    .errorCode(ErrorCode.INVALID_PHONE)
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .message("Пользователь по такому номеру не найден!")
                    .build());
            return pagination.collect(user);
        }

        if (qr.isPresent() && !qr.get().equals("")) {
            user = userRepository.findFirstByQrOrderByUpdatedAtDesc(qr.get()).orElseThrow(() -> ServiceException.builder()
                    .errorCode(ErrorCode.INVALID_QR)
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .message("Пользователь по такому qr не найден!")
                    .build());
            return pagination.collect(user);
        }
        throw ServiceException.builder().httpStatus(HttpStatus.BAD_REQUEST)
                .errorCode(ErrorCode.INVALID_ARGUMENT)
                .message("Неправильные данные")
                .build();
    }

    @Override
    public CinemaxDataResponse findByQrCinemx(String qr) {
        System.out.println("CINEMAX FC QR " + qr);
        User user = userRepository.findFirstByQrOrderByUpdatedAtDesc(qr).orElseThrow(() -> ServiceException.builder()
                .errorCode(ErrorCode.INVALID_QR)
                .httpStatus(HttpStatus.NOT_FOUND)
                .message("Пользователь по такому qr не найден!")
                .build());
        return pagination.collectCinemaxFc(user);
    }

    @Override
    public CinemaxInitResponse initUserCinemax(Map<String, String> formData, Long userId) {
        long brand_id = 0L;
        long category_id = 0L;
        String token = "";
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            if (entry.getKey().equals("brand")) brand_id = Long.parseLong(entry.getValue());
//            if (entry.getKey().equals("signature")) signature = entry.getValue();
            if (entry.getKey().equals("category")) category_id = Long.parseLong(entry.getValue());
            if (entry.getKey().equals("token")) token = entry.getValue();
        }
//        UserInfoResponse userInfoResponse = callApi.getClientInfoByToken(token);
//        if (userInfoResponse != null) {
//            user_id = userInfoResponse.getUserId();
//        }
//        try {
//            ResponseEntity<UserInfoResponse> response = authServiceClient.getClientInfoByToken(token);
//            if (response.getBody() != null){
//                user_id = response.getBody().getUserId();
//            }
//        } catch (FeignException f) {
//            return null;
//        }
        Optional<User> userOptional = userRepository.findFirstById(userId);
        if (!userOptional.isPresent()) {
            throw ServiceException.builder()
                    .errorCode(ErrorCode.INVALID_TOKEN)
                    .httpStatus(HttpStatus.CONFLICT)
                    .message("Ошибка! Пользователь не найден")
                    .build();
        }
        System.out.println("I AM USER" + userOptional.get());
        if (userOptional.get().getQr() == null) {
            try {
                qrGenerator.generateUserQr(userOptional.get());
                userRepository.save(userOptional.get());
            } catch (WriterException | IOException e) {
                throw ServiceException.builder().message(e.getMessage()).
                        httpStatus(HttpStatus.BAD_REQUEST).errorCode(ErrorCode.INVALID_BRAND).build();
            }
        }
        return CinemaxInitResponse.builder()
                .authorised(true)
                .modifier(0)
                .phone(userOptional.get().getPhone())
                .user_id(userOptional.get().getId())
                .qr(userOptional.get().getQr())
                .build();
    }

    @Override
    public CinemaxIdentifyResponse identifyUserCinemax(Map<String, String> formData) {
        String phone = "";
        String qr_code = "";

        for (Map.Entry<String, String> entry : formData.entrySet()) {
//            if (entry.getKey().equals("brand")) brand_id = Long.parseLong(entry.getValue());
//            if (entry.getKey().equals("signature")) signature = entry.getValue();
            if (entry.getKey().equals("smart_id")) qr_code = entry.getValue();
            if (entry.getKey().equals("phone")) phone = entry.getValue();
        }
        Optional<User> userOptional1 = userRepository.findByPhone(phone);
        Optional<User> userOptional2 = userRepository.findFirstByQrOrderByUpdatedAtDesc(qr_code);
        if (!userOptional1.isPresent()) {
            throw ServiceException.builder()
                    .errorCode(ErrorCode.INVALID_TOKEN)
                    .httpStatus(HttpStatus.CONFLICT)
                    .message("Ошибка! Пользователь не найден")
                    .build();
        }
        User user = userOptional1.get();
        if (!userOptional2.isPresent()) {
            try {
                qrGenerator.generateUserQr(user);
                userRepository.save(user);
            } catch (WriterException | IOException e) {
                throw ServiceException.builder().message(e.getMessage()).
                        httpStatus(HttpStatus.BAD_REQUEST).errorCode(ErrorCode.INVALID_BRAND).build();
            }
        }
        BonusRequest bonusRequest = callApi.getBonus(user.getId());
        return CinemaxIdentifyResponse.builder()
                .user(user.getId())
                .level(2)
                .bonus(bonusRequest.getActiveBonuses().toString())
                .cards(new ArrayList<>())
                .build();
    }

    @Override
    public UserLaraResponse findByQrOrPhoneLara(Optional<String> phone, Optional<String> qr, Long employeeId) {
        System.out.println("LARA IDENTIFY " + phone + " qr " + qr + " id " + employeeId);
        Map userMap = (Map) redisTemplate.opsForHash().get("employeeId", employeeId.toString());
        if (userMap == null) {
            throw ServiceException.builder().message("Не найден такой сотрудник, " +
                    "переавторизуйтесь или обратитесь к технической поддержке").httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT).build();
        }
        User user;
        if (qr.isPresent() && !qr.get().equals("")) {
            user = userRepository.findFirstByQrOrderByUpdatedAtDesc(qr.get()).orElseThrow(() -> ServiceException.builder()
                    .errorCode(ErrorCode.INVALID_QR)
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("Пользователь по такому qr не найден!")
                    .build());
            return pagination.collectLaravel(user);
        }
        if (phone.isPresent() && !phone.get().equals("")) {
            user = userRepository.findByPhone(phone.get()).orElseThrow(() -> ServiceException.builder()
                    .errorCode(ErrorCode.INVALID_PHONE)
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("Пользователь по такому номеру не найден!")
                    .build());
            return pagination.collectLaravel(user);
        }
        throw ServiceException.builder().httpStatus(HttpStatus.BAD_REQUEST)
                .errorCode(ErrorCode.INVALID_ARGUMENT)
                .message("Неправильные данные")
                .build();
    }

    @Override
    public UserFeignResponse findById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        List<UserTag> userTags = userTagRepository.findAllByDeletedAtIsNullAndUser_Id(userId);
        List<TagResponse> tagResponses = new ArrayList<>();
        userTags.forEach(element -> {
            TagResponse tagResponse = TagResponse.builder()
                    .id(element.getTag().getId())
                    .name(element.getTag().getName())
                    .noTransactionBlocking(element.getTag().getNoTransactionBlocking())
                    .build();
            tagResponses.add(tagResponse);
        });
        User user = userOptional.orElseThrow(() -> ServiceException.builder().message("Не найден пользователь")
                .errorCode(ErrorCode.INVALID_ARGUMENT).httpStatus(HttpStatus.NOT_FOUND).build());

        return UserFeignResponse.builder().id(user.getId())
                .name(user.getName() + " " + user.getSurname())
                .phone(user.getPhone())
                .photoUrl(user.getAvatar())
                .email(user.getEmail())
                .tags(tagResponses)
                .cityId(user.getCityId())
                .platform(user.getPlatform())
                .gender(user.getGender())
                .birthDate(user.getBirthDate())
                .deviceToken(user.getDeviceToken())
                .build();
    }


    @Override
    public List<UserFeignResponse> findUsersByIds(List<Long> userIds) {
        List<User> users = userRepository.findAllById_In(userIds);
        if (users.isEmpty()) throw ServiceException.builder().errorCode(ErrorCode.INVALID_ARGUMENT)
                .httpStatus(HttpStatus.BAD_REQUEST).message("Пользователи не найдены!")
                .build();
        List<UserFeignResponse> userFeignResponses = new ArrayList<>();
        users.forEach(user -> {
            UserFeignResponse userFeignResponse = UserFeignResponse.builder()
                    .id(user.getId())
                    .name(user.getName() + " " + user.getSurname())
                    .gender(user.getGender())
                    .platform(user.getPlatform())
                    .birthDate(user.getBirthDate())
                    .phone(user.getPhone())
                    .photoUrl(user.getAvatar())
                    .cityId(user.getCityId())
                    .build();
            if (user.getBirthDate() != null) {
                Integer userAge = pagination.calculateUserAge(user.getBirthDate());
                userFeignResponse.setAge(userAge);
            } else {
                userFeignResponse.setAge(null);
            }
            userFeignResponses.add(userFeignResponse);
        });
        return userFeignResponses;
    }

    @Override
    public List<UserFeignResponse> findUsersByIdsV2(UserBodyRequest userBodyRequest) {
        List<User> users = userRepository.findAllById_In(userBodyRequest.getUserIds());
        if (users.isEmpty()) throw ServiceException.builder().errorCode(ErrorCode.INVALID_ARGUMENT)
                .httpStatus(HttpStatus.BAD_REQUEST).message("Пользователи не найдены!")
                .build();
        List<UserFeignResponse> userFeignResponses = new ArrayList<>();
        users.forEach(user -> {
            UserFeignResponse userFeignResponse = UserFeignResponse.builder()
                    .id(user.getId())
                    .name(user.getName() + " " + user.getSurname())
                    .gender(user.getGender())
                    .platform(user.getPlatform())
                    .birthDate(user.getBirthDate())
                    .phone(user.getPhone())
                    .cityId(user.getCityId())
                    .build();
            if (user.getBirthDate() != null) {
                Integer userAge = pagination.calculateUserAge(user.getBirthDate());
                userFeignResponse.setAge(userAge);
            } else {
                userFeignResponse.setAge(null);
            }
            userFeignResponses.add(userFeignResponse);
        });
        return userFeignResponses;
    }

    @Override
    public String postUsersQuestions(String phone, String comment) {
        if (comment != null && !comment.equals("")) {
            Feedback feedback = new Feedback();
            feedback.setPhone(phone);
            feedback.setQuestion(comment);
            feedback.setIsSolved(false);
            feedbackRepository.save(feedback);
            return "Заявка на авторизацию отправлена, ожидайте, это займет некоторое время.";
        }
        return "";
    }

    @Override
    public PageResponse getAllFeedbackRequests(Optional<Integer> page,
                                               Optional<Integer> size,
                                               Optional<String[]> sortBy,
                                               Long employeeId,
                                               Optional<Boolean> solved) {
        Map employeeMap = getEmployeeFromRedis(employeeId);
        Page<Feedback> feedbackPage;
        if (solved.isPresent()) {
            feedbackPage = feedbackRepository.findAllByIsSolvedAndQuestionNot(pagination.paginate(page, size, sortBy), solved.get(), "");
        } else {
            feedbackPage = feedbackRepository.findAllByQuestionNot(pagination.paginate(page, size, sortBy), "");
        }
        return pagination.pageResponse(feedbackPage, feedbackPage.getContent());
    }

    @Override
    public void changeRequestStatus(Long id, Long employeeId) {
        Map employeeMap = getEmployeeFromRedis(employeeId);
        Optional<Feedback> feedbackOptional = feedbackRepository.findById(id);
        if (!feedbackOptional.isPresent()) throw ServiceException.builder().message("Обращение не найдено").
                httpStatus(HttpStatus.BAD_REQUEST).errorCode(ErrorCode.INVALID_TOKEN).build();
        Feedback feedback = feedbackOptional.get();
        feedback.setIsSolved(true);
        feedbackRepository.save(feedback);
    }

    @Override
    public UserQrResponse changeQr(Long id) {
        Optional<User> userOptional = userRepository.findFirstById(id);
        if (!userOptional.isPresent()) throw ServiceException.builder().message("Пользователь не найден").
                httpStatus(HttpStatus.BAD_REQUEST).errorCode(ErrorCode.INVALID_TOKEN).build();

        User user = userOptional.get();
        long oldDate = user.getQrUpdatedAt() != null ? user.getQrUpdatedAt().getTime() / 60000 : 30;
        long newDate = new Date().getTime() / 60000;
        if (newDate - oldDate > 20) {
            System.out.println("Нужно обновить");
            try {
                qrGenerator.generateUserQr(user);
                user.setQrUpdatedAt(new Date());
                userRepository.save(user);
            } catch (WriterException | IOException e) {
                throw ServiceException.builder().message(e.getMessage()).
                        httpStatus(HttpStatus.BAD_REQUEST).errorCode(ErrorCode.SYSTEM_ERROR).build();
            }
        }
        return UserQrResponse.builder()
                .qr_code(user.getQr())
                .qrUrl(user.getQrPath())
                .build();
    }

    @Override
    public void updateUserQrs(Long id) {
        getAdmin(id);
        List<User> users = userRepository.getQrUsers();
        users.forEach(e->{
            changeQr(e.getId());
        });
    }

    public Map getEmployeeFromRedis(Long employeeId) {
        Map employeeMap = (Map) redisTemplate.opsForHash().get("employeeId", employeeId.toString());
        if (employeeMap == null || employeeMap.get("role") == null) {
            throw ServiceException.builder().message("Не найден такой сотрудник, " +
                    "переавторизуйтесь или обратитесь к технической поддержке").httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT).build();
        }
        return employeeMap;
    }

    @Override
    @Transactional
    public void techWorkChange(Long employeeId, Boolean status) {
        Map employeeMap = getEmployeeFromRedis(employeeId);
        userRepository.updateTechWorkStatus(status);
    }

    @Transactional
    @Override
    public ResponseEntity<?> pushStatusChange(Long userId) {
        Map employeeMap = (Map) redisTemplate.opsForHash().get("userId", userId.toString());
        if (employeeMap == null) {
            throw ServiceException.builder().message("Не найден такой пользователь, " +
                    "переавторизуйтесь или обратитесь к технической поддержке").httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT).build();
        }
        String message = "";
        boolean status = false;
        Optional<User> userOptional = userRepository.findFirstById(userId);
        if (userOptional.isPresent() && userOptional.get().getPushable()) {
            message = "nonpushable";
        } else {
            message = "pushable";
            status = true;
        }
        userRepository.updatePushStatus(status, userId);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }


    @Override
    public void banUserFromAdmin(Long employeeId, Long userId, Integer ban) {
        Map employeeMap = getEmployeeFromRedis(employeeId);
        Optional<User> userOptional = userRepository.findFirstById(userId);
        if (!userOptional.isPresent()) {
            throw ServiceException.builder().errorCode(ErrorCode.INVALID_ARGUMENT)
                    .httpStatus(HttpStatus.BAD_REQUEST).message("Пользователь не найден")
                    .build();
        }
        User user = userOptional.get();
        if (ban.equals(1)) {
            user.setBanned(true);
            userRepository.save(user);
        } else if (ban.equals(0)) {
            user.setBanned(false);
            userRepository.save(user);
        } else {
            throw ServiceException.builder().errorCode(ErrorCode.INVALID_ARGUMENT)
                    .httpStatus(HttpStatus.BAD_REQUEST).message("Заблокирован может быть 1 или 0")
                    .build();
        }
    }

    @Override
    public Integer getTotalUsersCount() {
        return userRepository.getCountOfUsers();
    }

    @Override
    public Integer getCountOfUsersInAMonth() {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = 1;

        c.set(year, month, day, 0, 0, 0);
        Date currentMonth = c.getTime();
        return userRepository.getCountOfUsersInAMonth(currentMonth);
    }

    @Override
    public void changeUserPasswordFromAdmin(Long employeeId, Long userId, String password) {
        Map employeeMap = (Map) redisTemplate.opsForHash().get("employeeId", employeeId.toString());
        if (employeeMap == null) {
            throw ServiceException.builder().message("Не найден такой пользователь, " +
                    "переавторизуйтесь или обратитесь к технической поддержке").httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT).build();
        }
        Optional<User> userOptional = userRepository.findFirstById(userId);
        if (!userOptional.isPresent()) {
            throw ServiceException.builder().errorCode(ErrorCode.INVALID_ARGUMENT)
                    .httpStatus(HttpStatus.BAD_REQUEST).message("Пользователь не найден")
                    .build();
        }
        if (password.equals("")) {
            throw ServiceException.builder().message("Введите пароль пожалуйста").httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT).build();
        }
        System.out.println("NEw password:" + password);
        User user = userOptional.get();
        user.setPassword(bCryptPasswordEncoder.encode(password));
        System.out.println("NEw User:" + user);

        userRepository.save(user);
    }

    @Override
    public UserResponse loginUser(UserLoginRequest userLoginRequest) {

        System.out.println("USER LOGIN REQUEST" + userLoginRequest);
        Optional<User> userOptional = userRepository.findByPhone(userLoginRequest.getPhone());
        if (!userOptional.isPresent()) {
            throw ServiceException.builder().errorCode(ErrorCode.INVALID_ARGUMENT)
                    .httpStatus(HttpStatus.NOT_FOUND).message("Пользователь не найден")
                    .build();
        }
        User user = userOptional.get();
        System.out.println("HELLO FIRST" + redisTemplate.opsForHash().get("userId", user.getId().toString()));

        redisTemplate.opsForHash().delete("userId", user.getId().toString());
        System.out.println("HELLO SECOND" + redisTemplate.opsForHash().get("userId", user.getId().toString()));

        List<UserTag> userTags = userTagRepository.findAllByDeletedAtIsNullAndUser_Id(user.getId());
        List<TagResponse> tagResponses = new ArrayList<>();
        userTags.forEach(element -> {
            TagResponse tagResponse = TagResponse.builder()
                    .id(element.getId())
                    .name(element.getTag().getName())
                    .noTransactionBlocking(element.getTag().getNoTransactionBlocking())
                    .build();
            tagResponses.add(tagResponse);
        });
        if (!bCryptPasswordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())) {
            throw ServiceException.builder().errorCode(ErrorCode.INVALID_ARGUMENT)
                    .httpStatus(HttpStatus.UNAUTHORIZED).message("Неверный пароль")
                    .build();
        }
        user.setLastLogin(new Date());
        user.setPincode(false);
        UserAttempt userAttempt = userAttemptServiceV1.getUserAttemptByUserId(user);
        userAttempt.setSmsAttempts(0);
        userAttemptServiceV1.save(userAttempt);
        if (userLoginRequest.getPlatform() != null) {
            user.setPlatform(userLoginRequest.getPlatform());
        }
        if (userLoginRequest.getDeviceToken() != null) {
//            List<User> users = userRepository.findAllByDeviceTokenAndDeletedAtIsNull(userLoginRequest.getDeviceToken());
//            users.forEach(e->{
//                e.setDeviceToken("");
//            });
            user.setDeviceToken(userLoginRequest.getDeviceToken());
        }

        if (userLoginRequest.getVersion() != null) {
            user.setMobileVersion(userLoginRequest.getVersion());
        }
        userRepository.save(user);
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .name(user.getName())
                .techWork(user.getTechWork())
                .surname(user.getSurname())
                .cityId(user.getCityId())
                .email(user.getEmail())
                .pincode(user.getPincode())
                .gender(user.getGender())
                .platform(user.getPlatform())
                .birthDate(user.getBirthDate())
                .tags(tagResponses)
                .build();
        Map ruleHash = new ObjectMapper()
                .convertValue(userResponse, Map.class);
        redisTemplate.opsForHash().put("userPhone", user.getPhone(), ruleHash);
        redisTemplate.opsForHash().put("userId", user.getId().toString(), ruleHash);
        System.out.println("HELLO AFTER" + redisTemplate.opsForHash().get("userId", user.getId().toString()));
        return userResponse;
    }

    @Override
    public void checkUserLogin(UserLoginRequest userLoginRequest) {
        Optional<User> userOptional = userRepository.findByPhone(userLoginRequest.getPhone());
        if (!userOptional.isPresent()) {
            throw ServiceException.builder().errorCode(ErrorCode.INVALID_ARGUMENT)
                    .httpStatus(HttpStatus.NOT_FOUND).message("Пользователь не найден")
                    .build();
        }
    }

    @Override
    public void changePinStatus(Long userId) {
        Optional<User> userOptional = userRepository.findFirstById(userId);
        if (!userOptional.isPresent()) {
            throw ServiceException.builder().errorCode(ErrorCode.INVALID_ARGUMENT)
                    .httpStatus(HttpStatus.NOT_FOUND).message("Пользователь не найден")
                    .build();
        }
        User user = userOptional.get();
        if (user.getPincode() == null || !user.getPincode()) {
            user.setPincode(true);
            userRepository.save(user);
        } else {
            user.setPincode(false);
            userRepository.save(user);
        }
    }

    @Override
    public void sendKuke() {
//        List<Long> userIds = userRepository.getUsersForKuka();
//        notificate.notificate(userIds);
    }

    @Override
    public List<User> getChatUsers() {
        return userRepository.getChatUsers();
    }

    @Override
    public List<UserIdsResponse> getAllIdsTest() {
        List<User> users = userRepository.findAll();
        List<UserIdsResponse> userIdsResponses = new ArrayList<>();
        users.forEach(user -> {
            userIdsResponses.add(UserIdsResponse.builder().user_id(user.getId().toString()).build());
        });
        return userIdsResponses;
    }

    @Override
    public CinemaxUserRegisteredResponse registerCinemaxUsers(String phone, String email, Integer cityId) {
        Optional<User> userOptional = userRepository.findByPhone(phone);
        User user = new User();
        String newPassword = Integer.toString(ThreadLocalRandom.current().nextInt(100000, 1000000));
        if (!userOptional.isPresent()) {
            user.setPhone(phone);
            user.setEmail(email);
            user.setPasswordTemplate(newPassword);
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            user.setName("Имя");
            user.setSurname("Фамилия");
            user.setBanned(false);
            user.setComment("Пользователь с cdm.kz, пароль: " + newPassword);
            user.setPlatform("web");
            user.setLanguage("Русский");
            user.setLastLogin(new Date());
            user.setPincode(false);
            user.setPushable(true);
            user.setTechWork(false);
            user.setCityId(cityId);
            user.setIsConfirmed(false);
            System.out.println("USER CINEMAX" + user);
            userRepository.save(user);
        } else {
            user = userOptional.get();
        }
        return CinemaxUserRegisteredResponse.builder()
                .userId(user.getId())
                .phone(user.getPhone())
                .build();
    }

    @Override
    public void confirmCinemaxUser(Long userId, Integer status) {
        if (status.equals(1)) {
            Optional<User> user = userRepository.findFirstById(userId);
            if (user.isPresent()) {
                if (!user.get().getIsConfirmed()) {
                    UserToSmsResponse userToSmsResponse = new UserToSmsResponse();
                    userToSmsResponse.setUserId(user.get().getId());
                    userToSmsResponse.setUserPhone(user.get().getPhone());
                    userToSmsResponse.setSmsConfirmCode(user.get().getPasswordTemplate());
                    System.out.println("SMS RESPONSE" + userToSmsResponse);
                    smsSender.SendSmsToUser(userToSmsResponse, "register-cinemax");
                    user.get().setIsConfirmed(true);
                    userRepository.save(user.get());
                }
            }
        }

    }

    @Override
    public String getUserQrById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            System.out.println("USER QR" + userOptional.get().getQr());
            return userOptional.get().getQr();
        } else {
            return "NOT FOUND";
        }
    }

    public void getAdmin(Long userId) {

        if (userId == null) {
            throw ServiceException.builder().message("Admin not found").httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT).build();
        }

        Map userMap = (Map) redisTemplate.opsForHash().get("employeeId", userId.toString());
        if (userMap == null || !userMap.get("role").toString().equals("ROLE_ADMIN") || !userMap.get("role").toString().equals("ROLE_SUPER_ADMIN")) {
            throw ServiceException.builder().message("Не найден такой админ, " +
                    "переавторизуйтесь или обратитесь к технической поддержке").httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT).build();
        }

    }

}
