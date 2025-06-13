package com.roburo.passwordmanager;

public class Credential {
    private final String site;
    private final String username;
    private final String password;

    public Credential(String site, String username, String password) {
        this.site = site;
        this.username = username;
        this.password = password;
    }

    public String getSite()      { return site; }
    public String getUsername()  { return username; }
    public String getPassword()  { return password; }
}
