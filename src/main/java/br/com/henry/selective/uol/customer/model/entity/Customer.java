package br.com.henry.selective.uol.customer.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "customers")
@DynamicUpdate
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Nullable
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(min = 1, max = 255)
    private String name;

    @NotNull(message = "Age is mandatory")
    @Max(180)
    @Min(1)
    private Integer age;

    @Nullable
    @OneToOne(cascade = CascadeType.ALL)
    private LocationData location;

    @Nullable
    @OneToOne(cascade = CascadeType.ALL)
    private ClimateData climate;

}
