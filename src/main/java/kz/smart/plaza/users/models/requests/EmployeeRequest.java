package kz.smart.plaza.users.models.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeRequest {
    private Long id;
    private Date createdAt;
    private Date updatedAt;
    private String username;
    private Long brandId;
    private String brandName;
    private Long cityId;
    private Long renterTypeId;
    private Long partnerId;
    private String partnerName;
    private String role;
    private String name;
    private String phoneNumber;
}
