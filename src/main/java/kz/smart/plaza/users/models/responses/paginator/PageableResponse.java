package kz.smart.plaza.users.models.responses.paginator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageableResponse {

    private SortResponse sortResponse;
    private Integer offset;
    private Integer pageSize;
    private Integer pageNumber;
    private Boolean paged;
    private Boolean unpaged;
}
