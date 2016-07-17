package com.bmob.im.demo.bean;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * ����BmobChatUser����������������Ҫ���ӵ����Կ��ڴ����
 *
 * @author smile
 * @ClassName: TextUser
 * @Description: TODO
 * @date 2014-5-29 ����6:15:45
 */
public class User extends BmobChatUser {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * //��ʾ���ƴ��������ĸ
     */
    private String sortLetters;

    /**
     * //�Ա�-true-��
     */
    private boolean sex;

    /**
     * �������
     */
    private BmobGeoPoint location;//

    public BmobGeoPoint getLocation() {
        return location;
    }

    public void setLocation(BmobGeoPoint location) {
        this.location = location;
    }

    public boolean getSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

}
