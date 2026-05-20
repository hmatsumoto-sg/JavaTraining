package com.s_giken.training.webapp.model.entity;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Charge {
    @Nullable
    private Long chargeId;

    @NotBlank(message = "入力してください。")
    private String name;

    @NotNull(message = "入力してください。")
    @Min(value = 0, message = "0以上の金額を入力してください。")
    @Max(value = 999999999, message = "金額が大きすぎます。")
    private Integer amount;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "入力してください。")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Nullable
    private LocalDate endDate;
}
