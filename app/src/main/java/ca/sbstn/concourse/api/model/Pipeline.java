package ca.sbstn.concourse.api.model;

import com.google.gson.annotations.SerializedName;

public class Pipeline {
    protected String name;
    protected String url;
    protected boolean paused;
    @SerializedName("public") protected boolean isPublic;
    @SerializedName("team_name") protected String team;
    protected Build lastBuild;

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

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public Build getLastBuild() {
        return lastBuild;
    }

    public void setLastBuild(Build lastBuild) {
        this.lastBuild = lastBuild;
    }
}
