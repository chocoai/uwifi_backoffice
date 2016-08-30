package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.kdgz.uwifi.backoffice.constant.Constants;

@SuppressWarnings("serial")
public class Operatorlog extends Model<Operatorlog> {
	
	public static Operatorlog dao = new Operatorlog();

	/**
	 * 分页查询操作记录
	 * @param pageNumber
	 * @param pageSize
	 * @param queryCondition
	 * @return
	 */
	public Page<Operatorlog> operatorlogList(int pageNumber, int pageSize, String queryCondition, String userRole, String userid, String province, String city) {
		StringBuffer select = new StringBuffer();
		select.append("select a.*,b.addtime ");
		StringBuffer sql = new StringBuffer();
		sql.append("from operatorlog a left join userinfo b on a.userid = b.id where 1=1  ");
		
		if(!userRole.equals(Constants.ROLE_ADMIN)){
			sql.append(" and (b.pid="+userid+" or b.pid in (select id from userinfo where pid="+userid+") )");
		}
//		if(userRole.equals(Constants.ROLE_ADMIN)){
//			sql.append(" and a.roleid in ("+Constants.ROLE_GOLDCHANNEL+","+Constants.ROLE_SILVERCHANNEL+","+Constants.ROLE_BUSINESSUSER+")");
//		}
//		if(userRole.equals(Constants.ROLE_GOLDCHANNEL)){
//			sql.append(" and a.roleid in ("+Constants.ROLE_SILVERCHANNEL+","+Constants.ROLE_BUSINESSUSER+")");
//			sql.append(" and b.province= '"+province+"' and b.city='"+city+"'");
//		}
//		if(userRole.equals(Constants.ROLE_SILVERCHANNEL)){
//			sql.append(" and a.roleid in ("+Constants.ROLE_BUSINESSUSER+")");
//			sql.append(" and b.province= '"+province+"' and b.city='"+city+"'");
//		}
//		if(userRole.equals(Constants.ROLE_BUSINESSUSER)){
//			sql.append(" and a.roleid in ("+Constants.ROLE_BUSINESSUSER+")");
//			sql.append(" and b.province= '"+province+"' and b.city='"+city+"'");
//		}
		if(queryCondition.length() > 0){
			sql.append(" and a.username like '%" + queryCondition + "%' ");
		}
		sql.append(" order by a.operatortime desc");
		Page<Operatorlog> operatorlogList = paginate(pageNumber, pageSize, select.toString(), sql.toString());
		if(operatorlogList.getTotalPage()>0 && pageNumber>operatorlogList.getTotalPage()){
			operatorlogList = paginate(1, pageSize, select.toString(),
					sql.toString());
		}
		return operatorlogList;
	}
	
	public List<Operatorlog> getOperatorlogList(String userRole, String userid, String province, String city){
		StringBuffer select = new StringBuffer();
		select.append("select a.*,TIMESTAMPDIFF(day,DATE_FORMAT(now(),'%Y-%m-%d'),DATE_FORMAT(b.endtime,'%Y-%m-%d'))+1 as remind,b.starttime as kssj,b.endtime as jssj ");
		select.append(" from operatorlog a left join userinfo b on a.userid = b.id where a.logtype=1 and a.delflag=0 and b.status=1 and b.checkstatus=1");
		select.append(" and TIMESTAMPDIFF(day,DATE_FORMAT(now(),'%Y-%m-%d'),DATE_FORMAT(b.endtime,'%Y-%m-%d'))>=0 ");
		select.append(" and TIMESTAMPDIFF(day,DATE_FORMAT(now(),'%Y-%m-%d'),DATE_FORMAT(b.endtime,'%Y-%m-%d'))<=14 ");
		if(userRole.equals(Constants.ROLE_ADMIN)){
			select.append(" and a.roleid= "+Constants.ROLE_BUSINESSUSER);
		}
		if(userRole.equals(Constants.ROLE_GOLDCHANNEL) || userRole.equals(Constants.ROLE_SILVERCHANNEL)){
			select.append(" and (b.pid="+userid+" or b.pid in (select id from userinfo where pid="+userid+") )");
		}
//		if(userRole.equals(Constants.ROLE_SILVERCHANNEL)){
//			select.append(" and a.userid in (select b.id from userinfo b where b.roleid="+Constants.ROLE_SILVERCHANNEL+")");
//		}
		//select.append(" and b.province= '"+province+"' and b.city='"+city+"'");
		select.append(" order by a.operatortime desc");
		List<Operatorlog> list = dao.find(select.toString());
		return list;
	}
	
	public Operatorlog getOperatorinfoByUserid(Integer userid){
		
		Operatorlog operatorlog = dao.findFirst("select * from operatorlog where userid = "+userid);
		return operatorlog;
		
	}
	
	
	
}
