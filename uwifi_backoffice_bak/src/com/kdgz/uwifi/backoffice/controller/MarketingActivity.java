package com.kdgz.uwifi.backoffice.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.mail.internet.NewsAddress;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.jfinal.aop.Before;
import com.jfinal.core.JFinal;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.ShopLayoutInterceptor;
import com.kdgz.uwifi.backoffice.model.Lottery;
import com.kdgz.uwifi.backoffice.model.Lotteryprize;
import com.kdgz.uwifi.backoffice.model.Prize;
import com.kdgz.uwifi.backoffice.util.DateUtil;
import com.kdgz.uwifi.backoffice.util.StringUtil;

@Before(ShopLayoutInterceptor.class)
public class MarketingActivity extends BaseController {
	
	static final int LEVEL_ONE = 1;//一等奖
	static final int LEVEL_TWO = 2;//二等奖
	static final int LEVEL_THREE = 3;//三等奖
	static final int LEVEL_FOUR = 4;//四等奖
	static final int LEVEL_FIVE = 5;//五等奖
	static final int LEVEL_SIX = 6;//六等奖

	ResourceBundle bundle = ResourceBundle.getBundle("common");
	/**
	 * 优惠券列表
	 */
	public void index() {

		int businessid = getSessionAttr("shopid");
		String mark = getPara("mark","");
		int pageNum = 0;
		if(mark.indexOf("@") > -1){
			String[] args = mark.split("@");
			mark = args[0];
			pageNum = Integer.parseInt(args[1].replace("-", ""));
		}else{
			pageNum = getParaToInt(0,1);
		}
		int type = 0;
		String btnName = "";
		if("yhq".equals(mark)){//优惠券
			type = 3;
			btnName = "优惠券";
			setAttr("lottery", Lottery.dao.lotteryList(pageNum, 10, type, businessid));
		}
		if("ggk".equals(mark)){//刮刮卡
			type = 2;
			btnName = "刮刮卡";
			setAttr("lottery", Lottery.dao.lotteryList(pageNum, 10, type, businessid));
		}
		if("xydzp".equals(mark)){ //幸运大转盘
			type = 1;
			btnName = "大转盘";
			setAttr("lottery", Lottery.dao.lotteryList(pageNum, 10, type, businessid));
		}
		if("zjd".equals(mark)){ //砸金蛋
			type = 5;
			btnName = "砸金蛋";
			setAttr("lottery", Lottery.dao.lotteryList(pageNum, 10, type, businessid));
		}
		if("xysgj".equals(mark)){ //幸运水果机
			type = 4;
			btnName = "幸运水果机";
			setAttr("lottery", Lottery.dao.lotteryList(pageNum, 10, type, businessid));
		}
		setAttr("btnName", btnName);
		setAttr("mark", mark);
		flashRender("/admin/marketing_activity/coupon/couponList.html");
	}
	
	/**
	 * 新增优惠券
	 */
	public void addCoupon(){
		int businessid = getSessionAttr("shopid");
		String mark = getPara("mark","");
		if(getRequest().getMethod().equals("POST")) {
			Lottery lottery = new Lottery();
			Lottery param = getModel(Lottery.class);
			String startdate = getPara("startdate","");
			String enddate = getPara("enddate","");
			
			int starttime = DateUtil.DateToInt(startdate);
			int endtime = DateUtil.DateToInt(enddate);
			
			String title = param.getStr("title");
			lottery.set("title", title);	//活动名称
			lottery.set("sttxt", param.getStr("sttxt"));	// 简介
			lottery.set("info", param.getStr("info"));		//活动说明
			lottery.set("aginfo", param.getStr("aginfo"));	//重复抽奖提示
			
			lottery.set("startdate", starttime);
			lottery.set("enddate", endtime);
			
			lottery.set("endtitle", param.getStr("endtitle"));	//活动结束公告标题
			lottery.set("endinfo", param.getStr("endinfo"));	// 活动结束说明
			
			lottery.set("allpeople", param.getInt("allpeople"));	//预计活动的人数
			lottery.set("canrqnums", param.getInt("canrqnums"));	//个人闲置抽奖次数
			lottery.set("daynums", param.getInt("daynums"));	//每天最多抽奖次数
			lottery.set("buspassword", param.getStr("buspassword"));	//商家兑奖密码
			lottery.set("displayjpnums", param.getInt("displayjpnums"));	//是否显示奖品数量
			
			lottery.set("businessid", businessid);
			
			int type=0;
			if("yhq".equals(mark)){//优惠券
				type = 3;
			}
			if("ggk".equals(mark)){//刮刮卡
				type = 2;
				lottery.set("awardinfo", param.getStr("awardinfo"));	//兑奖信息
			}
			if("xydzp".equals(mark)){	//幸运大转盘
				type = 1;
				lottery.set("awardinfo", param.getStr("awardinfo"));	//兑奖信息
			}
			if("zjd".equals(mark)){	//砸金蛋
				type = 5;
				lottery.set("awardinfo", param.getStr("awardinfo"));	//兑奖信息
			}
			if("xysgj".equals(mark)){	//幸运水果机
				type = 4;
				lottery.set("awardinfo", param.getStr("awardinfo"));	//兑奖信息
			}
			lottery.set("type", type);		//活动类型
			
			
			long time = System.currentTimeMillis();
			time = time / 1000;
			lottery.set("addtime", time);
			lottery.set("updatetime", time);
			
			int status = 0;
			if(time >= starttime){
				status = 1;
			}
			lottery.set("status", status);//状态：[1-开始，0-结束]
			lottery.set("delflag", 0);
			
			//活动奖项信息
			Lotteryprize lotteryprizeParam = getModel(Lotteryprize.class);
			String prizename = lotteryprizeParam.getStr("prizename");	//奖品名称
			Integer prizenum = lotteryprizeParam.getInt("prizenum");	//奖品数量
			
			String prizename2 = getPara("prizename2");
			Integer prizenum2 = getParaToInt("prizenum2");
			
			String prizename3 = getPara("prizename3");
			Integer prizenum3 = getParaToInt("prizenum3");
			
			String prizename4 = getPara("prizename4");
			Integer prizenum4 = getParaToInt("prizenum4");

			String prizename5 = getPara("prizename5");
			Integer prizenum5 = getParaToInt("prizenum5");

			String prizename6 = getPara("prizename6");
			Integer prizenum6 = getParaToInt("prizenum6");		
			
			Prize prize = new Prize(prizename, prizenum, prizename2, prizenum2, prizename3, prizenum3,
					prizename4,prizenum4, prizename5, prizenum5, prizename6, prizenum6);
			
			//查询是否已有同名的活动
			Lottery oldLottery = Lottery.dao.findFirst("select * from lottery where title =? and type=? and businessid=?", title,type,businessid);// and status=1
			if(oldLottery!=null && oldLottery.getStr("title")!=null){ //活动名重复不允许重复添加
				Data rst = new Data();
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("活动名称不能重复,请重新输入！");
				setAttr("result",rst);
				setAttr("startTime",startdate);
				setAttr("endTime",enddate);
				initData(mark,lottery,"add",prize);//返回新增页面并填充数据
				return;
			}
			
			Data rst = new Data();
			if(lottery.save()){
				savePrize(lottery, time,  prize);
				rst.setStatus(Constants.OPERATION_SUCCEED);
				if("yhq".equals(mark)){//优惠券
					rst.setMsg("新增优惠券成功！");
				}
				if("ggk".equals(mark)){//刮刮卡
					rst.setMsg("新增刮刮卡成功！");
				}
				if("xydzp".equals(mark)){	//幸运大转盘
					rst.setMsg("新增幸运大转盘成功！");
				}
				if("zjd".equals(mark)){	//砸金蛋
					rst.setMsg("新增砸金蛋成功！");
				}
				if("xysgj".equals(mark)){	//幸运水果机
					rst.setMsg("新增幸运水果机成功！");
				}
			}
			setFlashData(rst);
			redirect("/marketingActivity?mark="+mark);
		} else {
			String startTime = DateUtil.getday_yymmdd(new Date(),0);
			String endTime = DateUtil.getday_yymmdd(new Date(),30);;
			setAttr("startTime",startTime);
			setAttr("endTime",endTime);
			
			initData(mark,null,"add",null);
		}
	}
	
	/**
	 * 编辑优惠券
	 */
	public void editCoupon(){
		//int businessid = getSessionAttr("shopid");
		String mark = getPara("mark","");
		String id = getPara("id","");
		Lottery lottery = Lottery.dao.getLotteryById(id);
		Lotteryprize lotteryprize1 = Lotteryprize.dao.getLotteryprize(id,LEVEL_ONE);
		Lotteryprize lotteryprize2 = Lotteryprize.dao.getLotteryprize(id,LEVEL_TWO);
		Lotteryprize lotteryprize3 = Lotteryprize.dao.getLotteryprize(id,LEVEL_THREE);
		Lotteryprize lotteryprize4 = Lotteryprize.dao.getLotteryprize(id,LEVEL_FOUR);
		Lotteryprize lotteryprize5 = Lotteryprize.dao.getLotteryprize(id,LEVEL_FIVE);
		Lotteryprize lotteryprize6 = Lotteryprize.dao.getLotteryprize(id,LEVEL_SIX);
		
		int currentPage = getParaToInt("pageNo",1);//当前页码
		
		if(getRequest().getMethod().equals("POST")) {
			//Lottery lottery = new Lottery();
			Lottery param = getModel(Lottery.class);
			String startdate = getPara("startdate","").trim();
			String enddate = getPara("enddate","").trim();
			
			String title =  param.getStr("title");
			lottery.set("title", title);	//活动名称
			lottery.set("sttxt", param.getStr("sttxt"));	// 简介
			lottery.set("info", param.getStr("info"));		//活动说明
			lottery.set("aginfo", param.getStr("aginfo"));	//重复抽奖提示
			
			lottery.set("startdate", DateUtil.DateToInt(startdate));
			lottery.set("enddate", DateUtil.DateToInt(enddate));
			
			lottery.set("endtitle", param.getStr("endtitle"));	//活动结束公告标题
			lottery.set("endinfo", param.getStr("endinfo"));	// 活动结束说明
			
			
			lottery.set("allpeople", param.getInt("allpeople"));	//预计活动的人数
			lottery.set("canrqnums", param.getInt("canrqnums"));	//个人闲置抽奖次数
			lottery.set("daynums", param.getInt("daynums"));	//每天最多抽奖次数
			lottery.set("buspassword", param.getStr("buspassword"));	//商家兑奖密码
			lottery.set("displayjpnums", param.getInt("displayjpnums"));	//是否显示奖品数量
			
			if(!("yhq".equals(mark))){
				lottery.set("awardinfo", param.getStr("awardinfo"));
			}
			
			long time = System.currentTimeMillis();
			time = time / 1000;
			lottery.set("updatetime", time);
			
			//活动奖项信息
			Lotteryprize lotteryprizeParam = getModel(Lotteryprize.class);
			String prizename = lotteryprizeParam.getStr("prizename");	//奖品名称
			Integer prizenum = lotteryprizeParam.getInt("prizenum");	//奖品数量
			
			String prizename2 = getPara("prizename2");
			Integer prizenum2 = getParaToInt("prizenum2");
			
			String prizename3 = getPara("prizename3");
			Integer prizenum3 = getParaToInt("prizenum3");
			
			String prizename4 = getPara("prizename4");
			Integer prizenum4 = getParaToInt("prizenum4");

			String prizename5 = getPara("prizename5");
			Integer prizenum5 = getParaToInt("prizenum5");

			String prizename6 = getPara("prizename6");
			Integer prizenum6 = getParaToInt("prizenum6");		
			
			Prize prize = new Prize(prizename, prizenum, prizename2, prizenum2, prizename3, prizenum3,
					prizename4,prizenum4, prizename5, prizenum5, prizename6, prizenum6);
			
			
			//查询是否已有同名的活动
			Lottery oldLottery = Lottery.dao.findFirst("select * from lottery where " +
											"title =? and type=? and id!=? and businessid=?", title,lottery.getInt("type"),id,lottery.getInt("businessid"));// and status=1
			if(oldLottery!=null && oldLottery.getStr("title")!=null){ //活动名重复不允许重复添加
				Data rst = new Data();
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("活动名称不能重复,请重新输入！");
				setAttr("result",rst);
				lottery.set("startdate", startdate);
				lottery.set("enddate",enddate);
				setAttr("currentPage",currentPage);//设置当前页码
				initData(mark,lottery,"edit",prize);//返回新增页面并填充数据
				return;
			}
			
			Data rst = new Data();
			if(lottery.update()){
				//先删除再插入
				Db.update("delete from lotteryprize  where lotteryid = ?", id);
				
				savePrize(lottery, time, prize );
				rst.setStatus(Constants.OPERATION_SUCCEED);
				rst.setMsg("修改活动成功！");
			}else{
				rst.setStatus(Constants.OPERATION_FAILED);
				rst.setMsg("修改活动失败！");
			}
			setFlashData(rst);
			redirect("/marketingActivity/"+currentPage+"?mark="+mark);
		} else {
			setAttr("lotteryprize1", lotteryprize1);
			setAttr("lotteryprize2", lotteryprize2);
			setAttr("lotteryprize3", lotteryprize3);
			setAttr("lotteryprize4", lotteryprize4);
			setAttr("lotteryprize5", lotteryprize5);
			setAttr("lotteryprize6", lotteryprize6);
			setAttr("currentPage",currentPage);//设置当前页码
			initData(mark, lottery, "edit", null);
		}
	}
	
	/**
	 * 为add\edit页面准备数据
	 * @param mark
	 * @param lottery
	 * @param op
	 * @param prize
	 */
	private void initData(String mark,Lottery lottery,String op,Prize prize){
		String btnName = "";
		if("yhq".equals(mark)){//优惠券
			btnName = "优惠券";
		}
		if("ggk".equals(mark)){//刮刮卡
			btnName = "刮刮卡";
		}
		if("xydzp".equals(mark)){//幸运大转盘
			btnName = "幸运大转盘";
		}
		if("zjd".equals(mark)){//砸金蛋
			btnName = "砸金蛋";
		}
		if("xysgj".equals(mark)){//幸运水果机
			btnName = "幸运水果机";
		}
		setAttr("btnName",btnName);
		setAttr("mark",mark);
		setAttr("lottery", lottery);
		
		if(prize!=null){
			if(prize.getPrizename()!=null)
				setAttr("lotteryprize1", new Lotteryprize().set("prizename", prize.getPrizename()).set("prizenum", prize.getPrizenum()));
			if(prize.getPrizename2()!=null)
				setAttr("lotteryprize2", new Lotteryprize().set("prizename", prize.getPrizename2()).set("prizenum", prize.getPrizenum2()));
			if(prize.getPrizename3()!=null)
				setAttr("lotteryprize3", new Lotteryprize().set("prizename", prize.getPrizename3()).set("prizenum", prize.getPrizenum3()));
			if(prize.getPrizename4()!=null)
				setAttr("lotteryprize4", new Lotteryprize().set("prizename", prize.getPrizename4()).set("prizenum", prize.getPrizenum4()));
			if(prize.getPrizename5()!=null)
				setAttr("lotteryprize5", new Lotteryprize().set("prizename", prize.getPrizename5()).set("prizenum", prize.getPrizenum5()));
			if(prize.getPrizename6()!=null)
				setAttr("lotteryprize6", new Lotteryprize().set("prizename", prize.getPrizename6()).set("prizenum", prize.getPrizenum6()));
		}
		
		if("xydzp".equals(mark) || "zjd".equals(mark) || "xysgj".equals(mark)){
			
			render("/admin/marketing_activity/circle/"+op+"Circle.html");
			return;
		}
		
		render("/admin/marketing_activity/coupon/"+op+"Coupon.html");
	}

	private void savePrize(Lottery lottery, long time, Prize prize) {
		if(StrKit.notBlank(prize.getPrizename())){
			
			Lotteryprize lotteryprize = new Lotteryprize();
			lotteryprize.set("lotteryid", lottery.getInt("id"));
			lotteryprize.set("addtime", time);
			lotteryprize.set("updatetime", time);
			
			lotteryprize.set("prizename", prize.getPrizename());
			lotteryprize.set("prizenum", prize.getPrizenum());
			lotteryprize.set("level", 1);//一等奖
			lotteryprize.set("delflag", 0);
			lotteryprize.save();
		}
		
		if(StrKit.notBlank(prize.getPrizename2())){
			Lotteryprize lotteryprize2 = new Lotteryprize();
			lotteryprize2.set("lotteryid", lottery.getInt("id"));
			lotteryprize2.set("addtime", time);
			lotteryprize2.set("updatetime", time);
			
			lotteryprize2.set("prizename", prize.getPrizename2());
			lotteryprize2.set("prizenum", prize.getPrizenum2());
			lotteryprize2.set("level", 2);//二等奖
			lotteryprize2.set("delflag", 0);
			lotteryprize2.save();
		}
		
		if(StrKit.notBlank(prize.getPrizename3())){
			Lotteryprize lotteryprize3 = new Lotteryprize();
			lotteryprize3.set("lotteryid", lottery.getInt("id"));
			lotteryprize3.set("addtime", time);
			lotteryprize3.set("updatetime", time);
			
			lotteryprize3.set("prizename", prize.getPrizename3());
			lotteryprize3.set("prizenum", prize.getPrizenum3());
			lotteryprize3.set("level", 3);//三等奖
			lotteryprize3.set("delflag", 0);
			lotteryprize3.save();
			
		}
		if(StrKit.notBlank(prize.getPrizename4())){
			Lotteryprize lotteryprize4 = new Lotteryprize();
			lotteryprize4.set("lotteryid", lottery.getInt("id"));
			lotteryprize4.set("addtime", time);
			lotteryprize4.set("updatetime", time);
			
			lotteryprize4.set("prizename", prize.getPrizename4());
			lotteryprize4.set("prizenum", prize.getPrizenum4());
			lotteryprize4.set("level", 4);//四等奖
			lotteryprize4.set("delflag", 0);
			lotteryprize4.save();
			
		}
		if(StrKit.notBlank(prize.getPrizename5())){
			Lotteryprize lotteryprize5 = new Lotteryprize();
			lotteryprize5.set("lotteryid", lottery.getInt("id"));
			lotteryprize5.set("addtime", time);
			lotteryprize5.set("updatetime", time);
			
			lotteryprize5.set("prizename", prize.getPrizename5());
			lotteryprize5.set("prizenum", prize.getPrizenum5());
			lotteryprize5.set("level", 5);//五等奖
			lotteryprize5.set("delflag", 0);
			lotteryprize5.save();
			
		}
		if(StrKit.notBlank(prize.getPrizename6())){
			Lotteryprize lotteryprize6 = new Lotteryprize();
			lotteryprize6.set("lotteryid", lottery.getInt("id"));
			lotteryprize6.set("addtime", time);
			lotteryprize6.set("updatetime", time);
			
			lotteryprize6.set("prizename", prize.getPrizename6());
			lotteryprize6.set("prizenum", prize.getPrizenum6());
			lotteryprize6.set("level", 6);//六等奖
			lotteryprize6.set("delflag", 0);
			lotteryprize6.save();
			
		}
	}
	
	public void deleteCoupon(){
		String mark = getPara("mark","");
		String id = getPara("id","");
		
		int result = Db.update("update lotteryprize set delflag = 1 where lotteryid = ?", id);
		Data rst = new Data();
		if(-1 != result){
			Db.update("update lottery set delflag = 1 where id = ?", id);
			rst.setStatus(Constants.OPERATION_SUCCEED);
			rst.setMsg("删除活动成功！");
		}else{
			rst.setStatus(Constants.OPERATION_FAILED);
			rst.setMsg("删除活动失败！");
		}
		setAttr("mark", mark);
		setFlashData(rst);
		int currentPage = getParaToInt("pageNo",1);//获取当前页码
		redirect("/marketingActivity/"+currentPage+"?mark="+mark);
	}
	
	public void changeStatus(){
		String mark = getPara("mark","");
		String id = getPara("id","");
		String flage = getPara("flage","");
		Lottery lottery = Lottery.dao.getLotteryById(id);
		Data rst = new Data();
		rst.setStatus(Constants.OPERATION_SUCCEED);
		if("end".equals(flage)){
			lottery.set("status", 0);
			rst.setMsg("活动已经结束！");
		}
		if("start".equals(flage)){
			lottery.set("status", 1);
			rst.setMsg("活动已经开始！");
		}
		if(lottery.update()) {
			setFlashData(rst);
		}
		setAttr("mark", mark);
		int currentPage = getParaToInt("pageNo",1);//当前页码
		redirect("/marketingActivity/"+currentPage+"?mark="+mark);
	}
	
	public void snManager(){
		//String mark = getPara("mark","");
		//String id = getPara("id","");
		String id = getPara(1,"");
		setAttr("lotteryid",id);
		setAttr("lotteryobj",Lottery.dao.getLotteryById(id));
		setAttr("lottery", Lottery.dao.lotteryRecordList(getParaToInt(0, 1), 10, Integer.parseInt(id)));
		render("/admin/marketing_activity/coupon/snManager.html");
	}
	
	public String expSndata(String lotteryid, String args2){
		String mdPath = ""; //导出文件
		String baseDir = JFinal.me().getServletContext().getRealPath("/"); // 得到虚拟目录的绝对路径
		List<LinkedHashMap<String, String>> listBean = null;
		try {
			//先删除之前的临时excel文件
			this.delExcel();
			
			//读取模板excel
			String mbPath = baseDir + "exp/mb.xls";
			InputStream is = new FileInputStream(new File(mbPath));
			HSSFWorkbook wb = new HSSFWorkbook(is);
			//处理第一张sheet
			listBean = new ArrayList<LinkedHashMap<String, String>>();
			
			listBean = getDetaiDataForExp(Integer.parseInt(lotteryid));
			
			exportSNInfo(wb,listBean);
			// 设置目标文件路径
			String uuid = UUID.randomUUID().toString();
			mdPath = baseDir + "exp/temp/" + uuid + ".xls";
			FileOutputStream os = new FileOutputStream(mdPath);
			wb.write(os);
			os.close();
		} catch (FileNotFoundException e) {
			mdPath = "-1";
		} catch (IOException e) {
			mdPath = "-1";
		} catch (Exception e) {
			mdPath = "-1";
			e.printStackTrace();
		}
		
		return mdPath;
	}
	
	private List<LinkedHashMap<String, String>> getDetaiDataForExp(Integer lotteryid) {
		List<LinkedHashMap<String, String>> listBean = new ArrayList<LinkedHashMap<String, String>>();
		LinkedHashMap<String, String> map = null;
		List<Record> list = Lottery.dao.lotteryRecordList(lotteryid);
		if(list.size() > 0){
			for(Record obj : list){
				map = new LinkedHashMap<String, String>();
				map.put("sn", StringUtil.null2String(obj.getStr("sn")));
				map.put("jx", StringUtil.null2String(obj.getInt("prizeno")));
				map.put("exchanged", isGrantPrize(StringUtil.null2String(obj.getInt("exchanged"))));
				map.put("exchangedtime", StringUtil.null2String(obj.getTimestamp("exchangedtime")));
				map.put("phoneno", StringUtil.null2String(obj.get("phoneno")));
				map.put("time", StringUtil.null2String(obj.getTimestamp("time")));
				listBean.add(map);
			}
		}
		return listBean;
	}
	
	private String isGrantPrize(String exchanged){
		String grant = "是";
		if("".equals(exchanged) || "0".equals(exchanged)){
			grant = "否";
		}
		return grant;
	}
	
	private void exportSNInfo(HSSFWorkbook wb, List<LinkedHashMap<String,String>> beanList) {
		HSSFRow row;
		HSSFCell cell;
		HSSFSheet sheet = wb.getSheetAt(0);
		
		//处理表头
		row = sheet.createRow(0);
		cell = row.createCell(0);
		cell.setCellValue("序号");
		cell = row.createCell(1);
		cell.setCellValue("SN码（中奖号）");
		cell = row.createCell(2);
		cell.setCellValue("奖项");
		cell = row.createCell(3);
		cell.setCellValue("是否已发奖品");
		cell = row.createCell(4);
		cell.setCellValue("奖品发送时间");
		cell = row.createCell(5);
		cell.setCellValue("中奖者手机号");
		cell = row.createCell(6);
		cell.setCellValue("中奖时间");
		
		for (int i = 0; i < beanList.size(); i++) {
			row = sheet.createRow(i+1);
			cell = row.createCell(0);
			cell.setCellValue((i+1)+"");
			cell = row.createCell(1);
			cell.setCellValue(beanList.get(i).get("sn"));
			cell = row.createCell(2);
			cell.setCellValue(beanList.get(i).get("jx"));
			cell = row.createCell(3);
			cell.setCellValue(beanList.get(i).get("exchanged"));
			cell = row.createCell(4);
			cell.setCellValue(beanList.get(i).get("exchangedtime")==""?"":beanList.get(i).get("exchangedtime").substring(0,19));
			cell = row.createCell(5);
			cell.setCellValue(beanList.get(i).get("phoneno"));
			cell = row.createCell(6);
			cell.setCellValue(beanList.get(i).get("time")==""?"":beanList.get(i).get("time").substring(0,19));
		}
	}
	
	public String delExcel() {
		try {
			String baseDir = JFinal.me().getServletContext().getRealPath("/");
			String path = baseDir + "exp/temp";
			delAllFile(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); //删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
		 	myFilePath.delete(); //删除空文件夹
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}
	
	public static boolean delAllFile(String path) {
		boolean flag = true;
		File file = new File(path);
		if (!file.exists()) {
			flag = false;
			return flag;
		}
		if (!file.isDirectory()) {
			flag = false;
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);//再删除空文件夹
			}
		}
		return flag;
  }
}
