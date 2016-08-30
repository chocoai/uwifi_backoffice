package com.kdgz.uwifi.backoffice.model;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

@SuppressWarnings("serial")
public class Bwlist extends Model<Bwlist> {

	public static Bwlist dao = new Bwlist();

	/**
	 * 获取列表
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Bwlist> pageinfoList(int pageNumber, int pageSize, String acid,
			String whtype, String controltype, String businessid) {

		StringBuffer sql = new StringBuffer();
		if (StrKit.notBlank(businessid)) {
			sql.append("from bwlist b where b.acid in (select acid from acinfo where businessid in ('"
					+ businessid + "')  )");
		} else {
			sql.append("from bwlist b where 1=1");
		}

		if (StrKit.notBlank(acid)) {
			sql.append(" and acid like '%" + acid + "%' ");
		}
		if (StrKit.notBlank(whtype)) {
			if (Integer.valueOf(whtype) > -1) {
				sql.append(" and whtype = " + whtype);
			}
		}
		if (StrKit.notBlank(controltype)) {
			if (Integer.valueOf(controltype) > -1) {
				sql.append(" and controltype = " + controltype);
			}
		}
		sql.append(" order by id desc");
		Page<Bwlist> acinfo = paginate(pageNumber, pageSize, "select * ",
				sql.toString());
		return acinfo;
	}

	/**
	 * 获取列表
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public Page<Record> pageinfoList(int pageNumber, int pageSize, int whtype,
			int[] controltype, String businessid, String businessName) {

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT");
		selectSql.append("	a.id AS id,");
		selectSql.append("	a.controlvalue AS device,");
		selectSql.append("	a.controltype AS type,");
		selectSql.append("	b.busname AS businessName,");
		selectSql.append("	a.comment AS comment");

		StringBuilder fromSql = new StringBuilder();
		fromSql.append(" FROM");
		fromSql.append("	bwlist a");
		fromSql.append("	INNER JOIN businessinfo b ON a.businessid = b.id ");
		fromSql.append("	WHERE 1=1");
		
		if (StringUtils.isBlank(businessid)) {
			fromSql.append(" and a.businessid = 0 ");
		} else {
			fromSql.append(" and a.businessid in (" + businessid + ")");
		}

		if (StringUtils.isNotBlank(businessName)) {
			fromSql.append(" and b.busname like '%" + businessName + "%' ");
		}

		if (whtype > -1) {
			fromSql.append(" and a.whtype = " + whtype);
		}
		if (controltype.length > 0) {
			for (int i = 0; i < controltype.length; i++) {
				if (i == 0) {
					fromSql.append(" and (a.controltype = " + controltype[i]);
				} else {
					fromSql.append(" or a.controltype = " + controltype[i]);
				}

			}
			fromSql.append(" )");
		}
		fromSql.append(" order by a.id desc");
		Page<Record> bwList = Db.paginate(pageNumber, pageSize,
				selectSql.toString(), fromSql.toString());
		//added by yan 
		if(bwList.getTotalPage()>0 && pageNumber>bwList.getTotalPage()){
			bwList = Db.paginate(1, pageSize,
					selectSql.toString(), fromSql.toString());
		}
		return bwList;
	}

	/**
	 * 获取黑白名单数量
	 * 
	 * @return
	 */
	public Record getWhiteListCount(String businessid) {
		StringBuilder sb = new StringBuilder();
		sb.append("select (select count(1) from bwlist where whtype = 0 and controltype in (2,3)");
		if (StringUtils.isNotBlank(businessid)) {
			sb.append(" and businessid in (" + businessid + ") ");
		}
		sb.append(") as whiteListCount,");
		sb.append("(select count(1) from bwlist where whtype = 1 and controltype in (2,3)");
		if (StringUtils.isNotBlank(businessid)) {
			sb.append(" and businessid in (" + businessid + ") ");
		}
		sb.append(") as blackListCount,");
		sb.append("(select count(1) from bwlist where whtype = 0 and controltype in (1)");
		if (StringUtils.isNotBlank(businessid)) {
			sb.append(" and businessid in (" + businessid + ") ");
		}
		sb.append(") as domainListCount");
		Record record = Db.findFirst(sb.toString());

		return record;
	}

	/**
	 * 验证是否已经存在的黑白名单
	 * 
	 * @param whtype
	 * @param controltype
	 * @param businessid
	 * @return
	 */
	public long countExistBwlist(int whtype, int controltype,
			String controlval, int businessid, String id) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT count(1) From bwlist where controltype = ? and controlvalue =? and businessid = ?");
		if(StringUtils.isNotEmpty(id)) {
			sb.append(" and id <> " + id);
		}
		
		long count = Db
				.queryLong(
						sb.toString(),
						new Object[] { controltype, controlval,
								businessid });

		return count;

	}
	
	/**
	 * 通过商家id和mac地址查询黑白名单信息
	 * @param businessid
	 * @param mac
	 * @return
	 */
	public Bwlist selectBwlistBybusinessidAndControlvalue(int businessid, String controlvalue) {
		
		StringBuffer sql = new StringBuffer();
		sql.append("select * from bwlist where 1=1 ");
		if(businessid > 0){
			sql.append(" and businessid = " + businessid );
		}
		if(StrKit.notBlank(controlvalue)){
			sql.append(" and controlvalue = '"+controlvalue+"' ");
		}
		sql.append(" order by id desc");
		Bwlist bwlist = dao.findFirst(sql.toString());
		return bwlist;

	}

}
