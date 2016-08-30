package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.kdgz.uwifi.backoffice.constant.Constants;

@SuppressWarnings("serial")
public class Acinfo extends Model<Acinfo> {

	public static final Acinfo dao = new Acinfo();

	/**
	 * 获取列表
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Acinfo> pageinfoList(int pageNumber, int pageSize,
			String busname, String construtname, String businessid) {
		StringBuffer sql = new StringBuffer();
		sql.append("from acinfo a, businessinfo b, construtinfo c, acbrand d, actypeinfo e ");
		sql.append("where a.businessid = b.id and a.construtid = c.id and a.acbrandid = d.id and a.actypeid = e.id ");
		if (StrKit.notBlank(busname)) {
			sql.append(" and b.busname like '%" + busname + "%' ");
		}
		if (StrKit.notBlank(construtname)) {
			sql.append(" and c.construtname like '%" + construtname + "%'");
		}
		if (StrKit.notBlank(businessid)) {
			sql.append(" and b.id in (" + businessid + ")");
		}
		sql.append(" order by a.id desc");
		Page<Acinfo> acinfo = paginate(pageNumber, pageSize,
				"select a.id, a.acid, a.businessid, a.acos, d.acbrandname, e.typename,"
						+ " a.eqptssid, c.construtname, b.busname",
				sql.toString());
		return acinfo;
	}

	/**
	 * 获取我的店铺路由器列表
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Acinfo> pageAcInfoList(int pageNumber, int pageSize,
			String busname, String acid, String businessid) {
		StringBuffer sql = new StringBuffer();
		sql.append("from acinfo a, businessinfo b, acbrand d, actypeinfo e ");
		sql.append("where a.businessid = b.id and a.acbrandid = d.id and a.actypeid = e.id ");
		if (StrKit.notBlank(busname)) {
			sql.append(" and b.busname like '%" + busname + "%' ");
		}
		if (StrKit.notBlank(acid)) {
			sql.append(" and a.acid like '%" + acid + "%'");
		}
		if (StrKit.notBlank(businessid)) {
			sql.append(" and b.id in (" + businessid + ")");
		} else {
			sql.append(" and b.id = 0");
		}
		sql.append(" order by a.id desc");
		Page<Acinfo> acinfo = paginate(pageNumber, pageSize,
				"select a.id, a.acid, a.businessid, a.acos, d.acbrandname, e.typename,"
						+ " a.eqptssid, b.busname", sql.toString());
		
		//added by yan 
		if(acinfo.getTotalPage()>0 && pageNumber>acinfo.getTotalPage()){
			acinfo = paginate(1, pageSize,
					"select a.id, a.acid, a.businessid, a.acos, d.acbrandname, e.typename,"
							+ " a.eqptssid, b.busname", sql.toString());
		}
		return acinfo;
	}

	/**
	 * 获取Ac信息表，此方法只用到获取ACId
	 * 
	 * @return
	 */
	public List<Acinfo> selectAcidList(String businessid) {

		StringBuffer sql = new StringBuffer();
		sql.append("select a.id, a.acid from acinfo a ");
		if (StrKit.notBlank(businessid)) {
			sql.append(" where 1=1 and a.businessid in (" + businessid + ")");
		}else{
			sql.append(" where 1=2 ");
		}
		sql.append(" order by a.id desc");
		List<Acinfo> list = dao.find(sql.toString());
		return list;

	}

	/**
	 * 查找ac信息通过ACId
	 * 
	 * @param acid
	 * @return
	 */
	public Acinfo selectAcinfoByAcId(String acid) {

		Acinfo acinfo = dao.findFirst("select * from acinfo where acid = '"
				+ acid + "' ");
		return acinfo;

	}

	/**
	 * 判断acid唯一性
	 */
	public Acinfo selectAcinfoByIdAndAcId(int id, String acid) {

		Acinfo acinfo = dao.findFirst("select * from acinfo where id <> '" + id
				+ "' and  acid = '" + acid + "' ");
		return acinfo;
	}

	/**
	 * 获取店铺的路由器数量
	 * 
	 * @param userId
	 * @return
	 */
	public long countShopDeviceNum(int busId) {

		long count = Db
				.queryLong(
						"SELECT count(1) AS count FROM acinfo WHERE acinfo.businessid = ?",
						new int[] { busId });

		return count;
	}
	
	/**
	 * 获取一个路由器acid
	 * @param businessId
	 * @return
	 * @author jason
	 */
	public Acinfo getAcIdByBusid(Integer businessId) {

		Acinfo acinfo = dao.findFirst("select * from acinfo where businessid = "+ businessId + " limit 1 ");
		return acinfo;

	}
	
	public Page<Acinfo> pageinfoList5(int pageNumber, int pageSize, String roleid,
			String acbh, Integer channlid, Integer isonline, String userid) {
		StringBuffer sql = new StringBuffer();
		sql.append("from acinfo a, businessinfo b, acstatetemp c, userbiz d, userinfo e ");
		sql.append("where a.businessid = b.id and a.acid = c.acid and b.id=d.businessid and d.userid=e.id ");
		if (StrKit.notBlank(acbh)) {
			sql.append(" and a.acid like '%" + acbh + "%' ");
		}
		if (channlid != null && channlid > -1) {
			sql.append(" and (e.pid="+channlid+" or e.pid in (select id from userinfo where pid="+channlid+") )");
		}else{
			if(!roleid.equals(Constants.ROLE_ADMIN)){
				sql.append(" and (e.pid="+userid+" or e.pid in (select id from userinfo where pid="+userid+") )");
			}
		}
		if (isonline != null && isonline > -1) {
			if(isonline==1){
				sql.append(" and unix_timestamp(now()) - unix_timestamp(c.lastpingtime)<=5*60 ");
			}else{
				sql.append(" and unix_timestamp(now()) - unix_timestamp(c.lastpingtime)>5*60 ");
			}
		}
//		if(roleid.equals(Constants.ROLE_ADMIN)){
//			sql.append(" and e.roleid in ("+Constants.ROLE_ADMIN+","+Constants.ROLE_GOLDCHANNEL+","+Constants.ROLE_SILVERCHANNEL+")");
//		}
//		if(roleid.equals(Constants.ROLE_GOLDCHANNEL)){
//			sql.append(" and e.roleid in ("+Constants.ROLE_GOLDCHANNEL+","+Constants.ROLE_SILVERCHANNEL+")");
//		}
//		if(roleid.equals(Constants.ROLE_SILVERCHANNEL)){
//			sql.append(" and e.roleid = "+Constants.ROLE_SILVERCHANNEL);
//		}
		sql.append(" order by c.lastpingtime desc");
		Page<Acinfo> acinfo = paginate(pageNumber, pageSize,
				"select a.id, a.acid, a.businessid, b.busname, a.addtime,"
						+" case when unix_timestamp(now()) - unix_timestamp(c.lastpingtime)<5*60 then now() else c.lastpingtime end lasttime,"
						+" case when unix_timestamp(now()) - unix_timestamp(c.lastpingtime)<5*60 then '在线' else '离线' end online," 
						+" e.id,e.pid,e.pname ",
				sql.toString());
		return acinfo;
	}

}
