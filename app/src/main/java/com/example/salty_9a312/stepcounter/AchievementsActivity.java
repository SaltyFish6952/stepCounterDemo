package com.example.salty_9a312.stepcounter;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class AchievementsActivity extends AppCompatActivity {

    private static final int[] ACHIEVEMENT_VALUE = {500, 1000, 5000, 10000, 20000};
    private ArrayList<achievementItem> achievementItems = new ArrayList<>();
    private achievementAdapter achievementAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        initList();

    }

    public void initList() {

        ListView achievement = findViewById(R.id.achievements_list);

        int total = getTotalCount();

        for (int i = 0; i < ACHIEVEMENT_VALUE.length; i++) {
            achievementItems.add(new achievementItem(ACHIEVEMENT_VALUE[i], total));
        }

        achievementAdapter = new achievementAdapter(this, R.layout.achievement_list, achievementItems);

        achievement.setAdapter(achievementAdapter);


    }


    public int getTotalCount() {

        DBUtils dbUtils = MainActivity.getDbUtils();

        Cursor cursor = dbUtils.query(new String[]{"current_step"}, null, null, null);

        int total_count = 0;

        if (cursor.moveToFirst()) {

            int temp = cursor.getInt(cursor.getColumnIndex("current_step"));

            total_count += temp;

        }
        while (cursor.moveToNext()) ;

        cursor.close();

        return total_count;

    }


}
