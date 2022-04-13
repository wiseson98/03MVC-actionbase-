package com.model2.mvc.service.purchase.impl;

import java.util.Map;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.product.dao.ProductDAO;
import com.model2.mvc.service.purchase.PurchaseService;
import com.model2.mvc.service.purchase.dao.PurchaseDAO;

public class PurchaseServiceImpl implements PurchaseService {

	///Field
	private PurchaseDAO purchaseDAO;
	private ProductDAO productDAO;
	
	///Constructor
	public PurchaseServiceImpl() {
		purchaseDAO = new PurchaseDAO();
		productDAO = new ProductDAO();
	}

	///Method
	public void addPurchase(Purchase purchase) throws Exception {
		purchaseDAO.insertPurchase(purchase);		
	}

	public void updateTranCode(Purchase purchase) throws Exception {
		purchaseDAO.updateTranCode(purchase);	
	}

	public void updatePurchase(Purchase purchase) throws Exception {
		purchaseDAO.updatePurchase(purchase);		
	}

	public Purchase getPurchase(int tranNo) throws Exception {
		return purchaseDAO.findPurchase(tranNo);
	}

	public Map<String, Object> getPurchaseList(Search search, String userId) throws Exception {
		return purchaseDAO.getPurchaseList(search, userId);
	}

	public Map<String, Object> getSaleList(Search search) throws Exception {
		return purchaseDAO.getSalesList(search);
	}

}
