package com.common.update;

import java.io.Serializable;

/**
 * @Description: 版本信息
 * @Author: xingguo.lei@luckincoffee.com
 * @Date: 2019-12-17 09:52
 */
public class Version implements Serializable {

    private boolean force;

    private boolean upgrade;

    private String address;

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public boolean isUpgrade() {
        return upgrade;
    }

    public void setUpgrade(boolean upgrade) {
        this.upgrade = upgrade;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
