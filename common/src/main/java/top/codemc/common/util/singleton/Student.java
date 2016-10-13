package top.codemc.common.util.singleton;

public class Student {
    private String account;// �˺ţ�����,�ʮλ
    private String password;// ����
    private String name;// ����

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Student [account=" + account + ", password=" + password + "]";
    }

}
