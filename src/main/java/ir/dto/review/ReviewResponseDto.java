package ir.dto.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewResponseDto {
    private Integer rating;
    private String comment;
    private Long orderNumber;
    private String orderDescription;
    private String customerFirstName;
    private String customerLastName;
    private String expertFirstName;
    private String expertLastName;
    private String subCategoryName;
}