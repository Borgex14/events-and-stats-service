package ru;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatDtoResponse {
    @NotBlank
    private String app;

    @NotBlank
    private String uri;

    @NotNull
    @Min(0)
    private Long hits;
}