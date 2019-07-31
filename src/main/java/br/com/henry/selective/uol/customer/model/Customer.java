package br.com.henry.selective.uol.customer.model;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Nullable
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    @NotBlank(message = "Age is mandatory")
    @NotNull
    @Max(180)
    private Integer age;

}
