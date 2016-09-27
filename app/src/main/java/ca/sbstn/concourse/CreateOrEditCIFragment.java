package ca.sbstn.concourse;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.realm.Realm;

public class CreateOrEditCIFragment extends Fragment {
    private static final String ARG_NAME = "ARG_NAME";

    private String ciName; // name of ci server we are editing
    private OnFragmentInteractionListener mListener;

    public CreateOrEditCIFragment() {}

    public static CreateOrEditCIFragment newInstance(String name) {
        CreateOrEditCIFragment fragment = new CreateOrEditCIFragment();

        if (name != null) {
            Bundle arguments = new Bundle();
            arguments.putString(ARG_NAME, name);

            fragment.setArguments(arguments);
        }

        return fragment;
    }

    public static CreateOrEditCIFragment newInstance() {
        return CreateOrEditCIFragment.newInstance(null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.getArguments() != null) {
             this.ciName = this.getArguments().getString(ARG_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_or_edit_ci, container, false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onSave() {
        Realm realm = Realm.getDefaultInstance();
        realm.where()
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
