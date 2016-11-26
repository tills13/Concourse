package ca.sbstn.concourse.api;

import java.util.List;

import ca.sbstn.concourse.api.model.Build;
import ca.sbstn.concourse.api.model.Job;
import ca.sbstn.concourse.api.model.Pipeline;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

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

    @PUT("pipelines/{pipeline}/pause")
    Call<ResponseBody> pausePipeline(@Path("pipeline") String pipeline);

    @PUT("pipelines/{pipeline}/unpause")
    Call<ResponseBody> unpausePipeline(@Path("pipeline") String pipeline);

    @GET("jobs")
    Call<List<Job>> getJobs();

    @GET("pipelines/{pipeline}/jobs")
    Call<List<Job>> getPipelineJobs(@Path("pipeline") String pipeline);

    @GET("teams/{team}/pipelines/{pipeline}/jobs")
    Call<List<Job>> getTeamPipelineJobs(@Path("team") String team, @Path("pipeline") String pipeline);

    @GET("pipelines/{pipeline}/jobs/{job}")
    Call<Job> getJob(@Path("pipeline") String pipeline, @Path("job") String job);

    @GET("teams/{team}/pipelines/{pipeline}/jobs/{job}")
    Call<Job> getTeamPipelineJob(@Path("team") String team, @Path("pipeline") String pipeline, @Path("job") String job);

    @GET("pipelines/{pipeline}/jobs/{job}/builds")
    Call<List<Build>> getJobBuilds(@Path("pipeline") String pipeline, @Path("job") String job);

    @POST("pipelines/{pipeline}/jobs/{job}/builds")
    Call<Build> createNewBuild(@Path("pipeline") String pipeline, @Path("job") String job);

    @POST("builds/{build}/abort")
    Call<Build> abortBuild(@Path("build") int buildId);

    @Streaming
    @GET("builds/{build}/events")
    Call<ResponseBody> getBuildEvents(@Path("build") int buildId);
}
