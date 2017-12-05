package scut.carson_ho.socket_carson.manager;

import android.content.Context;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import scut.carson_ho.socket_carson.bean.UserAndCard;
import scut.carson_ho.socket_carson.dao.UserAndCardDao;

/**
 * Created by gll on 2017/12/4.
 */

public class UserAndCardManager extends BaseDao {
    public UserAndCardManager(Context context) {
        super(context);
    }
    /***************************数据库查询*************************/

    /**
     * 通过ID查询对象
     * @param id
     * @return
     */
    private UserAndCard loadById(long id){
        return daoSession.getUserAndCardDao().load(id);
    }

    /**
     * 获取某个对象的主键ID
     * @param student
     * @return
     */
    private long getID(UserAndCard student){

        return daoSession.getUserAndCardDao().getKey(student);
    }

    /**
     * 通过名字获取Customer对象
     * @return
     */
    private List<UserAndCard> getStudentByName(String key){
        QueryBuilder queryBuilder =  daoSession.getUserAndCardDao().queryBuilder();
        queryBuilder.where(UserAndCardDao.Properties.MName.eq(key));
        int size = queryBuilder.list().size();
        if (size > 0){
            return queryBuilder.list();
        }else{
            return null;
        }
    }

    /**
     * 通过名字获取Customer对象
     * @return
     */
    private List<Long> getIdByName(String key){
        List<UserAndCard> students = getStudentByName(key);
        List<Long> ids = new ArrayList<Long>();
        int size = students.size();
        if (size > 0){
            for (int i = 0;i < size;i++){
                ids.add(students.get(i).getId());
            }
            return ids;
        }else{
            return null;
        }
    }

    /***************************数据库删除*************************/

    /**
     * 根据ID进行数据库的删除操作
     * @param id
     */
    private void deleteById(long id){

        daoSession.getUserAndCardDao().deleteByKey(id);
    }


    /**
     * 根据ID同步删除数据库操作
     * @param ids
     */
    private void deleteByIds(List<Long> ids){

        daoSession.getUserAndCardDao().deleteByKeyInTx(ids);
    }






}
