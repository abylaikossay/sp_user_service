package kz.smart.plaza.users.models.responses;

import kz.smart.plaza.users.models.responses.audits.AuditResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagResponse {
    private String name;
    private Boolean noTransactionBlocking;
    private Long id;
    private Date createdAt;
    private Date updatedAt;

}
