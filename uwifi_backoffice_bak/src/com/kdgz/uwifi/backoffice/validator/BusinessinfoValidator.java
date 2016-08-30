package com.kdgz.uwifi.backoffice.validator;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import com.kdgz.uwifi.backoffice.model.Businessinfo;

public class BusinessinfoValidator extends Validator {

	@Override
	protected void handleError(Controller controller) {

			controller.keepModel(Businessinfo.class);
			controller.keepPara();
	}

	@Override
	protected void validate(Controller controller) {
		if (controller.getRequest().getMethod().equals("POST")) {
			//validateInteger("module.sort", Integer.MIN_VALUE,
					//Integer.MAX_VALUE, "errorMsg", "排序必须是数字！");
			
//			validateToken("businessinfoToken", "errorMsg", "请不要重复提交");
		}
		
	}

}
