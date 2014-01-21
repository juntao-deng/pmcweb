package net.juniper.jmp.monitor.mo.home;

import java.util.List;

import net.juniper.jmp.monitor.sys.SessionBean;
/**
 * 
 * @author juntaod
 */
public class HomeInfo {
	private List<NavType> navList;
	private SessionBean sessionBean;
	public List<NavType> getNavList() {
		return navList;
	}
	public void setNavList(List<NavType> navList) {
		this.navList = navList;
	}
	public SessionBean getSessionBean() {
		return sessionBean;
	}
	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}
}
