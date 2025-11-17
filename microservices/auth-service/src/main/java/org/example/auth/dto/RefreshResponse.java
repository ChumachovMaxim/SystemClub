package org.example.auth.dto;

import lombok.Data;

@Data
public class RefreshResponse {
    private String newAccessToken;
    private String newRefreshToken;
}
