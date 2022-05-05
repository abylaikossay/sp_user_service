package kz.smart.plaza.users.services.v1.impl;


import kz.smart.plaza.users.models.entities.User;
import kz.smart.plaza.users.models.entities.UserPromo;
import kz.smart.plaza.users.models.errors.ErrorCode;
import kz.smart.plaza.users.models.errors.ServiceException;
import kz.smart.plaza.users.models.requests.UserPromoRequest;
import kz.smart.plaza.users.models.responses.PromoResponse;
import kz.smart.plaza.users.repositories.UserPromoRepository;
import kz.smart.plaza.users.repositories.UserRepository;
import kz.smart.plaza.users.services.v1.UserPromoServiceV1;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
@Transactional
public class UserPromoServiceImplV1 implements UserPromoServiceV1 {

    private UserPromoRepository userPromoRepository;
    private UserRepository userRepository;


    @Override
    public void addPromoToUser(UserPromoRequest userPromoRequest) {
        Optional<User> userOptional = userRepository.findFirstById(userPromoRequest.getUserId());
        if (!userOptional.isPresent()) {
            throw ServiceException.builder()
                    .errorCode(ErrorCode.INVALID_EMPLOYEE)
                    .httpStatus(HttpStatus.CONFLICT)
                    .message("Ошибка! Пользователь не найден")
                    .build();
        }
        User user = userOptional.get();
        UserPromo userPromo = new UserPromo();
        userPromo.setBrandIdReward(userPromoRequest.getBrandIdReward());
        userPromo.setRewardAmount(userPromoRequest.getRewardAmount());
        userPromo.setPromoCode(userPromoRequest.getPromoCode());
        userPromo.setPromoId(userPromoRequest.getPromoId());
        userPromo.setActive(userPromoRequest.getActive());
        userPromo.setRewardName(userPromoRequest.getRewardName());
        userPromo.setRewardId(userPromoRequest.getRewardId());
        userPromo.setUser(user);
        userPromoRepository.save(userPromo);
    }

    @Override
    public List<PromoResponse> getPromoByUserId(Long userId) {
        List<PromoResponse> promoResponses = new ArrayList<>();
        List<UserPromo> userPromos = userPromoRepository.findAllByUserId(userId);
        userPromos.forEach( userPromo -> {
            PromoResponse promoResponse = PromoResponse.builder()
                    .brandId(userPromo.getBrandIdReward())
                    .promoCode(userPromo.getPromoCode())
                    .rewardAmount(userPromo.getRewardAmount())
                    .rewardName(userPromo.getRewardName())
                    .userId(userPromo.getPromoId())
                    .userName(userPromo.getUser().getSurname()  + " " + userPromo.getUser().getName())
                    .build();
            promoResponses.add(promoResponse);
        });
        return promoResponses;
    }
}
