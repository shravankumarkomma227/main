package com.clinicadmin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JwkKey {
    private String kid;
    private String kty;
    private String alg;
    private String use;
    private String n;
    private String e;
}