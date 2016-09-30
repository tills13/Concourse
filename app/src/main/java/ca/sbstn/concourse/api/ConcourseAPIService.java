package ca.sbstn.concourse.api;

import java.util.List;

import ca.sbstn.concourse.api.model.Job;
import ca.sbstn.concourse.api.model.Pipeline;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by tills13 on 2016-09-14.
 */
public interface ConcourseAPIService {
    @GET("pipelines")
    Call<List<Pipeline>> getPipelines();

    @GET("teams/{team}/pipelines")
    Call<List<Pipeline>> getTeamPipelines(@Path("team") String team);

    @GET("pipelines/{pipeline}")
    Call<Pipeline> getPipeline(@Path("pipeline") String pipeline);

    @GET("pipelines/{pipeline}/jobs")
    Call<List<Job>> getPipelineJobs(@Path("pipeline") String pipeline);

    @GET("teams/{team}/pipelines/{pipeline}/jobs")
    Call<List<Job>> getTeamPipelineJobs(@Path("team") String team, @Path("pipeline") String pipeline);

    @GET("jobs")
    Call<List<Job>> getJobs();
}
