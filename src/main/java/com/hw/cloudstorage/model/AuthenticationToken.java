package com.hw.cloudstorage.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationToken {
    @JsonProperty("auth-token")
    private String authToken;
}
