package com.kdgz.uwifi.backoffice.controller;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.kdgz.uwifi.backoffice.bean.Data;
import com.kdgz.uwifi.backoffice.constant.Constants;
import com.kdgz.uwifi.backoffice.interceptor.ShopInterceptor;
import com.kdgz.uwifi.backoffice.model.Acauth;
import com.kdgz.uwifi.backoffice.model.Acinfo;
import com.kdgz.uwifi.backoffice.model.Authsmstemple;
import com.kdgz.uwifi.backoffice.model.Authweixinconfig;
import com.kdgz.uwifi.backoffice.model.Businessinfo;
import com.kdgz.uwifi.backoffice.model.Businesstemplet;
import com.kdgz.uwifi.backoffice.model.Multiauthconfig;
import com.kdgz.uwifi.backoffice.model.Smsacconfig;
import com.kdgz.uwifi.backoffice.model.Templetinfo;
import com.kdgz.uwifi.backoffice.model.Templetpage;
import com.kdgz.uwifi.backoffice.model.TempletpageLog;
import com.kdgz.uwifi.backoffice.render.MimeTypeRender;

@Before(ShopInterceptor.class)
public class TempletController extends BaseController{
	ResourceBundle bundle = ResourceBundle.getBundle("common");

	public void index() {
		int businessid = getSessionAttr("shopid");
		Businesstemplet businesstemplet = Businesstemplet.dao.getBusinesstempletByBusinessId(businessid);
		setAttr("authModuleManage", Templetinfo.dao.pageinfoList(getParaToInt(0, 1), 10, 1 ));
		setAttr("businessid", businessid);
		if(businesstemplet != null){
			setAttr("authid", businesstemplet.get("authid"));
		}
		setAttr("uploadPath",bundle.getString("uploadPath"));
		render("/admin/shop_manage/approvePage/templetManage.html");
	}
	
	public void viewCode(){
		int businessid = getSessionAttr("shopid");
		Acinfo acinfo = Acinfo.dao.getAcIdByBusid(businessid);
		if(null != acinfo){
			
		} else {
			setAttr("errorMsg","请到 首页-我的店铺，添加路由器！");
		}
		render("/admin/shop_manage/approvePage/templetView.html");
	}
	
	public void qrCode(){
		int businessid = getSessionAttr("shopid");
		Acinfo acinfo = Acinfo.dao.getAcIdByBusid(businessid);
		String acid = "";
		if(null != acinfo){
			acid = acinfo.getStr("acid");
		}
		String url = bundle.getString("authPageView");
		url += "/?gw_address=192.168.1.1&gw_port=2060&gw_id="+acid+"&mac=64:76:ba:8f:0d:f8&url=http%3A//www.baidu.com/index.php%3Ftn%3Dmonline_5_dg";
		render(new MimeTypeRender(url,true));
	}
	
	/**
	 * 更新认证页模板信息
	 */
	public void updateAuthPagetemplet() {
		int businessid = getSessionAttr("shopid");
		String authid = getPara("authid");   //认证页模板id
		
		Businesstemplet businesstemplet = Businesstemplet.dao.getBusinesstempletByBusinessId(businessid);
		
		if (getRequest().getMethod().equals("POST")) {
			
			if(businesstemplet != null){
				businesstemplet.set("businessid", businessid);
				if(authid != null){
					businesstemplet.set("authid", authid);
				}
				if (businesstemplet.update()) {
					if(authid != null){
						setAttr("authid", Integer.valueOf(authid));
					}
					setAttr("infoMsg", "修改模板成功！");
					
				} else {
					setAttr("infoMsg", "修改模板失败！");
				}
			}else{
				businesstemplet = new Businesstemplet();
				businesstemplet.set("businessid", businessid);
				if(authid != null){
					businesstemplet.set("authid", authid);
				}
				if (businesstemplet.save()) {
					setAttr("infoMsg", "修改模板成功！");
					
				} else {
					setAttr("infoMsg", "修改模板失败！");
				}
			}
			
			index();
			
		} else {
			setAttr("businesstemplet", businesstemplet);
		}
	}
	
	/**
	 * 首页幻灯片设置
	 */
	public void authPageManage(){
		int businessid = getSessionAttr("shopid");
		setAttr("authPagemanage", Templetpage.dao.pageTempletpage(getParaToInt(0, 1), 10, 1, businessid));
		setAttr("uploadPath",bundle.getString("uploadPath"));
		//setAttr("authPageView",bundle.getString("authPageView"));
		render("/admin/shop_manage/approvePage/homePageShow.html");
	}
	
	/**
	 * 添加幻灯片
	 */
	public void addAuthTempletpage() {
	    int businessid = getSessionAttr("shopid");
	    Businessinfo businessinfo = Businessinfo.dao.findById(businessid);
		setAttr("businessinfo", businessinfo);
		if (getRequest().getMethod().equals("POST")) {
			Templetpage templetpage = new Templetpage();
			Data retMsg = new Data();
			Templetpage param = getModel(Templetpage.class);
			//templetpage.set("acid", "");
			templetpage.set("businessid", businessid);
			templetpage.set("title", param.getStr("title"));
			templetpage.set("mediaurl", param.getStr("mediaurl"));
			templetpage.set("outerurl", param.getStr("outerurl"));
			templetpage.set("desc", param.getStr("desc"));
			templetpage.set("sort", param.getInt("sort"));
			templetpage.set("temptype", 1);
			templetpage.set("distype", "3");
			templetpage.set("mediatype", 1);
			templetpage.set("delflag", 0);
			Date now = new Date();
			templetpage.set("addtime", now);
			templetpage.set("updatetime", now);
			if (templetpage.save()) {
				TempletpageLog log = new TempletpageLog();
				Businesstemplet businesstemplet = Businesstemplet.dao.getBusinesstempletByBusinessId(businessid);
				//log.setAcId(acid);
				if(businesstemplet != null){
					String auth_id = businesstemplet.getInt("authid")==null ? "" : businesstemplet.getInt("authid").toString();
					log.setTempletId(auth_id);
				}
				
				log.setTemptype("1");
				log.setMediaurl(param.getStr("mediaurl"));
				log.setSort(param.getInt("sort").toString());
				long time = System.currentTimeMillis();
				log.setOptionTime(DateFormatUtils.format(time, Constants.DATETIME_FORMAT));
				log.setStatus("1");   //上线
				this.writePortalLog(log);
				retMsg.setStatus(Constants.OPERATION_SUCCEED);
				retMsg.setMsg("添加幻灯片成功！");
			} else {
				retMsg.setStatus(Constants.OPERATION_FAILED);
				retMsg.setMsg("添加幻灯片失败！");
			}
			//renderJson(retMsg);
			setFlashData(retMsg);
			redirect("/templetFlashManage");

		} else {
			setAttr("uploadPath",bundle.getString("uploadPath"));
			render("/admin/shop_manage/approvePage/homePageAddN.html");
		}
	}
	
	/**
	 * 查看选中的认证页面模板
	 */
	public void templetDetail(){
		int businessid = getSessionAttr("shopid");
		Templetinfo templetinfo = Templetinfo.dao.getSeclectedTemplet(businessid);
		if(templetinfo != null){
			setAttr("templetinfo",templetinfo);
		}
		Templetpage templetpage = Templetpage.dao.findById(getPara(0));
		setAttr("templetpage", templetpage);
		setAttr("uploadPath",bundle.getString("uploadPath"));
		render("/admin/shop_manage/approvePage/templetDetail.html");
	}
	
	/**
	 * 修改幻灯片
	 */
	public void editAuthTempletpage() {
		
		int businessid = getSessionAttr("shopid");
		Businessinfo businessinfo = Businessinfo.dao.findById(businessid);
		setAttr("businessinfo", businessinfo);
		Templetpage templetpage = Templetpage.dao.findById(getPara(0));
		
		//写日志 先下线 再上线
		Businesstemplet businesstemplet = Businesstemplet.dao.getBusinesstempletByBusinessId(businessid);
		
		if (getRequest().getMethod().equals("POST")) {
			Templetpage param = getModel(Templetpage.class);
			Data retMsg = new Data();
		   
			templetpage.set("title", param.getStr("title"));
			templetpage.set("mediaurl", param.getStr("mediaurl"));
			templetpage.set("outerurl", param.getStr("outerurl"));
			templetpage.set("desc", param.getStr("desc"));
			templetpage.set("sort", param.getInt("sort"));
			
			Date now = new Date();
			templetpage.set("updatetime", now);

			if (templetpage.update()) {
				TempletpageLog log2 = new TempletpageLog();
				//log2.setAcId(acid);
				if(businesstemplet != null){
					String auth_id = businesstemplet.getInt("authid")==null ? "" : businesstemplet.getInt("authid").toString();
					log2.setTempletId(auth_id);
				}
				
				log2.setTemptype("1");
				log2.setMediaurl(param.getStr("mediaurl"));
				log2.setSort(param.getInt("sort").toString());
				long time2 = System.currentTimeMillis();
				log2.setOptionTime(DateFormatUtils.format(time2, Constants.DATETIME_FORMAT));
				log2.setStatus("1");   //上线
				this.writePortalLog(log2);
				retMsg.setStatus(Constants.OPERATION_SUCCEED);
				retMsg.setMsg("修改幻灯片成功！");
			} else {
				retMsg.setStatus(Constants.OPERATION_FAILED);
				retMsg.setMsg("修改幻灯片失败！");
			}
			renderJson(retMsg);

		} else {
			
			TempletpageLog log = new TempletpageLog();
			//log.setAcId(acid);
			if(businesstemplet != null){
				String auth_id = businesstemplet.getInt("authid")==null ? "" : businesstemplet.getInt("authid").toString();
				log.setTempletId(auth_id);
			}
			log.setTemptype("1");
			log.setMediaurl(templetpage.getStr("mediaurl"));
			log.setSort(templetpage.getInt("sort").toString());
			long time = System.currentTimeMillis();
			log.setOptionTime(DateFormatUtils.format(time, Constants.DATETIME_FORMAT));
			log.setStatus("0");//下线
			this.writePortalLog(log);
			
			setAttr("templetpage", templetpage);
			setAttr("uploadPath",bundle.getString("uploadPath"));
			//render("/admin/module/authpage/edit.html");
			render("/admin/shop_manage/approvePage/homePageEdit.html");
		}
	}
	
	/**
	 * 删除幻灯片配置
	 * @throws Exception
	 */
	@Before(Tx.class)
	public void deleteAuthPage() throws Exception {
		int id = getParaToInt(0, 0);
	    int businessid = getSessionAttr("shopid");
		if (id != 0) {
			try {

				Templetpage templetpage = Templetpage.dao.findById(id);
				
				if(templetpage != null) {
					
					templetpage.set("delflag", 1);
					
					if (templetpage.update()) {
						TempletpageLog log = new TempletpageLog();
						Businesstemplet businesstemplet = Businesstemplet.dao.getBusinesstempletByBusinessId(businessid);
						if(businesstemplet != null){
							log.setTempletId(businesstemplet.getInt("authid").toString());
						}
						log.setTemptype("1");
						log.setMediaurl(templetpage.getStr("mediaurl"));
						log.setSort(templetpage.getInt("sort").toString());
						long time = System.currentTimeMillis();
						log.setOptionTime(DateFormatUtils.format(time, Constants.DATETIME_FORMAT));
						log.setStatus("0");   //下线
						this.writePortalLog(log);
						setAttr("infoMsg", "删除幻灯片信息成功！");
					}
					
				}

			} catch (Exception e) {
				setAttr("errorMsg", "删除幻灯片信息失败！");
				throw e;
			}

			forwardAction("/templetManage/authPageManage");
		}
	}
	
	/**
	 * 认证方式设置
	 */
	public void authStyle(){
		
		String acid = getPara("acid");
		//String businessid = getPara("businessid");
		int businessid = getSessionAttr("shopid");
		String authtypeId = getPara("acauth.authtype");
		Acauth acauth = Acauth.dao.selectAcauthByAcId(businessid+"");//acid to businessid
		setAttr("acid", acid);
		setAttr("businessid", businessid);
		
		setAttr("uploadPath",bundle.getString("uploadPath"));
		
		// 短信认证
		Smsacconfig smsacconfig = Smsacconfig.dao.selectSmsacconfigByAcId(businessid+"");// businessid
		Integer  smsid = getParaToInt("smsid");;

		// 微信认证
		Authweixinconfig authweixinconfig = Authweixinconfig.dao.selectAuthweixinByBusinessId(businessid+"");
		
		//多认证方式
		Multiauthconfig multiauthconfig = null;
		if(smsacconfig != null){
			 multiauthconfig = Multiauthconfig.dao.selectMultiauthBymmsid(smsacconfig.getInt("smsid"), businessid+"");//acId to businessid
		}
		if (getRequest().getMethod().equals("POST")) {
			Acauth param = getModel(Acauth.class);
			int afterauth = getParaToInt("afterauth",1);
			String portalurl = getPara("protalUrl","");
			if(2==afterauth){
				portalurl = getPara("zdurl","");
			}
			
			Data retMsg = new Data();
			if(acauth == null){
				acauth = new Acauth();
				//acauth.set("acid", acid);
				acauth.set("businessid", businessid);
				acauth.set("authtype", param.getInt("authtype"));
				Date now = new Date();
				acauth.set("addtime", now);
				acauth.set("updatetime", now);
				acauth.set("afterauth", afterauth);
				acauth.set("portalurl", portalurl.trim());
				if (acauth.save()) {
					retMsg.setStatus(Constants.OPERATION_SUCCEED);
					retMsg.setMsg("配置认证方式成功！");
				} else {
					retMsg.setStatus(Constants.OPERATION_FAILED);
					retMsg.setMsg("配置认证方式失败！");
				}
			}else{
				acauth.set("authtype", param.getInt("authtype"));
				Date now = new Date();
				acauth.set("updatetime", now);
				acauth.set("afterauth", afterauth);
				acauth.set("portalurl", portalurl.trim());
				if (acauth.update()) {
					retMsg.setStatus(Constants.OPERATION_SUCCEED);
					retMsg.setMsg("配置认证方式成功！");
				} else {
					retMsg.setStatus(Constants.OPERATION_FAILED);
					retMsg.setMsg("配置认证方式失败！");
				}
			}
			// 短信配置
			if(authtypeId.equals("1") || authtypeId.equals("3")){
				if(smsacconfig != null){
					smsacconfig.set("smsid", smsid);
					if (smsacconfig.update()) {
						retMsg.setStatus(Constants.OPERATION_SUCCEED);
						retMsg.setMsg("配置认证方式成功！");
					} else {
						retMsg.setStatus(Constants.OPERATION_FAILED);
						retMsg.setMsg("配置认证方式失败！");
					}
				}else{
					smsacconfig = new Smsacconfig();
					//smsacconfig.set("acid", acid);
					smsacconfig.set("businessid", businessid);
					smsacconfig.set("smsid", smsid);
					if (smsacconfig.save()) {
						retMsg.setStatus(Constants.OPERATION_SUCCEED);
						retMsg.setMsg("配置认证方式成功！");
					} else {
						retMsg.setStatus(Constants.OPERATION_FAILED);
						retMsg.setMsg("配置认证方式失败！");
					}
				}
			}
			
			//微信配置
			if(authtypeId.equals("2") || authtypeId.equals("3")){
				Authweixinconfig param1 = getModel(Authweixinconfig.class);
				if(authweixinconfig != null){
					//authweixinconfig.set("businessid", businessid);
					authweixinconfig.set("weixinname", param1.getStr("weixinname"));
					authweixinconfig.set("weixinno", param1.getStr("weixinno"));
					Date now = new Date();
					authweixinconfig.set("updatetime", now);
					if (authweixinconfig.update()) {
						retMsg.setStatus(Constants.OPERATION_SUCCEED);
						retMsg.setMsg("配置认证方式成功！");
					} else {
						retMsg.setStatus(Constants.OPERATION_FAILED);
						retMsg.setMsg("配置认证方式失败！");
					}
				}else{
					authweixinconfig = new Authweixinconfig();
					authweixinconfig.set("businessid", businessid);
					authweixinconfig.set("weixinname", param1.getStr("weixinname"));
					authweixinconfig.set("weixinno", param1.getStr("weixinno"));
					authweixinconfig.set("delflag", 0);
					Date now = new Date();
					authweixinconfig.set("addtime", now);
					authweixinconfig.set("updatetime", now);
					if (authweixinconfig.save()) {
						retMsg.setStatus(Constants.OPERATION_SUCCEED);
						retMsg.setMsg("配置认证方式成功！");
					} else {
						retMsg.setStatus(Constants.OPERATION_FAILED);
						retMsg.setMsg("配置认证方式失败！");
					}
				}
				
			}
			//多种认证方式配置
			if(authtypeId.equals("3")){
				saveMb(businessid,"10");
				
				Multiauthconfig param2 = getModel(Multiauthconfig.class);
				String[] tipcontent = getParaValues("tipcontent");
				
				if(multiauthconfig != null){
					multiauthconfig.set("smsid", smsid);
					multiauthconfig.set("employeepwd", param2.getStr("employeepwd"));
					multiauthconfig.set("freetime", param2.getInt("freetime"));
					multiauthconfig.set("tiptitle", param2.getStr("tiptitle"));
					if(tipcontent != null){
						for(int i = 0; i < tipcontent.length; i++){
							int j = i+1;
							multiauthconfig.set("tipcontent"+j, tipcontent[i]);
						}
					}
					
					Date now = new Date();
					multiauthconfig.set("updatetime", now);
					if (multiauthconfig.update()) {
						retMsg.setStatus(Constants.OPERATION_SUCCEED);
						retMsg.setMsg("配置认证方式成功！");
					} else {
						retMsg.setStatus(Constants.OPERATION_FAILED);
						retMsg.setMsg("配置认证方式失败！");
					}
				}else{
					multiauthconfig = new Multiauthconfig();
					multiauthconfig.set("smsid", smsid);
					multiauthconfig.set("businessid", businessid);
					multiauthconfig.set("employeepwd", param2.getStr("employeepwd"));
					multiauthconfig.set("freetime", param2.getInt("freetime"));
					multiauthconfig.set("tiptitle", param2.getStr("tiptitle"));
					if(tipcontent != null){
						for(int i = 0; i < tipcontent.length; i++){
							int j = i+1;
							multiauthconfig.set("tipcontent"+j, tipcontent[i]);
						}
					}
					Date now = new Date();
					multiauthconfig.set("addtime", now);
					multiauthconfig.set("updatetime", now);
					
					if (multiauthconfig.save()) {
						retMsg.setStatus(Constants.OPERATION_SUCCEED);
						retMsg.setMsg("配置认证方式成功！");
					} else {
						retMsg.setStatus(Constants.OPERATION_FAILED);
						retMsg.setMsg("配置认证方式失败！");
					}
				}
				
			}
			
			if(authtypeId.equals("0")||authtypeId.equals("1")||authtypeId.equals("2")){
				saveMb(businessid,"1");
			}
			
			//renderJson("msg", infoMsg);
			renderJson(retMsg);
			
		} else {
			setAttr("acauth", acauth);
			
			//短信认证
			List<Authsmstemple> authsmslist = Authsmstemple.dao.selectAuthsms();
			Authsmstemple authsmstemple = null;
			if(smsacconfig != null){
				 authsmstemple = Authsmstemple.dao.selectSmstempleById(smsacconfig.getInt("smsid"));
			}else{
				for(int i = 0; i < authsmslist.size(); ){
					authsmstemple  =  authsmslist.get(i);
					break;
				}
			}
			setAttr("authsmslist", authsmslist);
			setAttr("acid", acid);
			setAttr("smsacconfig", smsacconfig);
			setAttr("authsmstemple", authsmstemple);
		
			//微信认证
			setAttr("authweixinconfig", authweixinconfig);
			
			//多认证方式
			setAttr("multiauthconfig", multiauthconfig);
			
			render("/admin/shop_manage/AuthenticateStyle/AuthenticatesStyle.html");
		}
		
	}
	
	public void saveMb(int businessid, String authid){
		Businesstemplet businesstemplet = Businesstemplet.dao.getBusinesstempletByBusinessId(businessid);
		if(businesstemplet != null){
			businesstemplet.set("businessid", businessid);
			if(authid != null){
				businesstemplet.set("authid", authid);
			}
			businesstemplet.update();
				
		}else{
			businesstemplet = new Businesstemplet();
			businesstemplet.set("businessid", businessid);
			if(authid != null){
				businesstemplet.set("authid", authid);
			}
			businesstemplet.save();
		}
	}
	
	/**
	 * 删除提示文本内容
	 */
	public void delTipcontent(){
		
		String acid = getPara("acid");
		String tip = getPara("tip");
		Multiauthconfig multiauthconfig = Multiauthconfig.dao.selectMultiauthBymmsid(-1, acid);
		if(multiauthconfig != null){
			int id = multiauthconfig.getInt("id");
			Record record = new Record();
			record.set("id", id);
			if(tip.equals("1")){
				record.set("tipcontent1", null);
			}
			if(tip.equals("2")){
				record.set("tipcontent2", null);
			}
			if(tip.equals("3")){
				record.set("tipcontent3", null);
			}
			if(tip.equals("4")){
				record.set("tipcontent4", null);
			}
			if(tip.equals("5")){
				record.set("tipcontent5", null);
			}
			Db.update("multiauthconfig", record);
			
		}
	}
	
	/**
	 * 获取短信模板
	 */
	public void getSmstemple(){
		
		Integer id = getParaToInt("id");
		Authsmstemple authsmstemple = Authsmstemple.dao.selectSmstempleById(id);
		if(authsmstemple != null){
			renderJson(authsmstemple);
		}else{
			renderJson("templecontent", "");
		}
		
		
	}
}
