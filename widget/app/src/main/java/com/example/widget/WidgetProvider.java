package com.example.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

    SqlHelper helper;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_provider);

            helper = new SqlHelper(context);
            Cursor c = helper.getAllStudents();

            if(c.moveToNext()) {
                views.setViewVisibility(R.id.tvOne, View.VISIBLE);
                views.setTextViewText(R.id.tvOne, c.getString(c.getColumnIndex("Name")));
                if(c.moveToNext()) {
                    views.setViewVisibility(R.id.tvTwo, View.VISIBLE);
                    views.setTextViewText(R.id.tvTwo, c.getString(c.getColumnIndex("Name")));
                    if(c.moveToNext()) {
                        views.setViewVisibility(R.id.tvThree, View.VISIBLE);
                        views.setTextViewText(R.id.tvThree, c.getString(c.getColumnIndex("Name")));
                        if(c.moveToNext()) {
                            views.setViewVisibility(R.id.tvFour, View.VISIBLE);
                            views.setTextViewText(R.id.tvFour, c.getString(c.getColumnIndex("Name")));
                        }
                    }
                }
            }

            views.setOnClickPendingIntent(R.id.gotoBtn, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
