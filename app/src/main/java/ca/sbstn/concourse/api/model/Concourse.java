package ca.sbstn.concourse.api.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Concourse extends RealmObject {
    @PrimaryKey
    protected String name;
    protected String host;
    protected String proxyHost;
    protected int proxyPort;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProxyHost() {
        return this.proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return this.proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
