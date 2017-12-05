package scut.carson_ho.socket_carson.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by gll on 2017/12/4.
 */
@Entity
public class UserAndCard {
    /**
     * ID
     * 用户表
     * 卡号
     * 手机号
     * 名字
     * 房间号
     *
     **/
    @Id
    private long id;
    private String mCardNumber;
    private String mPhone;
    private String mName;
    private String mDoorNumber;
    @Generated(hash = 234049680)
    public UserAndCard(long id, String mCardNumber, String mPhone, String mName,
            String mDoorNumber) {
        this.id = id;
        this.mCardNumber = mCardNumber;
        this.mPhone = mPhone;
        this.mName = mName;
        this.mDoorNumber = mDoorNumber;
    }
    @Generated(hash = 635334594)
    public UserAndCard() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getMCardNumber() {
        return this.mCardNumber;
    }
    public void setMCardNumber(String mCardNumber) {
        this.mCardNumber = mCardNumber;
    }
    public String getMPhone() {
        return this.mPhone;
    }
    public void setMPhone(String mPhone) {
        this.mPhone = mPhone;
    }
    public String getMName() {
        return this.mName;
    }
    public void setMName(String mName) {
        this.mName = mName;
    }
    public String getMDoorNumber() {
        return this.mDoorNumber;
    }
    public void setMDoorNumber(String mDoorNumber) {
        this.mDoorNumber = mDoorNumber;
    }





}
