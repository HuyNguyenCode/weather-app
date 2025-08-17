package com.ninegroup.weather.api;

import com.google.gson.annotations.SerializedName;

public class Token {
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("expires_in")
    private Integer expires;
    @SerializedName("refresh_expires_in")
    private Integer refreshExpires;
    @SerializedName("refresh_token")
    private String refreshToken;
    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("not-before-policy")
    private float policy;
    @SerializedName("session_state")
    private String session;
    @SerializedName("scope")
    private String scope;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public float getExpires() {
        return expires;
    }

    public void setExpires(Integer expires) {
        this.expires = expires;
    }

    public float getRefreshExpires() {
        return refreshExpires;
    }

    public void setRefreshExpires(Integer refreshExpires) {
        this.refreshExpires = refreshExpires;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public float getPolicy() {
        return policy;
    }

    public void setPolicy(float policy) {
        this.policy = policy;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "Token{" +
                "accessToken='" + accessToken + '\'' +
                ", expires=" + expires +
                ", refreshExpires=" + refreshExpires +
                ", refreshToken='" + refreshToken + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", policy=" + policy +
                ", session='" + session + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }

    public Token(String accessToken, Integer expires, Integer refreshExpires, String refreshToken,
                 String tokenType, float policy, String session, String scope) {
        this.accessToken = accessToken;
        this.expires = expires;
        this.refreshExpires = refreshExpires;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.policy = policy;
        this.session = session;
        this.scope = scope;
    }
}
