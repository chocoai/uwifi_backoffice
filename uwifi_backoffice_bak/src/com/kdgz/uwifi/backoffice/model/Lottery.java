package com.kdgz.uwifi.backoffice.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

@SuppressWarnings("serial")
public class Lottery  extends Model<Lottery> {

	public static Lottery dao = new Lottery();
	
	/**
	 * 分页显示营销活动列表信息
	 * @param pageNumber
	 * @param pageSize
	 * @param type [1:大转盘，2:刮刮卡，3:优惠券]
	 * @param businessid [店铺ID]
	 * @return
	 * @author jason
	 */
	public Page<Lottery> lotteryList(int pageNumber, int pageSize, int type,
			Integer businessid) {
		StringBuffer sql = new StringBuffer();
		sql.append("from lottery t where 1=1 and delflag = 0 ");
		if (type > 0) {
			sql.append(" and t.type = " + type);
		}
		if (businessid > 0) {
			sql.append(" and t.businessid = " + businessid);
		}
		sql.append(" order by id desc");
		StringBuffer select = new StringBuffer();
		select.append("select t.id,t.title,from_unixtime(t.startdate, '%Y-%m-%d') startdate, ");
		select.append("from_unixtime(t.enddate, '%Y-%m-%d') enddate,t.status ");
		Page<Lottery> lottery_list = paginate(pageNumber, pageSize, select.toString(), sql.toString());
		
		if(lottery_list.getTotalPage()>0 && pageNumber>lottery_list.getTotalPage()){
			lottery_list = paginate(1, pageSize, select.toString(), sql.toString());
		}
		return lottery_list;
	}
	
	/**
	 * 获取Lottery 对象
	 * @param id
	 * @return
	 * @author jason
	 */
	public Lottery getLotteryById(String id){
		StringBuffer select = new StringBuffer();
		select.append("select t.id,t.title,t.sttxt,t.awardinfo,t.info,t.aginfo,from_unixtime(t.startdate, '%Y-%m-%d') startdate, ");
		select.append("from_unixtime(t.enddate, '%Y-%m-%d') enddate,t.endtitle,t.endinfo,t.allpeople,t.canrqnums,t.daynums,t.buspassword,t.displayjpnums,t.status,t.type ");
		select.append("from lottery t where t.id="+id);
		
		Lottery lottery = dao.findFirst(select.toString());
		return lottery;
	}
	
	/**
	 * 带分页的中奖信息
	 * @param pageNumber
	 * @param pageSize
	 * @param lotteryid
	 * @return
	 */
	public Page<Record> lotteryRecordList(int pageNumber, int pageSize,Integer lotteryid) {
		StringBuffer sql = new StringBuffer();
		sql.append("from lotteryrecord a,cuser b where a.cuserid=b.id and islottery=1 and a.lotteryid="+lotteryid);
		sql.append(" order by a.id desc");
		StringBuffer select = new StringBuffer();
		select.append("select a.sn,a.prizeno,a.exchanged,from_unixtime(a.exchangedtime) exchangedtime,");
		select.append("b.phoneno,from_unixtime(a.time) time ");
		Page<Record> lotteryRecordlist = Db.paginate(pageNumber, pageSize, select.toString(), sql.toString());
		return lotteryRecordlist;
	}
	
	public List<Record> lotteryRecordList(Integer lotteryid){
		StringBuffer select = new StringBuffer();
		select.append("select a.sn,a.prizeno,a.exchanged,from_unixtime(a.exchangedtime) exchangedtime,");
		select.append("b.phoneno,from_unixtime(a.time) time ");
		select.append(" from lotteryrecord a,cuser b where a.cuserid=b.id and islottery=1 and a.lotteryid="+lotteryid);
		select.append(" order by a.id desc");
		
		List<Record> list = Db.find(select.toString());
		return list;
	}
	
	/**
	 * 通过商家id获取活动列表
	 * @param businessid
	 * @author allen
	 * @return
	 */
	public List<Lottery> getLotteryListByBusinessid(Integer businessid){
		
		List<Lottery> list = dao.find("select * from lottery where delflag = 0 and businessid = "+businessid + " order by addtime desc");
		return list;
	}
}
