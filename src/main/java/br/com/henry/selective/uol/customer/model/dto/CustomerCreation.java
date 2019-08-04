package br.com.henry.selective.uol.customer.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class CustomerCreation {

    @NotBlank(message = "Name is mandatory")
    @Size(min = 1, max = 255)
    private String name;

    @NotNull(message = "Age is mandatory")
    @Min(1)
    private Integer age;

}
