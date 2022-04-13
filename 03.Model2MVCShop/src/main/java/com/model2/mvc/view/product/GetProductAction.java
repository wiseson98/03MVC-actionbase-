package com.model2.mvc.view.product;

import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;

public class GetProductAction extends Action {

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String prodNo = request.getParameter("prodNo");
		
		ProductService service = new ProductServiceImpl();
		Product product = service.getProduct(Integer.parseInt(prodNo));
		
		request.setAttribute("product", product);
		
				
		String cookieValue = null;
		Cookie cookie = null;
		
		cookieValue = prodNo;
		
		if(request.getCookies() != null ) {
			for(Cookie c : request.getCookies()){				
				if(c.getName().equals("history")) {
					cookieValue = URLDecoder.decode(c.getValue(), "euc-kr");
					cookieValue += "," + prodNo;	
					System.out.println("history ¿÷¿∏∏È : " + cookieValue);
				}					
			}
		}
		System.out.println("cookieValue : " + cookieValue);
		cookie = new Cookie("history", URLEncoder.encode(cookieValue, "euc-kr"));
		response.addCookie(cookie);
		
		return "forward:/product/readProduct.jsp";
	}
}