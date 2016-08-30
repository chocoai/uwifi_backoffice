package com.kdgz.uwifi.backoffice.validator;

import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.kdgz.uwifi.backoffice.model.Acbrand;
import com.kdgz.uwifi.backoffice.model.Acinfo;
import com.kdgz.uwifi.backoffice.model.Actypeinfo;
import com.kdgz.uwifi.backoffice.model.Businessinfo;
import com.kdgz.uwifi.backoffice.model.Construtinfo;

public class AcinfoValidator extends Validator {

	@Override
	protected void handleError(Controller controller) {

		if (controller.getRequest().getMethod().equals("POST")) {
			controller.keepModel(Businessinfo.class);
			controller.keepPara();
			String businessids = controller.getSessionAttr("businessids");
			if (getActionKey().equals("/acinfo/addAcinfo")) {
				
				//获取商家名称列表
				List<Businessinfo> businessinfoList = Businessinfo.dao.selectBussinessinfoList(businessids);
				controller.setAttr("businessinfoList", businessinfoList);
				//获取施工方名称列表
				List<Construtinfo> construtinfoList = Construtinfo.dao.selectConstrutinfoList();
				controller.setAttr("construtinfoList", construtinfoList);
				//获取品牌列表
				List<Acbrand> acbrandlist = Acbrand.dao.selectAcbrandList();
				controller.setAttr("acbrandList", acbrandlist);
				
				controller.render("/admin/route_manage/ac_add.html");
				
				
			} else if(getActionKey().equals("/acinfo/editAcinfo")) {
				Acinfo  acinfo = Acinfo.dao.findById(controller.getParaToInt(0));
				
				//获取商家名称列表
				List<Businessinfo> businessinfoList = Businessinfo.dao.selectBussinessinfoList(businessids);
				controller.setAttr("businessinfoList", businessinfoList);
				//获取施工方名称列表
				List<Construtinfo> construtinfoList = Construtinfo.dao.selectConstrutinfoList();
				controller.setAttr("construtinfoList", construtinfoList);
				//获取品牌列表
				List<Acbrand> acbrandlist = Acbrand.dao.selectAcbrandList();
				controller.setAttr("acbrandList", acbrandlist);
				//获取品牌型号
				List<Actypeinfo> actypeinfoList = Actypeinfo.dao.selectActpeinfoListById(acinfo.get("acbrandid").toString());
				controller.setAttr("actypeinfoList", actypeinfoList);
				
				controller.setAttr("acinfo", acinfo);
				controller.render("/admin/route_manage/ac_edit.html");
			}
		}
	}

	@Override
	protected void validate(Controller controller) {
		if (controller.getRequest().getMethod().equals("POST")) {
			validateRegex("acinfo.acid", "^[A-Za-z0-9]+$", "errorMsg", "AC网关ID只能是数字和字母的组合!");
		}
	}

}
