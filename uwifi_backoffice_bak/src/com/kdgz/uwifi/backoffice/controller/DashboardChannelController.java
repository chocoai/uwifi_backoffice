package com.kdgz.uwifi.backoffice.controller;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.ChannelInterceptor;
import com.kdgz.uwifi.backoffice.model.Businessinfo;
import com.kdgz.uwifi.backoffice.model.Operatorlog;

/**
 * 工作台
 * @author allen
 * 
 */
@Before(ChannelInterceptor.class)
public class DashboardChannelController extends BaseController {

	public void index() {

		String userRole = getSessionAttr("roleid");
		String userid = getSessionAttr("userid")+"";
		String province = getSessionAttr("province");
		String city = getSessionAttr("city");
		String queryCondition = getPara("channelORbusname","");
		
		//数据统计
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT");
		sb.append(" SUM(CASE WHEN roleid="+Constants.ROLE_ADMIN+" THEN 1 ELSE 0 END) AS 'admin', ");
		sb.append(" SUM(CASE WHEN roleid="+Constants.ROLE_GOLDCHANNEL+" THEN 1 ELSE 0 END) AS 'goldchannel', ");
		sb.append(" SUM(CASE WHEN roleid="+Constants.ROLE_SILVERCHANNEL+" THEN 1 ELSE 0 END) AS 'silverchannel', ");
		sb.append(" SUM(CASE WHEN roleid="+Constants.ROLE_BUSINESSUSER+" THEN 1 ELSE 0 END) AS 'businessuser' ");
		sb.append("FROM userinfo where delflag=0 and status=1 and checkstatus=1 ");
		if(!userRole.equals(Constants.ROLE_ADMIN)){
			//sb.append("and province='"+province+"' and city='"+city+"'");
			sb.append(" and (pid="+userid+" or pid in (select id from userinfo where pid="+userid+") )");
		}
		Record record = Db.findFirst(sb.toString());
		if (record != null) {
			setAttr("goldchannel", record.get("goldchannel"));
			setAttr("silverchannel", record.get("silverchannel"));
			setAttr("businessuser", record.get("businessuser"));
		}
		long onlineDeviceCount=0;
		onlineDeviceCount = Businessinfo.dao.countOnlineDeviceNum5(userRole, userid);
		setAttr("onlineDeviceCount", onlineDeviceCount);
		setAttr("userRole", userRole);
		
		//商家过期提醒
		List<Operatorlog> operatorList = Operatorlog.dao.getOperatorlogList(userRole, userid, province, city);
		setAttr("operatorList", operatorList);
		
		//渠道监控
		setAttr("channelORbusname", queryCondition);
		setAttr("operatorlogPage",Operatorlog.dao.operatorlogList(getParaToInt(0, 1), 10, queryCondition, userRole, userid, province, city));

		setAttr("admin", Constants.ROLE_ADMIN);
		setAttr("gold", Constants.ROLE_GOLDCHANNEL);
		setAttr("silver", Constants.ROLE_SILVERCHANNEL);
		setAttr("business", Constants.ROLE_BUSINESSUSER);
		flashRender("/admin/site/channel_index.html");
	}
	
	public void moreInfo(){
		String userRole = getSessionAttr("roleid");
		String userid = getSessionAttr("userid")+"";
		String province = getSessionAttr("province");
		String city = getSessionAttr("city");
		String queryCondition = getPara("channelORbusname","");
		setAttr("channelORbusname", queryCondition);
		setAttr("operatorlogPage",Operatorlog.dao.operatorlogList(getParaToInt(0, 1), 10, queryCondition, userRole, userid, province, city));

		render("/admin/channel/homepage/operatorLog.html");
	}
	
}
