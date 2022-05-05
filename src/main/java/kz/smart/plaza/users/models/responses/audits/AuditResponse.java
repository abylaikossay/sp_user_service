package kz.smart.plaza.users.models.responses.audits;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public abstract class AuditResponse {
    private Long id;
    private Date createdAt;
    private Date updatedAt;
}
