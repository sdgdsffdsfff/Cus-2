package com.suning.cus.json;

/**
 * Created by 14110105 on 2015/3/12.
 */
public class JsonUser extends JsonBase {

    /**
     * 账户ID：唯一的标识
     */
    private String employeeId;

    /**
     * 是否是初始密码
     */
    private String isFirstLogin;

    /**
     * 服务器时间:YYYY-MM-DD hh:mm:ss
     */
    private String serverTime;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getIsFirstLogin() {
        return isFirstLogin;
    }

    public void setIsFirstLogin(String isFirstLogin) {
        this.isFirstLogin = isFirstLogin;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    // 是否是第一次登录，Y则需要强制修改密码
    private String firstLogin;

    public String getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(String firstLogin) {
        this.firstLogin = firstLogin;
    }
}
