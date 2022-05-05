package kz.smart.plaza.users.repositories;

import kz.smart.plaza.users.models.entities.ButtonInfo;
import kz.smart.plaza.users.models.entities.UserClick;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public interface ButtonInfoRepository extends JpaRepository<ButtonInfo, Long> {

    List<ButtonInfo> findAllByPlatformAndActiveAndDeletedAtIsNull(Integer platform, Boolean active);

    ButtonInfo findFirstByButtonIdAndPlatformAndActiveAndDeletedAtIsNull(Long buttonId, Integer platform, Boolean active);

    ButtonInfo findFirstByButtonIdAndPlatformAndActiveAndLanguageAndDeletedAtIsNull(Long buttonId, Integer platform, Boolean active, String lang);
}
