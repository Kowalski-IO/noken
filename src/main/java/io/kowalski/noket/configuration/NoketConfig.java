package io.kowalski.noket.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class NoketConfig extends Configuration {

    @JsonProperty("secretKey")
    private String secretKey;

    @JsonProperty("registrationCode")
    private String registrationCode;

    public String getSecretKey() {
        return secretKey;
    }

    public String getRegistrationCode() {
        return registrationCode;
    }

}
