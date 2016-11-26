package ca.sbstn.concourse.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ca.sbstn.concourse.CIActivity;
import ca.sbstn.concourse.R;
import ca.sbstn.concourse.adapter.BuildListAdapter;
import ca.sbstn.concourse.api.ConcourseAPIService;
import ca.sbstn.concourse.api.model.Build;
import ca.sbstn.concourse.api.model.Job;
import ca.sbstn.concourse.api.model.Pipeline;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuildDetailsFragment extends Fragment {
    private static final String TAG = "BuildDetailsFragment";
    private static final String ARG_BUILD = "ARG_BUILD";

    private Build build;
    protected CIActivity context;
    protected Handler handler;

    private ScrollView layout;
    private LinearLayout contentView;
    private TextView buildOutputContainer;

    private Thread readerThread;
    private BufferedReader eventReader;

    public BuildDetailsFragment() {}

    public static BuildDetailsFragment newInstance(Build build) {
        BuildDetailsFragment fragment = new BuildDetailsFragment();

        Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_BUILD, build);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.build = (Build) this.getArguments().getSerializable(ARG_BUILD);
        }

        this.handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.layout = (ScrollView) inflater.inflate(R.layout.build_details_fragment, container, false);

        this.contentView = (LinearLayout) this.layout.findViewById(R.id.content);
        this.buildOutputContainer = ((TextView) this.layout.findViewById(R.id.build_output));
        ((TextView) this.layout.findViewById(R.id.build_id_and_name)).setText(String.format(Locale.getDefault(), "Build #%d", this.build.getId()));

        this.initializeEventStream();

        return layout;
    }

    public void initializeEventStream() {
        ConcourseAPIService api = this.context.getApi();
        api.getBuildEvents(this.build.getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                readerThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean endEventSent = false;

                        while (!endEventSent) {
                            eventReader = new BufferedReader(new InputStreamReader(response.body().byteStream()));

                            List<String> output = new ArrayList<>();
                            int cacheCounter = 0;

                            try {
                                String line;
                                while (!endEventSent && (line = eventReader.readLine()) != null) {
                                    try {
                                        if (line.startsWith("event: ")) {
                                            String event = line.substring(7);
                                            Log.e("event:", event);
                                            if (event.equals("end")) {
                                                endEventSent = true;
                                            }
                                        } else if (line.startsWith("data: ")) {
                                            String json = line.substring(6);
                                            JSONObject data = new JSONObject(json);
                                            String payload = data.getJSONObject("data").getString("payload");

                                            if (payload.startsWith("\r") || (output.size() >= 1 && output.get(output.size() - 1).endsWith("\r"))) {
                                                output.remove(output.size() - 1);
                                            }

                                            payload = payload.replace("\\n", "\n");
                                            //output.addAll(Arrays.asList(payload.split("\\n")));

                                            output.add(payload);

                                            if (cacheCounter++ % 10 == 0) {
                                                final String finalOutput = TextUtils.join("\n", output);

                                                // fml
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        buildOutputContainer.setText(finalOutput);
                                                        layout.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                layout.fullScroll(View.FOCUS_DOWN);
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    } catch (JSONException e) {
                                        Log.i("JsonException", e.getMessage());
                                    }
                                }
                            } catch (IOException e) {
                                Log.i("IOException", e.getMessage());
                            } finally {
                                Log.e(TAG, "closing connections");
                                /*try {
                                    eventReader.close();
                                    eventReader = null;
                                } catch (IOException e) {
                                    Log.e(TAG, e.getMessage());
                                }*/

                                Snackbar.make(contentView, "Build Complete", Snackbar.LENGTH_LONG).show();
                            }
                        }

                        Log.d(TAG, "read thread complete");
                    }
                });

                readerThread.start();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = (CIActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (eventReader != null) {
            try {
                eventReader.close();
            } catch (IOException e) {
                Log.e("BuildDetailsFragment", e.getMessage());
            }
        }

        if (this.readerThread != null) {
            readerThread.interrupt();
        }
    }
}
