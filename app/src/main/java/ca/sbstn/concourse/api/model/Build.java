package ca.sbstn.concourse.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by tills13 on 29/09/16.
 */

public class Build {
    protected int id;
    protected String name;
    protected String status;
    protected String jobName;
    protected String url;
    protected String apiURL;
    protected String pipelineName;

    @SerializedName("start_time") protected long startTime;
    @SerializedName("end_time") protected long endTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApiURL() {
        return apiURL;
    }

    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
