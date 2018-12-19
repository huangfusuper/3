package com.baizhi.netty2;

public class HostAndPost {
    private String host;
    private int post;

    @Override
    public String toString() {
        return "HostAndPost{" +
                "host='" + host + '\'' +
                ", post=" + post +
                '}';
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPost() {
        return post;
    }

    public void setPost(int post) {
        this.post = post;
    }

    public HostAndPost(String host, int post) {
        this.host = host;
        this.post = post;
    }

    public HostAndPost() {
    }
}
