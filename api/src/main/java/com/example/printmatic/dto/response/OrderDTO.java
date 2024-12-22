package com.example.printmatic.dto.response;

import com.example.printmatic.enums.OrderStatus;
import com.example.printmatic.enums.PageSize;
import com.example.printmatic.enums.PaperType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@Data
public class OrderDTO {
    private Long id;
    private  String title;
    private String fileUrl;
    private int copies;
    private boolean doubleSided;
    private PageSize pageSize;
    private PaperType paperType;
    private Integer colorfulPages;
    private Integer grayscalePages;
    private String additionalInfo;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
}
