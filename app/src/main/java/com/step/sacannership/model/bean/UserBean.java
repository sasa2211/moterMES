package com.step.sacannership.model.bean;

public class UserBean {

    private String name;
    private String token;
    private int userId;
    private String userName;
    private int userType;
    private int loginType;
    private String userAuthority;

//    private List<NavItemListBean> navItemList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getUserAuthority() {
        return userAuthority;
    }

    public void setUserAuthority(String userAuthority) {
        this.userAuthority = userAuthority;
    }
    //
//    public List<NavItemListBean> getNavItemList() {
//        return navItemList;
//    }
//
//    public void setNavItemList(List<NavItemListBean> navItemList) {
//        this.navItemList = navItemList;
//    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }
}
