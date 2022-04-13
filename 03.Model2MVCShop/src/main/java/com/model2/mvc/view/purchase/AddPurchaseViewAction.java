package com.model2.mvc.view.purchase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;

public class AddPurchaseViewAction extends Action {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String prodNo = request.getParameter("prodNo");
		
		ProductService productService = new ProductServiceImpl();
		Product product = productService.getProduct(Integer.parseInt(prodNo));
		
		request.setAttribute("product", product);
		
		return "forward:/purchase/addPurchaseView.jsp";
		
	}
}