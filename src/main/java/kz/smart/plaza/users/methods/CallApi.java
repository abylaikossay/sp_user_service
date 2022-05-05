package kz.smart.plaza.users.methods;

import kz.smart.plaza.users.clients.AuthServiceClient;
import kz.smart.plaza.users.clients.BonusService;
import kz.smart.plaza.users.clients.PartnerService;
import kz.smart.plaza.users.models.requests.*;
import kz.smart.plaza.users.models.responses.UserBonusResponse;
import kz.smart.plaza.users.models.responses.authService.UserInfoResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class CallApi {
    private BonusService bonusService;
    private AuthServiceClient authServiceClient;
    private PartnerService partnerService;

    public BonusRequest getBonus(Long userId) {
        try {
            ResponseEntity<BonusRequest> response
                    = bonusService.getBonus(userId);
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public EcoBonusRequest getEcoBonus(Long userId) {
        try {
            ResponseEntity<EcoBonusRequest> response
                    = bonusService.getEcoBonus(userId);
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public List<UserBonusResponse> getUserBonuses(UserBonusRequest userBonusRequest) {
        try {
            ResponseEntity<List<UserBonusResponse>> response
                    = bonusService.getUserBonuses(userBonusRequest);
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public BrandPartnerRequest getBrandAndPartnerByBrandId(Long brandId) {
        try {
            ResponseEntity<BrandPartnerRequest> response
                    = partnerService.getBrandAndPartnerByBrandId(brandId);
            return response.getBody();
        }
        catch (Exception e) {
            return null;
        }
    }

    public EmployeeRequest getEmployeeById(Long employeeId) {
        try {
            ResponseEntity<EmployeeRequest> response
                    = partnerService.getEmployeeById(employeeId);
            return response.getBody();
        }
        catch (Exception e) {
            return null;
        }
    }

    public UserInfoResponse getClientInfoByToken(String token) {
        try {
            ResponseEntity<UserInfoResponse> response
                    = authServiceClient.getClientInfoByToken(token);
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
