package com.example.salty_9a312.stepcounter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class achievementAdapter extends ArrayAdapter<achievementItem> {

    ArrayList<achievementItem> achievementItemArrayList = new ArrayList<>();


    public achievementAdapter(@NonNull Context context, int resource, @NonNull ArrayList<achievementItem> objects) {
        super(context, resource, objects);
        achievementItemArrayList = objects;
    }

    @Override
    public int getCount() {
        return achievementItemArrayList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.achievement_list, null);

        TextView progress_1 = view.findViewById(R.id.progress_count_1);
        TextView progress_2 = view.findViewById(R.id.progress_count_2);
        TextView progress_percent = view.findViewById(R.id.progress_percent);

        ProgressBar progressBar = view.findViewById(R.id.progress_bar);

        progress_1.setText(achievementItemArrayList.get(position).getAchievement_steps() +"");
        progress_2.setText(achievementItemArrayList.get(position).getAchievement_steps() + "");


        int current = achievementItemArrayList.get(position).getCurrent_steps();
        int achieve = achievementItemArrayList.get(position).getAchievement_steps();
        DecimalFormat decimalFormat = new DecimalFormat("0.0000");

        float div = Float.parseFloat(decimalFormat.format((float) current / achieve));

        Log.e("div",div +"");


        BigDecimal bigDecimal = new BigDecimal(div * 100);

        String percent = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).toString();
        int progress = (int) (bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue());

        Log.e("progress",progress + "");


        if(progress >= 100){
            progress = 100;
            percent = "100";
        }

        progressBar.setProgress(progress);
        progress_percent.setText(percent+"%");


        return view;
    }


}
