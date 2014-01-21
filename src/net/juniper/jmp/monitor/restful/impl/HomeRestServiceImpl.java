package net.juniper.jmp.monitor.restful.impl;
import java.util.Calendar;
import java.util.List;

import net.juniper.jmp.core.locator.SpringWebContextHelper;
import net.juniper.jmp.monitor.mo.home.HomeInfo;
import net.juniper.jmp.monitor.mo.home.NavType;
import net.juniper.jmp.monitor.restful.HomeRestService;
import net.juniper.jmp.monitor.services.NavTypeService;
import net.juniper.jmp.monitor.sys.SessionBean;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
@Service(value="net.juniper.jmp.monitor.restful.HomeRestService")
public class HomeRestServiceImpl implements HomeRestService{
   private Logger logger = Logger.getLogger(HomeRestServiceImpl.class);
   
   @Override
   public HomeInfo getHomeInfo() {
	   HomeInfo homeInfo = new HomeInfo();
	   try{
		   List<NavType> groupList = SpringWebContextHelper.getService(NavTypeService.class).getNavList();
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
