package com.xy.fy.singleton;

public class Message {
    private int msgId;
    private int collegeId;// ��ѧIdҲ�����
    private int account;// �˺ţ��������
    private String nickname;// �ǳ�
    private String headPhoto;// �û�ͷ��
    private int kind;// ��Ϣ����
    private String text;// �ı���Ϣ
    private String larPic;// СͼƬ
    private String smaPic;// ͼƬ
    private String time;// ʱ��,֮����Ū��String������Ϊ����ʾ�ķ���
    private String date;// ����,֮����Ū��String������Ϊ����ʾ�ķ���
    private int praNum;// �޸���
    private int comNum;// ���۸���
    private int colNum;// �ղظ���

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadPhoto() {
        return headPhoto;
    }

    public void setHeadPhoto(String headPhoto) {
        this.headPhoto = headPhoto;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public int getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(int collegeId) {
        this.collegeId = collegeId;
    }

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLarPic() {
        return larPic;
    }

    public void setLarPic(String larPic) {
        this.larPic = larPic;
    }

    public String getSmaPic() {
        return smaPic;
    }

    public void setSmaPic(String smaPic) {
        this.smaPic = smaPic;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPraNum() {
        return praNum;
    }

    public void setPraNum(int praNum) {
        this.praNum = praNum;
    }

    public int getComNum() {
        return comNum;
    }

    public void setComNum(int comNum) {
        this.comNum = comNum;
    }

    public int getColNum() {
        return colNum;
    }

    public void setColNum(int colNum) {
        this.colNum = colNum;
    }

    @Override
    public String toString() {
        return "Message [msgId=" + msgId + ", collegeId=" + collegeId
                + ", account=" + account + ", nickname=" + nickname
                + ", headPhoto=" + headPhoto + ", kind=" + kind + ", text="
                + text + ", larPic=" + larPic + ", smaPic=" + smaPic
                + ", time=" + time + ", date=" + date + ", praNum=" + praNum
                + ", comNum=" + comNum + ", colNum=" + colNum + "]";
    }

}
