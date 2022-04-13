package com.model2.mvc.view.purchase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.purchase.impl.PurchaseServiceImpl;

public class UpdateTranCodeAction extends Action {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String resultPageName = "listPurchase.do"; 
		
		String tranCode = request.getParameter("tranCode");
		
		Purchase purchase = new Purchase();
				
		purchase.setTranNo(Integer.parseInt(request.getParameter("tranNo")));
				
		purchase.setTranCode(tranCode);
		
		PurchaseService purchaseService = new PurchaseServiceImpl();
		purchaseService.updateTranCode(purchase);
		
		if(request.getParameter("menu") != null && request.getParameter("menu").equals("manage")) {
			resultPageName = "listSale.do";
		}
		
		return "forward:/" + resultPageName;
	}
}