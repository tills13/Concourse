package ca.sbstn.concourse.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by tills13 on 29/09/16.
 */

public class Job implements Serializable {
    protected String name;
    protected String url;
    @SerializedName("finished_build") protected Build finishedBuild;
    @SerializedName("next_build") protected Build nextBuild;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Build getFinishedBuild() {
        return finishedBuild;
    }

    public void setFinishedBuild(Build finishedBuild) {
        this.finishedBuild = finishedBuild;
    }

    public Build getNextBuild() {
        return nextBuild;
    }

    public void setNextBuild(Build nextBuild) {
        this.nextBuild = nextBuild;
    }
}
