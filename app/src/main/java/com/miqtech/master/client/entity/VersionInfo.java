package com.miqtech.master.client.entity;

public class VersionInfo {
    private String version;
    private int versionCode;
    private String url;
    private int isCoercive;
    private int type;

    private int patchCode;

    private String patchUrl;

    public int getPatchCode() {
        return patchCode;
    }

    public void setPatchCode(int patchCode) {
        this.patchCode = patchCode;
    }

    public String getPatchUrl() {
        return patchUrl;
    }

    public void setPatchUrl(String patchUrl) {
        this.patchUrl = patchUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIsCoercive() {
        return isCoercive;
    }

    public void setIsCoercive(int isCoercive) {
        this.isCoercive = isCoercive;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
