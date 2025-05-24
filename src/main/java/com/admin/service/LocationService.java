package com.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final RestTemplate restTemplate;

    // List of restricted countries
    private static final List<String> BLOCKED_COUNTRIES = Arrays.asList(
            "Syria", "Afghanistan", "Iran"
    );

    public String getCountryFromIp(String ip) {
        try {
            String url = "https://ipapi.co/" + ip + "/country_name";
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isBlockedCountry(String country) {
        return country != null && BLOCKED_COUNTRIES.contains(country);
    }
}