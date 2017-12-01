package scut.carson_ho.socket_carson.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by fangwenjiao on 14-9-4.
 */
public class VLCApplication extends Application {

	private static VLCApplication sInstance;

	@Override
	public void onCreate() {
		super.onCreate();

		sInstance = this;
	}

	public static Context getAppContext() {
		return sInstance;
	}
}
