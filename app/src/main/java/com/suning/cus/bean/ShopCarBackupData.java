package com.suning.cus.bean;

import com.lidroid.xutils.db.annotation.Id;

import java.io.Serializable;

/**
 * Created by 15010551 on 2015/3/23.
 */
public class ShopCarBackupData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(column="cmmdtyId")
    private int cmmdtyId;

    /**
     * 配件编码
     */
    private String cmmdtyCode;

    public String getCmmdtyCode() {
        return cmmdtyCode;
    }

    public void setCmmdtyCode(String cmmdtyCode) {
        this.cmmdtyCode = cmmdtyCode;
    }

    /**
     * 配件名称
     */
    private String cmmdtyName;

    public String getCmmdtyName() {
        return cmmdtyName;
    }

    public void setCmmdtyName(String cmmdtyName) {
        this.cmmdtyName = cmmdtyName;
    }

    /**
     * 配件数量
     */
    private String cmmdtyNum;

    public String getCmmdtyNum() {
        return cmmdtyNum;
    }

    public void setCmmdtyNum(String cmmdtyNum) {
        this.cmmdtyNum = cmmdtyNum;
    }

    /**
     * 配件单价
     */
    private String cmmdtyPrice;

    public String getCmmdtyPrice() {
        return cmmdtyPrice;
    }

    public void setCmmdtyPrice(String cmmdtyPrice) {
        this.cmmdtyPrice = cmmdtyPrice;
    }
}
