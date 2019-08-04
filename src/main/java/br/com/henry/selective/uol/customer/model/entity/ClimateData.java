package br.com.henry.selective.uol.customer.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Table(name = "climates")
@Data
@NoArgsConstructor
public class ClimateData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Nullable
    private Long id;

    @NotNull
    @Size(min = 1, max = 255)
    private BigDecimal minimumTemperature;

    @NotNull
    @Size(min = 1, max = 255)
    private BigDecimal maximumTemperature;

}
