package br.com.henry.selective.uol.customer.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class CustomerUpdate {

    @NotNull
    private Long id;

    @Nullable
    @Size(min = 1, max = 255)
    private String name;

    @Nullable
    @Max(180)
    @Min(1)
    private Integer age;

}
