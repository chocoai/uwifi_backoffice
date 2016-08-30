package com.kdgz.uwifi.backoffice.model;

import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("serial")
public class Lotteryprize extends Model<Lotteryprize> {

	public static Lotteryprize dao = new Lotteryprize();
	
	/**
	 * 获取活动奖项对象
	 * @param id
	 * @param level
	 * @return
	 * @author jason
	 */
	public Lotteryprize getLotteryprize(String id, int level){
		StringBuffer select = new StringBuffer();
		select.append("select t.prizename,t.prizenum ");
		select.append("from lotteryprize t where t.lotteryid="+id+" and t.level="+level);
		
		Lotteryprize lotteryprize = dao.findFirst(select.toString());;
		
		return lotteryprize;
	}
	
}
