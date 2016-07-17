package com.xy.fy.singleton;

public class Comment {
    private String name;// �ظ�������
    private String date;// �ظ�����
    private String time;// �ظ�ʱ��
    private String content;// �ظ�����

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Comment [name=" + name + ", date=" + date + ", time=" + time
                + ", content=" + content + "]";
    }

}
