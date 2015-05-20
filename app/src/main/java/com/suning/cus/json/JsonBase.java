package com.suning.cus.json;

/**
 * Created by 14110105 on 2015/3/11.
 */
public class JsonBase {

    /**
     * 成功失败标志
     */
    protected String isSuccess;
    /**
     * 返回错误时的描述
     */
    protected  String errorDesc;




    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    private String successDesc;

    public String getSuccessDesc() {
        return successDesc;
    }

    public void setSuccessDesc(String successDesc) {
        this.successDesc = successDesc;
    }


}
