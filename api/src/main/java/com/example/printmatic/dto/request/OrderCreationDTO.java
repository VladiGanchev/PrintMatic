package com.example.printmatic.dto.request;

import com.example.printmatic.enums.DeadlineEnum;
import com.example.printmatic.enums.PageSize;
import com.example.printmatic.enums.PaperType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class OrderCreationDTO {

    @NotNull(message = "The title has to be present")
    private String title;

    @NotNull(message = "Number of copies can't be null")
    @Min(value = 1, message = "At least one copy is required.")
    private int copies;

    @NotNull(message = "Double sided or not must be specified.")
    private boolean doubleSided;

    @NotNull(message = "Page size is required.")
    private PageSize pageSize;

    @NotNull(message = "Paper type is required.")
    private PaperType paperType;

    private String additionalInfo;

    @NotNull
    private DeadlineEnum deadline;

    @NotNull
    private String fileUrl;

    @NotNull
    private int totalPages;

    @NotNull
    private int colorfulPages;

    @NotNull
    private int grayscalePages;
}
