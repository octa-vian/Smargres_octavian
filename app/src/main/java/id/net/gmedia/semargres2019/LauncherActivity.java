package id.net.gmedia.semargres2019;

import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import id.net.gmedia.semargres2019.Util.AppSharedPreferences;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        ImageView img_splashscreen = findViewById(R.id.img_splashscreen);
        AlphaAnimation animation1 = new AlphaAnimation(0f, 1.0f);
        animation1.setDuration(3000);
        img_splashscreen.setAlpha(1f);
        img_splashscreen.startAnimation(animation1);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(AppSharedPreferences.isLoggedIn(LauncherActivity.this)){
                    startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                else{
                    startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        }, 3000);

        /*View ivLogo = findViewById(R.id.iv_logo);

        AlphaAnimation animation1 = new AlphaAnimation(0f, 1.0f);
        animation1.setDuration(2000);
        ivLogo.setAlpha(1f);
        ivLogo.startAnimation(animation1);

        int secondsDelayed = 3;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(AppSharedPreferences.isLoggedIn(LauncherActivity.this)){
                    startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                else{
                    startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, secondsDelayed * 1000);*/
    }
}
