package com.kdgz.uwifi.backoffice.validator;

import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.kdgz.uwifi.backoffice.model.Acinfo;
import com.kdgz.uwifi.backoffice.model.Bwlist;

public class BwlistValidator extends Validator {

	@Override
	protected void handleError(Controller controller) {

		if (controller.getRequest().getMethod().equals("POST")) {
			controller.keepModel(Bwlist.class);
			controller.keepPara();
			String businessids = controller.getSessionAttr("businessids");
			
			
			String infoMsg = controller.getAttr("errorMsg");
			
			if (getActionKey().equals("/bwlist/addBwlist")) {
				
				
				controller.renderJson("infoMsg", infoMsg);
				
				
			} else if(getActionKey().equals("/bwlist/editBwlist")) {
				
				Bwlist bwlist = Bwlist.dao.findById(controller.getParaToInt(0));
				
				List<Acinfo> acidList = Acinfo.dao.selectAcidList(businessids);
				controller.setAttr("acidList", acidList);
				
				controller.setAttr("bwlist", bwlist);
				
				controller.renderJson("infoMsg", infoMsg);
//				controller.render("/admin/route_manage/bwlist_edit.html");
			}
		}
	}

	@Override
	protected void validate(Controller controller) {
		if (controller.getRequest().getMethod().equals("POST")) {
			int controltype = controller.getParaToInt("bwlist.controltype");
			if(controltype == 1){
				validateRegex("bwlist.controlvalue", 
						"(http://|ftp://|https://|www){0,1}[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr)[^\u4e00-\u9fa5\\s]*",
						"errorMsg", "输入的域名不符合要求,如(www.baidu.com)");
				
			}
			if(controltype == 2){
				validateRegex("bwlist.controlvalue", 
						"^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$",
						"errorMsg", "输入的IP地址不符合要求,如(192.168.10.30)");
			}
			if(controltype == 3){
				validateRegex("bwlist.controlvalue", 
						"^[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}+:[a-fA-F0-9]{2}$",
						"errorMsg", "输入的MAC地址不符合要求,如(48:5A:B6:9E:47:FF)");
			}
			
			
			
		}
	}

}
