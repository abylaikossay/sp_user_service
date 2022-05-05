package kz.smart.plaza.users.models.responses.cinemaxSignature;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CinemaxIdentifyResponse {
    private Long user;
    private String bonus;
    private Integer level;
    private List<String> cards;
}
