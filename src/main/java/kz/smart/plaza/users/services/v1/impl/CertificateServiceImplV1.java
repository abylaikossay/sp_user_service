package kz.smart.plaza.users.services.v1.impl;

import com.google.zxing.WriterException;
import kz.smart.plaza.users.methods.*;
import kz.smart.plaza.users.models.entities.*;
import kz.smart.plaza.users.models.errors.ErrorCode;
import kz.smart.plaza.users.models.errors.ServiceException;
import kz.smart.plaza.users.models.requests.*;
import kz.smart.plaza.users.models.responses.UserFeignResponse;
import kz.smart.plaza.users.models.responses.UserResponse;
import kz.smart.plaza.users.models.responses.certificate.BrandCertificateResponse;
import kz.smart.plaza.users.models.responses.certificate.CertificateCodeResponse;
import kz.smart.plaza.users.models.responses.certificate.CertificateResponse;
import kz.smart.plaza.users.models.responses.certificate.UserCertificateResponse;
import kz.smart.plaza.users.models.responses.notifications.GiftCertificateResponse;
import kz.smart.plaza.users.models.responses.notifications.NotificateCertificateResponse;
import kz.smart.plaza.users.models.responses.success.SuccessResponse;
import kz.smart.plaza.users.repositories.CertificateCodeRepository;
import kz.smart.plaza.users.repositories.CertificateRepository;
import kz.smart.plaza.users.repositories.UserCertificateRepository;
import kz.smart.plaza.users.services.v1.CertificateServiceV1;
import kz.smart.plaza.users.services.v1.FileServiceV1;
import kz.smart.plaza.users.services.v1.UserServiceV1;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@AllArgsConstructor
@Transactional
public class CertificateServiceImplV1 implements CertificateServiceV1 {
    private FileServiceV1 fileServiceV1;
    private CertificateRepository certificateRepository;
    private CertificateCodeRepository certificateCodeRepository;
    private UserCertificateRepository userCertificateRepository;
    private RedisTemplate redisTemplate;
    private UserServiceV1 userServiceV1;
    private QRGenerator qrGenerator;
    private Pagination pagination;
    private KafkaProducerMethods kafkaProducer;
    private Notificate notificate;
    private CallApi callApi;


    @Override
    public SuccessResponse addNewCertificate(CertificateRequest certificateRequest, Long employeeId) {
        this.getEmployeeFromFeign(employeeId);
        this.checkBrandNominal(certificateRequest.getBrandId(), certificateRequest.getSum());
        Certificate certificate = new Certificate();
        if (certificateRequest.getBackgroundImg() != null) {
            String file = fileServiceV1.storeFile(certificateRequest.getBackgroundImg(), "certificate");
            certificate.setBackgroundImg(file);
        }
        if (certificateRequest.getQrBgImg() != null) {
            String file = fileServiceV1.storeFile(certificateRequest.getQrBgImg(), "qr_certificate");
            certificate.setQrBgImg(file);
        }
        certificate.setSum(certificateRequest.getSum());
        certificate.setBrandId(certificateRequest.getBrandId());
        certificate.setFromDate(certificateRequest.getFromDate());
        certificate.setLogoLocation(certificateRequest.getLogoLocation());
        certificate.setPriceLocation(certificateRequest.getPriceLocation());
        certificate.setHasLimit(certificateRequest.getHasLimit());
        certificate.setToDate(certificateRequest.getToDate());
        certificateRepository.save(certificate);
        return SuccessResponse.builder().message("Сертификат успешно  добавлен!").build();
    }

    @Override
    public SuccessResponse updateCertificate(CertificateRequest certificateRequest, Long id, Long employeeId) {
        this.getEmployeeFromFeign(employeeId);
        Certificate certificate = certificateRepository.findFirstByIdAndDeletedAtIsNull(id);
        if (certificate == null) {
            throw ServiceException.builder().message("Сертификат не найден").httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT).build();
        }
        if (!certificate.getSum().equals(certificateRequest.getSum())) {
            this.checkBrandNominal(certificateRequest.getBrandId(), certificateRequest.getSum());
        }
        if (certificateRequest.getBackgroundImg() != null) {
            if (certificate.getBackgroundImg() != null) {
                fileServiceV1.delete(certificate.getBackgroundImg(), "certificate");
            }
            String file = fileServiceV1.storeFile(certificateRequest.getBackgroundImg(), "certificate");
            certificate.setBackgroundImg(file);
        }
        if (certificateRequest.getQrBgImg() != null) {
            if (certificate.getQrBgImg() != null) {
                fileServiceV1.delete(certificate.getQrBgImg(), "qr_certificate");
            }
            String file = fileServiceV1.storeFile(certificateRequest.getQrBgImg(), "qr_certificate");
            certificate.setQrBgImg(file);
        }
        certificate.setSum(certificateRequest.getSum());
        certificate.setBrandId(certificateRequest.getBrandId());
        certificate.setFromDate(certificateRequest.getFromDate());
        certificate.setLogoLocation(certificateRequest.getLogoLocation());
        certificate.setPriceLocation(certificateRequest.getPriceLocation());
        certificate.setToDate(certificateRequest.getToDate());
        if (certificateRequest.getHasLimit() != null) {
            certificate.setHasLimit(certificateRequest.getHasLimit());
        }
        certificateRepository.save(certificate);
        return SuccessResponse.builder().message("Сертификат успешно  обновлен!").build();
    }

    @Override
    public List<Certificate> getCertificatesByBrand(Long brandId) {
        List<Certificate> certificates = certificateRepository.findAllByDeletedAtIsNullAndBrandIdOrderBySumAsc(brandId);
        List<Certificate> availableCertificates = new ArrayList<>();
        certificates.forEach(certificate -> {
            if (certificate.getHasLimit().equals(true)) {
                List<CertificateCode> certificateCodes = certificateCodeRepository.findAllByCertificate_IdAndIsActivatedIsFalse(certificate.getId());
                if (!certificateCodes.isEmpty()) {
                    availableCertificates.add(certificate);
                }
            } else {
                availableCertificates.add(certificate);
            }
        });
        return availableCertificates;
    }

    @Override
    public List<UserCertificateResponse> getUserCertificates(Long userId, Integer status) {
        User user = userServiceV1.getUser(userId);
        List<Integer> statuses = new ArrayList<>();
        if (status.equals(0) || status.equals(2)) {
            statuses.add(0);
            statuses.add(2);
        } else if (status.equals(3)) {
            statuses.add(3);
        } else {
            statuses.add(1);
        }
        List<UserCertificate> userCertificates = userCertificateRepository.findAllByUser_IdAndStatusInOrderByIdDesc(userId, statuses);
        List<UserCertificateResponse> userCertificateResponses = new ArrayList<>();
        userCertificates.forEach(userCertificate -> {
            Certificate certificate = userCertificate.getCertificateCode().getCertificate();
            BrandPartnerRequest brandPartnerRequest = this.getBrandById(certificate.getBrandId());
            UserCertificateResponse userCertificateResponse = UserCertificateResponse.builder()
                    .certificateCodeId(userCertificate.getCertificateCode().getId())
                    .backgroundImg(certificate.getBackgroundImg())
                    .qrBhImg(certificate.getQrBgImg())
                    .brandName(brandPartnerRequest.getBrandName())
                    .brandLogo(brandPartnerRequest.getBrandLogo())
                    .brandId(certificate.getBrandId())
                    .logoLocation(certificate.getLogoLocation())
                    .priceLocation(certificate.getPriceLocation())
                    .sum(certificate.getSum())
                    .fromDate(certificate.getFromDate())
                    .toDate(certificate.getToDate())
                    .status(userCertificate.getStatus())
                    .buyTime(userCertificate.getCreatedAt())
                    .build();
            userCertificateResponses.add(userCertificateResponse);
        });
        return userCertificateResponses;
    }

    @Override
    public CertificateCodeResponse getCertificateById(Long id, Long userId) {
        User user = userServiceV1.getUser(userId);
        CertificateCode certificateCode = certificateCodeRepository.findById(id).orElseThrow(() -> ServiceException.builder()
                .errorCode(ErrorCode.INVALID_ARGUMENT)
                .httpStatus(HttpStatus.NOT_FOUND)
                .message("Сертификат не найден")
                .build());
        Optional<UserCertificate> userCertificateOptional = userCertificateRepository.findFirstByCertificateCode_IdAndStatusIsNot(certificateCode.getId(), 3);
        if (!userCertificateOptional.isPresent()) {
            throw ServiceException.builder()
                    .errorCode(ErrorCode.INVALID_ARGUMENT)
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .message("Сертификат не найден")
                    .build();
        }
        UserCertificate userCertificate = userCertificateOptional.get();
        CertificateResponse certificateResponse = this.getCertificateByCertificateId(certificateCode.getCertificate().getId());
        return CertificateCodeResponse.builder()
                .id(certificateCode.getId())
                .createdAt(certificateCode.getCreatedAt())
                .updatedAt(certificateCode.getUpdatedAt())
                .code(certificateCode.getCode())
                .zipCode(certificateCode.getZipCode())
                .status(userCertificate.getStatus())
                .barcodeUrl(certificateCode.getBarcodeUrl())
                .buyTime(userCertificate.getCreatedAt())
                .certificate(certificateResponse)
                .fromBrand(certificateCode.getFromBrand())
                .qrString(certificateCode.getQrString())
                .qrUrl(certificateCode.getQrUrl())
                .isActivated(certificateCode.getIsActivated())
                .build();

    }

    @Override
    public CertificateCodeResponse getCertificateByCode(String qr) {
        Optional<CertificateCode> certificateCode = certificateCodeRepository.findFirstByQrString(qr);
        if (!certificateCode.isPresent()) {
            throw ServiceException.builder()
                    .errorCode(ErrorCode.INVALID_CERTIFICATE)
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("Сертификат не найден")
                    .build();
        }
        Certificate certificate = certificateRepository.findFirstByIdAndDeletedAtIsNull(certificateCode.get().getCertificate().getId());
        if (certificate == null) {
            throw ServiceException.builder()
                    .errorCode(ErrorCode.INVALID_CERTIFICATE)
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("Сертификат не найден")
                    .build();
        }
        Optional<UserCertificate> userCertificateOptional = userCertificateRepository.findFirstByCertificateCode_IdAndStatusIsNot(certificateCode.get().getId(), 3);
        if (!userCertificateOptional.isPresent()) {
            throw ServiceException.builder()
                    .errorCode(ErrorCode.INVALID_CERTIFICATE)
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("Сертификат не принадлежит никому")
                    .build();
        }
        CertificateResponse certificateResponse = this.getCertificateByCertificateId(certificate.getId());
        UserFeignResponse userFeignResponse = userServiceV1.findById(userCertificateOptional.get().getUser().getId());
        return CertificateCodeResponse.builder()
                .id(certificateCode.get().getId())
                .updatedAt(certificateCode.get().getUpdatedAt())
                .createdAt(certificateCode.get().getCreatedAt())
                .code(certificateCode.get().getCode())
                .qrString(certificateCode.get().getQrString())
                .qrUrl(certificateCode.get().getQrUrl())
                .zipCode(certificateCode.get().getZipCode())
                .barcodeUrl(certificateCode.get().getBarcodeUrl())
                .certificate(certificateResponse)
                .user(userFeignResponse)
                .status(userCertificateOptional.get().getStatus())
                .build();
    }


    @Override
    public SuccessResponse buyCertificate(BuyCertificateRequest buyCertificateRequest, Long userId) {
        System.out.println("USER ID " + userId + " buyCertificateRequest " + buyCertificateRequest);
        User user = userServiceV1.getUser(userId);
        Certificate certificate = certificateRepository.findFirstByIdAndDeletedAtIsNull(buyCertificateRequest.getCertificateId());
        if (certificate == null) {
            throw ServiceException.builder()
                    .errorCode(ErrorCode.INVALID_ARGUMENT)
                    .httpStatus(HttpStatus.CONFLICT)
                    .message("Сертификат с таким id не существует")
                    .build();
        }
        List<CertificateCode> certificateCodes = certificateCodeRepository.findAllByCertificate_IdAndIsActivatedIsFalse(buyCertificateRequest.getCertificateId());
        CertificateCode certificateCode;
        if (certificateCodes.isEmpty()) {
            if (certificate.getHasLimit()) {
                throw ServiceException.builder()
                        .errorCode(ErrorCode.INVALID_ARGUMENT)
                        .httpStatus(HttpStatus.CONFLICT)
                        .message("Сертификаты с данным номиналом не доступны")
                        .build();
            } else {
                certificateCode = new CertificateCode();
                certificateCode.setIsActivated(true);
                certificateCode.setFromBrand(false);
                certificateCode.setCertificate(certificate);
            }
        } else {
            certificateCode = certificateCodes.get(0);
            certificateCode.setIsActivated(true);
            certificateCode.setFromBrand(true);
        }
        this.addQrAndBarcodeToCertificate(certificateCode);
        UserCertificate userCertificate = new UserCertificate();
        userCertificate.setUser(user);
        userCertificate.setCertificateCode(certificateCode);
        userCertificate.setStatus(UserCertificate.ACTIVE);
        addExpirationToUserCertificate(certificate.getExpirationMonth(), userCertificate);
        List<OrderProductRequest> orderProductRequests = new ArrayList<>();
        orderProductRequests.add(OrderProductRequest.builder()
                .amount(1)
                .categoryId(15L)
                .price(certificate.getSum())
                .productName("Сертификат " + certificateCode.getCode())
                .returnable(true)
                .build());
        CertificateOrderRequest certificateOrderRequest = CertificateOrderRequest.builder()
                .orderProducts(orderProductRequests)
                .bonus(buyCertificateRequest.getBonus())
                .cash(0.0)
                .creditCard(certificate.getSum() - buyCertificateRequest.getBonus())
                .promo(0.0)
                .brandId(certificate.getBrandId())
                .userId(userId)
                .build();
        kafkaProducer.sendOrderCertificate(certificateOrderRequest);
        certificateCodeRepository.save(certificateCode);
        userCertificateRepository.save(userCertificate);

        return SuccessResponse.builder().message("Сертификат успешно приобретен").build();
    }

    void addExpirationToUserCertificate(Integer expirationMonth, UserCertificate userCertificate) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date currentDay = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDay);
            calendar.add(Calendar.MONTH, expirationMonth);
            Date expirationDate = simpleDateFormat.parse(simpleDateFormat.format(calendar.getTime()));
            userCertificate.setToDate(expirationDate);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<BrandCertificateResponse> getCertificatesOfBrand(Long employeeId) {
        EmployeeRequest employeeRequest = this.getEmployeeFromFeign(employeeId);
        Long brandId = employeeRequest.getBrandId();
        List<Certificate> certificates = certificateRepository.findAllByDeletedAtIsNullAndBrandIdOrderBySumAsc(brandId);
        List<BrandCertificateResponse> brandCertificateResponses = new ArrayList<>();
        certificates.forEach(certificate -> {
            List<CertificateCode> certificateCodes = certificateCodeRepository.findAllByCertificate_IdAndFromBrandIsTrue(certificate.getId());
            certificateCodes.forEach(certificateCode -> {
                BrandCertificateResponse brandCertificateResponse = BrandCertificateResponse.builder()
                        .brandId(certificate.getBrandId())
                        .brandLogo("")
                        .brandName("")
                        .certificateCodeId(certificateCode.getId())
                        .code(certificateCode.getCode())
                        .zipCode(certificateCode.getZipCode())
                        .isActivated(certificateCode.getIsActivated())
                        .sum(certificate.getSum())
                        .build();
                brandCertificateResponses.add(brandCertificateResponse);
            });
        });
        return brandCertificateResponses;
    }

    private BrandPartnerRequest getBrandById(Long brandId) {
        return callApi.getBrandAndPartnerByBrandId(brandId);
    }

    @Override
    public void activateCertificate(String qr, Long employeeId) {
        EmployeeRequest employeeRequest = this.getEmployeeFromFeign(employeeId);
        Optional<CertificateCode> certificateCodeOptional = certificateCodeRepository.findFirstByQrString(qr);
        if (!certificateCodeOptional.isPresent()) {
            throw ServiceException.builder().message("Не верный qr код!").httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT).build();
        }
        Certificate certificate = certificateRepository.findFirstByIdAndDeletedAtIsNull(certificateCodeOptional.get().getCertificate().getId());
        Long brandId = employeeRequest.getBrandId();
//        if (!certificate.getBrandId().equals(brandId)) {
//            throw ServiceException.builder().message("Вы не можете деактивировать сертификат!").httpStatus(HttpStatus.BAD_REQUEST)
//                    .errorCode(ErrorCode.INVALID_ARGUMENT).build();
//        }
        Optional<UserCertificate> userCertificateOptional = userCertificateRepository.findFirstByCertificateCode_IdAndStatusIsNot(certificateCodeOptional.get().getId(), 3);
        if (!userCertificateOptional.isPresent()) {
            throw ServiceException.builder().message("Сертификат не найден!").httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT).build();
        }
        if (userCertificateOptional.get().getStatus().equals(UserCertificate.USED)) {
            throw ServiceException.builder().message("Сертификат уже активирован!").httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT).build();
        } else if (userCertificateOptional.get().getStatus().equals(UserCertificate.EXPIRED)) {
            throw ServiceException.builder().message("Срок действия сертификата истек!").httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT).build();
        }
        userCertificateOptional.get().setStatus(UserCertificate.USED);
        userCertificateRepository.save(userCertificateOptional.get());
        BrandPartnerRequest brandPartnerRequest = this.getBrandById(certificate.getBrandId());
        NotificateCertificateResponse notificateCertificateResponse = NotificateCertificateResponse.builder()
                .externalId(userCertificateOptional.get().getId())
                .brandId(certificate.getBrandId())
                .brandUrl(brandPartnerRequest.getBrandLogo())
                .brandName(brandPartnerRequest.getBrandName())
                .userId(userCertificateOptional.get().getUser().getId())
                .sum(certificate.getSum())
                .build();
        notificate.notificateCertificate(notificateCertificateResponse);
    }

    @Override
    public void giftCertificate(Long userId, Long id, String phone) {
        User user = userServiceV1.getUser(userId);
        Optional<String> phoneOptional = Optional.ofNullable(phone);
        UserResponse userResponse = userServiceV1.findByQrOrPhone(phoneOptional, Optional.empty());
        System.out.println("SECOND USER " + userResponse);
        Optional<CertificateCode> certificateCodeOptional = certificateCodeRepository.findById(id);
        if (!certificateCodeOptional.isPresent()) {
            throw ServiceException.builder().message("Сертификат не найден").httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT).build();
        }
        Optional<UserCertificate> userCertificateOptional = userCertificateRepository.findFirstByCertificateCode_IdAndStatusIsNot(id, 3);
        if (!userCertificateOptional.isPresent()) {
            throw ServiceException.builder().message("Сертификат не принадлежит вам").httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT).build();
        }
        UserCertificate userCertificate = userCertificateOptional.get();
        if (!user.getId().equals(userCertificate.getUser().getId())) {
            throw ServiceException.builder().message("Сертификат не принадлежит вам").httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT).build();
        }
        if (userResponse.getId().equals(user.getId())) {
            throw ServiceException.builder().message("Нельзя подарить сертификат самому себе").httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT).build();
        }
        if (!userCertificate.getStatus().equals(1)) {
            throw ServiceException.builder().message("Данный сертификат нельзя подарить").httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT).build();
        }
        userCertificate.setStatus(UserCertificate.USED);
        userCertificateRepository.save(userCertificate);
        UserCertificate userCertificate2 = new UserCertificate();
        User user2 = userServiceV1.getUser(userResponse.getId());
        userCertificate2.setUser(user2);
        userCertificate2.setCertificateCode(certificateCodeOptional.get());
        userCertificate2.setStatus(UserCertificate.ACTIVE);
        userCertificate2.setGiftedUserId(user.getId());
        userCertificateRepository.save(userCertificate2);
        BrandPartnerRequest brandPartnerRequest = this.getBrandById(certificateCodeOptional.get().getCertificate().getBrandId());
        GiftCertificateResponse giftCertificateResponse = GiftCertificateResponse.builder()
                .externalId(userCertificateOptional.get().getId())
                .brandId(brandPartnerRequest.getBrandId())
                .brandUrl(brandPartnerRequest.getBrandLogo())
                .brandName(brandPartnerRequest.getBrandName())
                .userId(user.getId())
                .userName(user2.getSurname() + " " + user2.getName())
                .userPhone(user2.getPhone())
                .sum(certificateCodeOptional.get().getCertificate().getSum())
                .build();
        notificate.notificateGiftCertificate(giftCertificateResponse, "gifted");
        GiftCertificateResponse giftCertificateResponse2 = GiftCertificateResponse.builder()
                .externalId(userCertificateOptional.get().getId())
                .brandId(brandPartnerRequest.getBrandId())
                .brandUrl(brandPartnerRequest.getBrandLogo())
                .brandName(brandPartnerRequest.getBrandName())
                .userId(user2.getId())
                .userName(user.getSurname() + " " + user.getName())
                .userPhone(user.getPhone())
                .sum(certificateCodeOptional.get().getCertificate().getSum())
                .build();
        notificate.notificateGiftCertificate(giftCertificateResponse2, "gained");
    }

    @Override
    public void addCodeToCertificate(Long userId, List<CertificateCodeRequest> codes, Long certificateId) {
        this.getEmployeeFromFeign(userId);
        Optional<Certificate> certificateOptional = certificateRepository.findById(certificateId);
        if (!certificateOptional.isPresent()) {
            throw ServiceException.builder().message("Сертификат не найден").httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT).build();
        }
        System.out.println("CODES " + codes);
        codes.forEach(code -> {
//            String barCode12 = code.getBarcodeUrl().substring(0, Math.min(code.getBarcodeUrl().length(), 12));
            CertificateCode certificateCode2 = certificateCodeRepository.findFirstByCode(code.getCode());
            if (certificateCode2 == null) {
                CertificateCode certificateCode = new CertificateCode();
                certificateCode.setCode(code.getCode());
                certificateCode.setBarcodeUrl(code.getBarcodeUrl());
                certificateCode.setZipCode(code.getZipCode());
                certificateCode.setCertificate(certificateOptional.get());
                certificateCode.setFromBrand(true);
                certificateCode.setIsActivated(false);
                certificateCodeRepository.save(certificateCode);
            }
        });
    }

    @Override
    public CertificateResponse getCertificateByCertificateId(Long id) {
        Certificate certificate = certificateRepository.findFirstByIdAndDeletedAtIsNull(id);
        if (certificate == null) {
            throw ServiceException.builder().message("Сертификат не найден").httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT).build();
        }
        return pagination.collectCertificate(certificate);
    }

    @Override
    public SuccessResponse deleteCertificate(Long employeeId, Long id) {
        this.getEmployeeFromFeign(employeeId);
        Certificate certificate = certificateRepository.findFirstByIdAndDeletedAtIsNull(id);
        if (certificate == null) {
            throw ServiceException.builder().message("Сертификат не найден").httpStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(ErrorCode.INVALID_ARGUMENT).build();
        }
        certificate.setDeletedAt(new Date());
        certificateRepository.save(certificate);
        return SuccessResponse.builder().message("Сертификат успешно удален!").build();
    }

    @Override
    public List<CertificateCode> getCertificateCodes(Long employeeId, Long certificateId) {
        this.getEmployeeFromFeign(employeeId);
        return certificateCodeRepository.findAllByCertificate_IdAndFromBrandIsTrue(certificateId);
    }

    @Override
    public SuccessResponse deleteCertificateCode(Long id, Long employeeId) {
        this.getEmployeeFromFeign(employeeId);
        Optional<CertificateCode> certificateCodeOptional = certificateCodeRepository.findById(id);
        if (certificateCodeOptional.isPresent()) {
            CertificateCode certificateCode = certificateCodeOptional.get();
            if (certificateCode.getIsActivated()) {
                throw ServiceException.builder().message("Данный сертификат нельзя удалять").httpStatus(HttpStatus.BAD_REQUEST)
                        .errorCode(ErrorCode.INVALID_ARGUMENT).build();
            }
            certificateCodeRepository.delete(certificateCode);
        }
        return SuccessResponse.builder().message("Код успешно удален!").build();
    }

    private CertificateCode addQrAndBarcodeToCertificate(CertificateCode certificateCode) {
        try {
            qrGenerator.generateCertificateQr(certificateCode);
            certificateCodeRepository.save(certificateCode);
        } catch (WriterException | IOException e) {
            throw ServiceException.builder().message(e.getMessage()).
                    httpStatus(HttpStatus.BAD_REQUEST).errorCode(ErrorCode.INVALID_BRAND).build();
        }
        return certificateCode;
    }


    private EmployeeRequest getEmployeeFromFeign(Long employeeId) {
        EmployeeRequest employeeRequest = callApi.getEmployeeById(employeeId);
        if (employeeRequest == null) {
            throw ServiceException.builder()
                    .errorCode(ErrorCode.INVALID_ARGUMENT)
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .message("Не найден такой сотрудник, " +
                            "переавторизуйтесь или обратитесь к технической поддержке")
                    .build();
        }
        return employeeRequest;
    }

    private void checkBrandNominal(Long brandId, Double sum) {
        Optional<Certificate> certificateOptional = certificateRepository.findFirstByDeletedAtIsNullAndBrandIdAndSum(brandId, sum);
        if (certificateOptional.isPresent()) throw ServiceException.builder()
                .errorCode(ErrorCode.INVALID_ARGUMENT)
                .httpStatus(HttpStatus.CONFLICT)
                .message("Сертификат с таким номиналом уже существует")
                .build();
    }

    @Override
    public void checkUserCertificatesAndUpdateThemByExpiration() {
            List<UserCertificate> userCertificates = userCertificateRepository.findAll();
            Date currentDate = new Date();
            userCertificates.forEach(e -> {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(e.getCreatedAt());
                    calendar.add(Calendar.MONTH, e.getCertificateCode().getCertificate().getExpirationMonth());
                    Date expirationDate = simpleDateFormat.parse(simpleDateFormat.format(calendar.getTime()));
                    if (expirationDate.before(currentDate)) {
                        e.setStatus(2);
                    }
                    e.setToDate(expirationDate);
                }
                catch (Exception el) {
                    el.printStackTrace();
                }
            });
            userCertificateRepository.saveAll(userCertificates);
        }
    }
