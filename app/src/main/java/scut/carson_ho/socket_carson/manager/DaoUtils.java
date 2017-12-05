package scut.carson_ho.socket_carson.manager;

import android.content.Context;

/**
 * Created by Administrator on 2017/7/7 0007.
 */

public class DaoUtils {

    private  static UserAndCardManager studentManager;
    public  static Context context;

    public static void init(Context context){
        DaoUtils.context = context;
    }

    /**
     * 单列模式获取StudentManager对象
     * @return
     */
    public static UserAndCardManager getStudentInstance(){
        if (studentManager == null) {
            studentManager = new UserAndCardManager(context);
        }
        return studentManager;
    }
}
