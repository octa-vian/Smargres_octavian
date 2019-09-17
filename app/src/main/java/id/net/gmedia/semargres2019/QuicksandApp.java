package id.net.gmedia.semargres2019;

import android.app.Application;

import id.net.gmedia.semargres2019.Util.FontsOverride;

public class QuicksandApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "SERIF", "Quicksand-Regular.ttf");
    }
}
