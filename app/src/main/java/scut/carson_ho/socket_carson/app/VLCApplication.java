package scut.carson_ho.socket_carson.app;

import android.app.Application;
import android.content.Context;

import org.greenrobot.greendao.database.Database;

import scut.carson_ho.socket_carson.dao.DaoMaster;
import scut.carson_ho.socket_carson.dao.DaoSession;

/**
 * Created by fangwenjiao on 14-9-4.
 */
public class VLCApplication extends Application {

	private static VLCApplication sInstance;
	/**
	 * 是否加密数据库.
	 */

	public static Context applicationContext;
	public static final boolean ENCRYPTED = false;
	private static DaoSession daoSession;
	private final String DB_ENCRYPIED_NAME = "encrypied.db";
	private final String DB_NAME = "normal.db";
	@Override
	public void onCreate() {
		super.onCreate();
		DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? DB_ENCRYPIED_NAME : DB_NAME);
//        Database db = ENCRYPTED ? helper.getEncryptedWritableDb(getResources().getString(R.string.db_psw)) : helper.getWritableDb();//如果使用了加密，则需要用密码打开
		Database db = helper.getWritableDb();
		daoSession = new DaoMaster(db).newSession();//获取到daoSession，用于后续操作
		sInstance = this;
	}

	public static Context getAppContext() {
		return sInstance;
	}
	public static DaoSession getDaoSession() {
		return daoSession;
	}

	public static VLCApplication getInstance() {
		return sInstance;
	}
}
