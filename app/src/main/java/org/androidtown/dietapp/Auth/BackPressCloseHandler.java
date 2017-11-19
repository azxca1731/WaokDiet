package org.androidtown.dietapp.Auth;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by azxca on 2017-11-10.
 */

public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Toast toast;
    private Activity activity;
    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }


    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis(); showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            toast.cancel();
            activity.finishAffinity();
            System.exit(0);
        }
    }


    public void showGuide() {
        toast = Toast.makeText(activity, "뒤로가기 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT); toast.show();
    }
}
