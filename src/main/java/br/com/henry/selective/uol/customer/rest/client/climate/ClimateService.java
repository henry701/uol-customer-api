package br.com.henry.selective.uol.customer.rest.client.climate;

import br.com.henry.selective.uol.customer.model.entity.ClimateData;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface ClimateService {
    ClimateData getClimateForLocation(BigDecimal latitude, BigDecimal longitude);
}
