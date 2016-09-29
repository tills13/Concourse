package ca.sbstn.concourse.api.model;

public class Pipeline {
    protected String name;
    protected String url;
    protected boolean paused;

    public Pipeline(String name, String url, boolean paused) {
        this.name = name;
        this.url = url;
        this.paused = paused;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
