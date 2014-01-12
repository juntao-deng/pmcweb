package net.juniper.jmp.monitor.sys;

import java.util.Date;

public class SessionBean {
	private String userId;
	private String userCode;
	private Date loginDate;
	private BusiSys busiSys;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public Date getLoginDate() {
		return loginDate;
	}
	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}
	public BusiSys getBusiSys() {
		return busiSys;
	}
	public void setBusiSys(BusiSys busiSys) {
		this.busiSys = busiSys;
	}
}
