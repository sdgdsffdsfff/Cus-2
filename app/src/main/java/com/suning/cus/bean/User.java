package com.suning.cus.bean;

/**
 * 用户
 * Created by 14110105 on 2015/3/11.
 */
public class User {

    /**
     * 用户ID
     */
    private String employeeId;

    /**
     * 登陆密码
     */
    private String password;

    /**
     * 手机IMEI
     */
    private String imei;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

}
