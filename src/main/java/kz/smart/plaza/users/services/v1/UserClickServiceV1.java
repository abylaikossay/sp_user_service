package kz.smart.plaza.users.services.v1;


import kz.smart.plaza.users.models.entities.UserClick;
import kz.smart.plaza.users.models.responses.MainPageResponse;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserClickServiceV1 {
    void updateUserClick(Long userId, String type);
    UserClick getUserClicks(Long userId);
    void addButtonInfo(MultipartFile buttonImg, Long buttonId,String buttonName, Integer platform, Boolean active, String lang, Long userId);
    List<MainPageResponse> getMainPage(Integer platform);
    MainPageResponse getButtonInfo(Integer platform, Long buttonId, Long userId);
}
