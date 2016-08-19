package com.miqtech.master.client.entity;

import java.io.Serializable;

public class User implements Serializable {
    String username;
    String nickname;
    String icon;
    String iconThumb;
    String iconMedia;
    String telephone;
    String score;
    String id;
    String password;
    String token;
    String idCard = "";
    String realName = "";
    String cityCode;
    String cityName;
    String qq = "";
    String sex = "";
    String speech;
    String bgImg;
    int acceptAccess;
    int acceptMatch;
    String labor;
    int isChecked = 0;
    String moduleInfo;   //感兴趣资讯模块
    int isUp; //0非，1是

    public String getModuleInfo() {
        return moduleInfo;
    }

    public void setModuleInfo(String moduleInfo) {
        this.moduleInfo = moduleInfo;
    }

    int isPasswordNull;//密码是否为空1是0否
    int profileStatus;//用户信息是否完善0否1是


    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public int getIsUp() {
        return isUp;
    }

    public void setIsUp(int isUp) {
        this.isUp = isUp;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIconThumb() {
        return iconThumb;
    }

    public void setIconThumb(String iconThumb) {
        this.iconThumb = iconThumb;
    }

    public String getIconMedia() {
        return iconMedia;
    }

    public void setIconMedia(String iconMedia) {
        this.iconMedia = iconMedia;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getSpeech() {
        return speech;
    }

    public void setSpeech(String speech) {
        this.speech = speech;
    }

    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }

    public int getAcceptAccess() {
        return acceptAccess;
    }

    public void setAcceptAccess(int acceptAccess) {
        this.acceptAccess = acceptAccess;
    }

    public int getAcceptMatch() {
        return acceptMatch;
    }

    public void setAcceptMatch(int acceptMatch) {
        this.acceptMatch = acceptMatch;
    }

    public String getLabor() {
        return labor;
    }

    public void setLabor(String labor) {
        this.labor = labor;
    }

    public int getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(int isChecked) {
        this.isChecked = isChecked;
    }

    public int getIsPasswordNull() {
        return isPasswordNull;
    }

    public void setIsPasswordNull(int isPasswordNull) {
        this.isPasswordNull = isPasswordNull;
    }

    public int getProfileStatus() {
        return profileStatus;
    }

    public void setProfileStatus(int profileStatus) {
        this.profileStatus = profileStatus;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", icon='" + icon + '\'' +
                ", iconThumb='" + iconThumb + '\'' +
                ", iconMedia='" + iconMedia + '\'' +
                ", telephone='" + telephone + '\'' +
                ", score='" + score + '\'' +
                ", id='" + id + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                ", idCard='" + idCard + '\'' +
                ", realName='" + realName + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", cityName='" + cityName + '\'' +
                ", qq='" + qq + '\'' +
                ", sex='" + sex + '\'' +
                ", speech='" + speech + '\'' +
                ", bgImg='" + bgImg + '\'' +
                ", acceptAccess=" + acceptAccess +
                ", acceptMatch=" + acceptMatch +
                ", labor='" + labor + '\'' +
                ", isChecked=" + isChecked + '\'' +
                ", profileStatus=" + profileStatus + '\'' +
                ", isPasswordNull=" + isPasswordNull +
                '}';
    }
}
