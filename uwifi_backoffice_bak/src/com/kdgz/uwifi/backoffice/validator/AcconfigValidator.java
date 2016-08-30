package com.kdgz.uwifi.backoffice.validator;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.kdgz.uwifi.backoffice.model.Businessinfo;

public class AcconfigValidator extends Validator {

	@Override
	protected void handleError(Controller controller) {

		if (controller.getRequest().getMethod().equals("POST")) {
			controller.keepModel(Businessinfo.class);
			controller.keepPara();
			
			if (getActionKey().equals("/businessinfo/addBusinessinfo")) {
				
				controller.render("/admin/businessinfo/add.html");
			} else if(getActionKey().equals("/businessinfo/editBusinessinfo")) {
				Businessinfo businessinfo = Businessinfo.dao.findById(controller.getParaToInt(0));
				
				controller.setAttr("businessinfo", businessinfo);
				controller.render("/admin/businessinfo/edit.html");
			}
		}
	}

	@Override
	protected void validate(Controller controller) {
		if (controller.getRequest().getMethod().equals("POST")) {
			//validateInteger("module.sort", Integer.MIN_VALUE,
					//Integer.MAX_VALUE, "errorMsg", "排序必须是数字！");
		}
	}

}
