package com.github.yangwk.jobscheduler.core.impl.registry;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class HostInfo implements Comparable<HostInfo>{
    public final String hostIp;
    public final int port;
    public HostInfo(String hostIp, int port) {
        this.hostIp = Objects.requireNonNull(hostIp);
        this.port = port;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(hostIp).append(port).toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) {
          return false;
        }
        HostInfo rhs = (HostInfo) obj;
        return new EqualsBuilder()
                      .append(hostIp, rhs.hostIp)
                      .append(port, rhs.port)
                      .isEquals();

    }

    @Override
    public String toString() {
        return new StringBuilder().append(hostIp).append(":").append(port).toString();
    }

    @Override
    public int compareTo(HostInfo hi) {
        int c = hostIp.compareTo(hi.hostIp);
        if(c != 0) {
            return c;
        }
        return Integer.compare(port, hi.port);
    }
    
    
}
