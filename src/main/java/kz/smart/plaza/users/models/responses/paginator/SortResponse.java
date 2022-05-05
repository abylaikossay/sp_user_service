package kz.smart.plaza.users.models.responses.paginator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SortResponse {
    private Boolean sorted;
    private Boolean unsorted;
    private Boolean empty;
}
