package kz.smart.plaza.users.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagRequest {
    private String name;
    private Boolean noTransactionBlocking;
}
