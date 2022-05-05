package kz.smart.plaza.users.clients;

import kz.smart.plaza.users.models.requests.BrandPartnerRequest;
import kz.smart.plaza.users.models.requests.EmployeeRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient("partner-service")
public interface PartnerService {


    @GetMapping("api/brand/feign/{brandId}")
    ResponseEntity<BrandPartnerRequest> getBrandAndPartnerByBrandId(@PathVariable Long brandId);

    @GetMapping("api/employee/{id}")
    ResponseEntity<EmployeeRequest> getEmployeeById(@PathVariable Long id);

}
