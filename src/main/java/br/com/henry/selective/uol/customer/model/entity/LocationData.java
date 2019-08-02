package br.com.henry.selective.uol.customer.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Table(name = "locations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Nullable
    private Long id;

    @NotNull
    @Size(min = 1, max = 255)
    private String continentName;

    @NotNull
    @Size(min = 1, max = 255)
    private String countryName;

    @NotNull
    @Size(min = 1, max = 255)
    private String cityName;

    @NotNull
    @Size(min = 1, max = 255)
    private BigDecimal latitude;

    @NotNull
    @Size(min = 1, max = 255)
    private BigDecimal longitude;

}
