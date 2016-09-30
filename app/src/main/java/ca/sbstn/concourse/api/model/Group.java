package ca.sbstn.concourse.api.model;

/**
 * Created by tills13 on 29/09/16.
 */

public class Group {
    protected String name;
    protected String[] jobs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getJobs() {
        return jobs;
    }

    public void setJobs(String[] jobs) {
        this.jobs = jobs;
    }
}
