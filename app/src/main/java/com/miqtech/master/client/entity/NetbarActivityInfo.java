package com.miqtech.master.client.entity;

import java.util.List;

/**
 * Created by admin on 2016/3/10.
 */
public class NetbarActivityInfo {
    NetBarAmuse amuse;
    List<YueZhan> matches;
    List<NetbarService> services;

    public NetBarAmuse getAmuse() {
        return amuse;
    }

    public void setAmuse(NetBarAmuse amuse) {
        this.amuse = amuse;
    }

    public List<YueZhan> getMatches() {
        return matches;
    }

    public void setMatches(List<YueZhan> matches) {
        this.matches = matches;
    }

    public List<NetbarService> getServices() {
        return services;
    }

    public void setServices(List<NetbarService> services) {
        this.services = services;
    }
}
