package kz.smart.plaza.users.models.responses.paginator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse {
    private List<?>content;
    private Integer totalPages;
    private Long totalElements;
    private Boolean last;
    private Integer size;
    private Integer number;
    private Integer numberOfElements;
    private Boolean first;
    private SortResponse sortResponse;
    private Boolean empty;
    private PageableResponse pageable;
}
