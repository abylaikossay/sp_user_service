package kz.smart.plaza.users.services.v1.impl;

import kz.smart.plaza.users.models.entities.ButtonInfo;
import kz.smart.plaza.users.models.entities.User;
import kz.smart.plaza.users.models.entities.UserClick;
import kz.smart.plaza.users.models.errors.ErrorCode;
import kz.smart.plaza.users.models.errors.ServiceException;
import kz.smart.plaza.users.models.responses.MainPageResponse;
import kz.smart.plaza.users.repositories.ButtonInfoRepository;
import kz.smart.plaza.users.repositories.UserAttemptsRepository;
import kz.smart.plaza.users.repositories.UserClickRepository;
import kz.smart.plaza.users.services.v1.FileServiceV1;
import kz.smart.plaza.users.services.v1.UserClickServiceV1;
import kz.smart.plaza.users.services.v1.UserServiceV1;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class UserClickServiceImplV1 implements UserClickServiceV1 {

    private UserClickRepository userClickRepository;
    private UserServiceV1 userServiceV1;
    private FileServiceV1 fileServiceV1;
    private ButtonInfoRepository buttonInfoRepository;


    @Override
    public void updateUserClick(Long userId, String type) {
        User user = userServiceV1.getUser(userId);
        Optional<UserClick> userClickOptional = userClickRepository.findFirstByUser_Id(userId);
        UserClick userClick;
        if (userClickOptional.isPresent()) {
            userClick = userClickOptional.get();
        } else {
            userClick = new UserClick();
            userClick.setUser(user);
            userClick.setCinemax(0);
            userClick.setBottomBanner(0);
            userClick.setEco(0);
            userClick.setNews(0);
            userClick.setMyQr(0);
            userClick.setParking(0);
            userClick.setOnlineMarket(0);
            userClick.setPartners(0);
            userClick.setPromo(0);
            userClick.setStartTime(0);
            userClick.setUploadCheque(0);
            userClick.setTopBanner(0);
            userClick.setTrcMap(0);
        }
        switch (type) {
            case "cinemax":
                userClick.setCinemax(userClick.getCinemax() + 1);
                break;
            case "bottomBanner":
                userClick.setBottomBanner(userClick.getBottomBanner() + 1);
                break;
            case "eco":
                userClick.setEco(userClick.getEco() + 1);
                break;
            case "news":
                userClick.setNews(userClick.getNews() + 1);
                break;
            case "myQr":
                userClick.setMyQr(userClick.getMyQr() + 1);
                break;
            case "parking":
                userClick.setParking(userClick.getParking() + 1);
                break;
            case "onlineMarket":
                userClick.setOnlineMarket(userClick.getOnlineMarket() + 1);
                break;
            case "partners":
                userClick.setPartners(userClick.getPartners() + 1);
                break;
            case "promo":
                userClick.setPromo(userClick.getPromo() + 1);
                break;
            case "startTime":
                userClick.setStartTime(userClick.getStartTime() + 1);
                break;
            case "uploadCheque":
                userClick.setUploadCheque(userClick.getUploadCheque() + 1);
                break;
            case "topBanner":
                userClick.setTopBanner(userClick.getTopBanner() + 1);
                break;
            case "trcMap":
                userClick.setTrcMap(userClick.getTrcMap() + 1);
                break;
        }
        userClickRepository.save(userClick);
    }

    @Override
    public UserClick getUserClicks(Long userId) {
//        gggg
        User user = userServiceV1.getUser(userId);
        Optional<UserClick> userClickOptional = userClickRepository.findFirstByUser_Id(user.getId());
        UserClick userClick = null;
        if (userClickOptional.isPresent()) {
            userClick = userClickOptional.get();
        } else {
            throw ServiceException.builder()
                    .message("По пользователю нет данных!")
                    .errorCode(ErrorCode.SYSTEM_ERROR)
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .build();
        }
        return userClick;
    }

    @Override
    public void addButtonInfo(MultipartFile buttonImg, Long buttonId, String buttonName, Integer platform, Boolean active, String lang, Long userId) {
        String buttonUrl = fileServiceV1.storeFile(buttonImg, "button");
        ButtonInfo buttonInfo = ButtonInfo.builder()
                .buttonId(buttonId)
                .buttonImg(buttonUrl)
                .buttonName(buttonName)
                .platform(platform)
                .active(active)
                .language(lang)
                .build();
        buttonInfoRepository.save(buttonInfo);

    }

    @Override
    public List<MainPageResponse> getMainPage(Integer platform) {
        List<ButtonInfo> buttonInfos = buttonInfoRepository.findAllByPlatformAndActiveAndDeletedAtIsNull(platform, true);

        return buttonInfos.stream().map(e -> MainPageResponse.builder()
                .buttonId(e.getButtonId())
                .buttonName(e.getButtonName())
                .buttonImg(e.getButtonImg())
                .platform(e.getPlatform()).build()).collect(Collectors.toList());
    }

    @Override
    public MainPageResponse getButtonInfo(Integer platform, Long buttonId, Long userId) {
        User user = userServiceV1.getUser(userId);

        ButtonInfo buttonInfo = buttonInfoRepository.findFirstByButtonIdAndPlatformAndActiveAndLanguageAndDeletedAtIsNull(buttonId, platform,
                true,user.getLanguage() != null ? user.getLanguage() : "Русский");

        if (buttonInfo == null) {
            throw ServiceException.builder()
                    .errorCode(ErrorCode.SYSTEM_ERROR)
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .message("Данные кнопки не найдены!")
                    .build();
        }

        return MainPageResponse.builder()
                .buttonId(buttonInfo.getButtonId())
                .buttonImg(buttonInfo.getButtonImg())
                .buttonName(buttonInfo.getButtonName())
                .platform(buttonInfo.getPlatform())
                .active(buttonInfo.getActive())
                .build();
    }
}
