package com.suning.cus.bean;

import java.io.Serializable;

/**
 * 更新信息
 * @author 13075578
 *
 */
public class UpdateAppInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * 安装包下载路径
     */
    private String downloadAddr;
    /**
     * 版本号
     */
    private String version;

    public String getDownloadAddr() {
        return downloadAddr;
    }

    public void setDownloadAddr(String downloadAddr) {
        this.downloadAddr = downloadAddr;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
