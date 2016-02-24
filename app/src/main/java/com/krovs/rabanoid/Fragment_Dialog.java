package com.krovs.rabanoid;

import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Fragment_Dialog extends AppCompatDialogFragment
{

    public Fragment_Dialog()
    {
        // Empty constructor required for DialogFragment
    }

    public static Fragment_Dialog newInstance(String title, String content)
    {
        Fragment_Dialog frag = new Fragment_Dialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("content", content);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dialog, container);
        TextView tv = (TextView)view.findViewById(R.id.dialog_tv1);
        Button btn = (Button)view.findViewById(R.id.btndis);
        ImageView iv = (ImageView)view.findViewById(R.id.imageView);

        tv.setText(Html.fromHtml(getArguments().getString("content")));
        String title = getArguments().getString("title");
        getDialog().setTitle(title);

        //if this dialog is for about, show icon image
        if (title == getString(R.string.menu_about))
            iv.setImageResource(getActivity().getResources().getIdentifier("ic_launcher", "mipmap", getActivity().getPackageName()));


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return view;
    }
}