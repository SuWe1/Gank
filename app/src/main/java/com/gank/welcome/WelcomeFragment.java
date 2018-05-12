package com.gank.welcome;

/**
 * Created by Swy on 2017/6/12.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gank.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class WelcomeFragment extends Fragment {

    private AppCompatTextView description_head;
    private AppCompatTextView description_body;
    private ImageView img_welcome_description;

    private int page;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public WelcomeFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static WelcomeFragment newInstance(int sectionNumber) {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt(ARG_SECTION_NUMBER);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);
        initView(rootView);
        switch (page) {
            case 0:
                description_head.setText(R.string.welcoem_description_one_head);
                description_body.setText(R.string.welcoem_description_one_body);
                img_welcome_description.setBackgroundResource(R.drawable.welcome_page_one);
                break;
            case 1:
                description_head.setText(R.string.welcoem_description_two_head);
                description_body.setText(R.string.welcoem_description_two_body);
                img_welcome_description.setBackgroundResource(R.drawable.welcome_page_two);
                break;
            case 2:
                description_head.setText(R.string.welcoem_description_three_head);
                description_body.setText(R.string.welcoem_description_three_body);
                img_welcome_description.setBackgroundResource(R.drawable.ic_share_2);
            default:
                break;
        }
        return rootView;
    }

    private void initView(View view) {
        description_head = (AppCompatTextView) view.findViewById(R.id.description_head);
        description_body = (AppCompatTextView) view.findViewById(R.id.description_body);
        img_welcome_description = (ImageView) view.findViewById(R.id.img_welcome_description);
    }
}
