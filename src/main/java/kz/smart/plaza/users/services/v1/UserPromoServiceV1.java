package kz.smart.plaza.users.services.v1;


import kz.smart.plaza.users.models.requests.UserPromoRequest;
import kz.smart.plaza.users.models.responses.PromoResponse;

import java.util.List;

public interface UserPromoServiceV1 {
    void addPromoToUser(UserPromoRequest userPromoRequest);
    List<PromoResponse> getPromoByUserId(Long userId);
}
