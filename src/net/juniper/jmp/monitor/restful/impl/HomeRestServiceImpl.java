package net.juniper.jmp.monitor.restful.impl;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import net.juniper.jmp.monitor.mo.home.HomeInfo;
import net.juniper.jmp.monitor.mo.home.NavType;
import net.juniper.jmp.monitor.restful.HomeRestService;
import net.juniper.jmp.monitor.services.NavTypeService;
import net.juniper.jmp.monitor.sys.SessionBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomeRestServiceImpl implements HomeRestService{
   private Logger logger = LoggerFactory.getLogger(HomeRestServiceImpl.class);
   @Inject
   private NavTypeService navService;
   
   @Override
   public HomeInfo getHomeInfo() {
	   HomeInfo homeInfo = new HomeInfo();
	   try{
		   List<NavType> groupList = navService.getNavList();
		   homeInfo.setNavList(groupList);
		   
		   SessionBean sb = new SessionBean();
		   sb.setLoginDate(Calendar.getInstance().getTime());
		   sb.setUserCode("super");
		   sb.setUserId("super");
		   homeInfo.setSessionBean(sb);
		   return homeInfo;
	   }
	   catch(Throwable e){
		   logger.error(e.getMessage(), e);
		   return homeInfo;
	   }
	   
   } 
} 
