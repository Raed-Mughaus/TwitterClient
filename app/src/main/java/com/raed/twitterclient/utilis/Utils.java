package com.raed.twitterclient.utilis;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raed.twitterclient.MainActivity;
import com.raed.twitterclient.R;
import com.raed.twitterclient.TwitterErrors;
import com.raed.twitterclient.TwitterErrors.TwitterError;

import java.io.IOException;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import retrofit2.HttpException;

public class Utils {

    private static final NavigableMap<Long, String> suffixes = new TreeMap<Long, String>(){
        {
            put(1_000L, "k");
            put(1_000_000L, "M");
            put(1_000_000_000L, "G");
            put(1_000_000_000_000L, "T");
            put(1_000_000_000_000_000L, "P");
            put(1_000_000_000_000_000_000L, "E");
        }
    };

    //todo continue testing this method in intellij
    public static String getHappenXTimeAgoString(Resources resources, long happenAt){
        long now = System.currentTimeMillis();
        long since = now - happenAt;
        long sinceInSeconds = since/1000;

        long years = (sinceInSeconds / (60 * 60 * 24 * 365));
        if (years != 0) return resources.getQuantityString(R.plurals.years, ((int) years), years);

        long months = (sinceInSeconds / (60 * 60 * 24 * 31)) % 12;
        if (months != 0) return resources.getQuantityString(R.plurals.months, ((int) months), months);

        long days = (sinceInSeconds / (60 * 60 * 24) ) % 31;
        if (days != 0) return resources.getQuantityString(R.plurals.days, ((int) days), days);

        long hours = (sinceInSeconds / (60*60) ) % 24;
        if (hours != 0) return resources.getQuantityString(R.plurals.hours, ((int) hours), hours);

        long minutes = (sinceInSeconds / 60) % 60;
        if (minutes != 0) return resources.getQuantityString(R.plurals.minutes, ((int) minutes), minutes);

        long seconds = sinceInSeconds % 60 ;
        return resources.getQuantityString(R.plurals.seconds, ((int) seconds), seconds);
    }

    //copied from stack overflow
    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static boolean isOnline(Context context){
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static TwitterError[] extractTwitterErrors(HttpException e){
        String responseStr  = null;
        try { responseStr = e.response().errorBody().string(); } catch (IOException ignored) { }
        TypeToken typeToken = new TypeToken<TwitterErrors>(){};
        return ((TwitterErrors) new Gson().fromJson(responseStr, typeToken.getType())).errors;
    }

    public static void restartApp(Context context){
        Intent activityIntent = new Intent(context, MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent restartPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,
                activityIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mgr.setExactAndAllowWhileIdle(AlarmManager.RTC, System.currentTimeMillis() + 100, restartPendingIntent);
        }else
            mgr.setExact(AlarmManager.RTC, System.currentTimeMillis() + 100, restartPendingIntent);
        System.exit(0);
    }

    /*public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap
                .createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);


        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }*/
}
