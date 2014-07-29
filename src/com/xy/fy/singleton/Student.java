package com.xy.fy.singleton;

public class Student {
	private Integer account;// 账号，主键,最长十位
	private String password;// 密码


	public Integer getAccount() {
		return account;
	}

	public void setAccount(Integer account) {
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
		return "Student [account=" + account + ", password=" + password+"]";
	}

}
