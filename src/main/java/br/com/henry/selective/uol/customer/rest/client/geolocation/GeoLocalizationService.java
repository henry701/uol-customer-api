package br.com.henry.selective.uol.customer.rest.client.geolocation;

import br.com.henry.selective.uol.customer.model.entity.LocationData;
import org.springframework.stereotype.Service;

@Service
public interface GeoLocalizationService {
    LocationData getLocationForIp(String ip);
}
