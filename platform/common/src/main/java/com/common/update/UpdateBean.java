package com.common.update;

import java.io.Serializable;

/**
 * @Author: xingguo.lei@luckincoffee.com
 * @Date: 2019-12-17 09:52
 */
public class UpdateBean implements Serializable {

    private long timestamp;

    private Version version;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }
}
