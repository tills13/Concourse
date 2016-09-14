package ca.sbstn.concourse.api.model;

/**
 * Created by tills13 on 2016-09-14.
 */
public class Pipeline {
    public String name;
    public String url;
    public boolean paused;

    public Pipeline(String name, String url, boolean paused) {
        this.name = name;
        this.url = url;
        this.paused = paused;
    }
}
