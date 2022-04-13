package com.model2.mvc.service.purchase.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.model2.mvc.common.Search;
import com.model2.mvc.common.util.DBUtil;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.Purchase;
import com.model2.mvc.service.domain.User;

public class PurchaseDAO {
	///Field
	
	///Constructor
	public PurchaseDAO() {
	}
	
	///Method
	public void insertPurchase(Purchase purchase) throws Exception {
		Connection con = DBUtil.getConnection();
		
//		insert into transaction values (seq_transaction_tran_no.nextval, 10009, 'user16', '1', 'SCOTT', NULL, NULL, NULL, 3, sysdate, to_date('2022-03-31', 'YYYY-MM-DD'));
		String sql = "INSERT " + 
								 "INTO transaction " + 
								 "VALUES (seq_transaction_tran_no.nextval, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE, TO_DATE(?, 'YYYY-MM-DD'))";
		
		PreparedStatement pStmt = con.prepareStatement(sql);
		pStmt.setInt(1, purchase.getPurchaseProd().getProdNo());
		pStmt.setString(2, purchase.getBuyer().getUserId());
		pStmt.setString(3, purchase.getPaymentOption());
		pStmt.setString(4, purchase.getReceiverName());
		pStmt.setString(5, purchase.getReceiverPhone());
		pStmt.setString(6, purchase.getDlvyAddr());
		pStmt.setString(7, purchase.getDlvyRequest());
		pStmt.setString(8, purchase.getTranCode());
		pStmt.setString(9, purchase.getDlvyDate());
		pStmt.executeUpdate();
		
		pStmt.close();
		con.close();
		
	}

	public void updateTranCode(Purchase purchase) throws Exception{
		
		Connection con = DBUtil.getConnection();
		
		String sql = "UPDATE transaction "
				  + " SET tran_status_code = ? "
				  + " WHERE tran_no = ?";
		
		PreparedStatement pStmt = con.prepareStatement(sql);
		pStmt.setString(1, purchase.getTranCode());
		pStmt.setInt(2, purchase.getTranNo());
		pStmt.executeUpdate();
		
		pStmt.close();
		con.close();
	}
	
	public void updatePurchase(Purchase purchase) throws Exception{
		
		Connection con = DBUtil.getConnection();
		
		String sql = "UPDATE transaction "
				  + " SET payment_option = ?, "
				  	  + " receiver_name = ?, "
				  	  + " receiver_phone = ?, "
				  	  + " dlvy_addr = ?, "
				  	  + " dlvy_request = ?, "
				  	  + " dlvy_date = TO_DATE(?, 'YYYY-MM-DD') "
				  + " WHERE tran_no = ?";
		
		PreparedStatement pStmt = con.prepareStatement(sql);
		pStmt.setString(1, purchase.getPaymentOption());
		pStmt.setString(2, purchase.getReceiverName());
		pStmt.setString(3, purchase.getReceiverPhone());
		pStmt.setString(4, purchase.getDlvyAddr());
		pStmt.setString(5, purchase.getDlvyRequest());
		pStmt.setString(6, purchase.getDlvyDate());
		pStmt.setInt(7, purchase.getTranNo());
		pStmt.executeUpdate();
		
		pStmt.close();
		con.close();
	}
	
	public Purchase findPurchase(int tranNo) throws Exception{
		
		Connection con = DBUtil.getConnection();
//		 select tran_no, prod_no, buyer_id, payment_option, receiver_name, RECEIVER_PHONE, DEMAILADDR,DLVY_REQUEST, TRAN_STATUS_CODE, ORDER_DATA, to_char(DLVY_DATE, 'YYYY-MM-DD HH:MM:SS')
		String sql = "SELECT "
				  + " t.tran_no, p.prod_no, p.prod_name, t.buyer_id, "
				  + " t.receiver_name, t.receiver_phone, t.payment_option, "
				  + " t.dlvy_addr, t.dlvy_request, t.tran_status_code, "
				  + " t.order_date, TO_CHAR(t.dlvy_date, 'YYYY-MM-DD HH:MM:SS') dlvy_date"
				  + " FROM product p, transaction t "
				  + " WHERE p.prod_no = t.prod_no(+) "
				  		+ " AND t.tran_no = ?";
		
		PreparedStatement pStmt = con.prepareStatement(sql);
		pStmt.setInt(1, tranNo);
		
		ResultSet rs = pStmt.executeQuery();
		
		Purchase purchase = null;
		
		while(rs.next()) {
			purchase = new Purchase();
			purchase.setTranNo(rs.getInt("tran_no"));
			
			purchase.setPurchaseProd(new Product());
			purchase.getPurchaseProd().setProdNo(rs.getInt("prod_no"));
			purchase.getPurchaseProd().setProdName(rs.getString("prod_name"));
			
			purchase.setBuyer(new User());
			purchase.getBuyer().setUserId(rs.getString("buyer_id"));
			
			purchase.setPaymentOption(rs.getString("payment_option"));
			purchase.setReceiverName(rs.getString("receiver_name"));
			purchase.setReceiverPhone(rs.getString("receiver_phone"));
			purchase.setDlvyAddr(rs.getString("dlvy_addr"));
			purchase.setDlvyRequest(rs.getString("dlvy_request"));
			purchase.setTranCode(rs.getString("tran_status_code"));
			purchase.setOderDate(rs.getDate("order_date"));
			purchase.setDlvyDate(rs.getString("dlvy_date"));			
		}
		
		rs.close();
		pStmt.close();
		con.close();
		
		return purchase;
	}
	
	public Map<String, Object> getPurchaseList(Search search, String userId) throws Exception{
		
		Map<String , Object>  map = new HashMap<String, Object>();
		
		Connection con = DBUtil.getConnection();
		
		String sql = "SELECT tran_no, tran_status_code FROM TRANSACTION ";
		
		if (search.getSearchCondition() != null) {
			sql += " WHERE buyer_id='" + userId + "' ";
		}
		sql += " ORDER BY order_date DESC";
		
		System.out.println("PurchaseDAO::Original SQL :: " + sql);
		
		//==> TotalCount GET
		int totalCount = this.getTotalCount(sql);
		System.out.println("PurchaseDAO :: totalCount  :: " + totalCount);
		
		//==> CurrentPage 게시물만 받도록 Query 다시구성
		sql = makeCurrentPageSql(sql, search);
		PreparedStatement pStmt = con.prepareStatement(sql);
		ResultSet rs = pStmt.executeQuery();
		
		System.out.println(search);

		List<Purchase> list = new ArrayList<Purchase>();
		
		while(rs.next()){
			Purchase purchase = new Purchase();
			purchase.setTranNo(rs.getInt("tran_no"));
			purchase.setTranCode(rs.getString("tran_status_code"));
			list.add(purchase);
		}

		//==> totalCount 정보 저장
		map.put("totalCount", new Integer(totalCount));
		//==> currentPage 의 게시물 정보 갖는 List 저장
		map.put("list", list);

		rs.close();
		pStmt.close();
		con.close();

		return map;
	}
	
	public Map<String, Object> getSalesList(Search search) throws Exception{
		Map<String , Object>  map = new HashMap<String, Object>();
		
		Connection con = DBUtil.getConnection();
		
		String sql = "SELECT "
			      + " tran_no, prod_no, buyer_id, "
			      + " order_date, tran_status_code "
			      + " FROM transaction "
			      + " ORDER BY order_date DESC";
		
		
		System.out.println("PurchaseDAO::Original SQL :: " + sql);
		
		//==> TotalCount GET
		int totalCount = this.getTotalCount(sql);
		System.out.println("PurchaseDAO :: totalCount  :: " + totalCount);
		
		//==> CurrentPage 게시물만 받도록 Query 다시구성
		sql = makeCurrentPageSql(sql, search);
		PreparedStatement pStmt = con.prepareStatement(sql);
		ResultSet rs = pStmt.executeQuery();
		
		System.out.println(search);

		List<Purchase> list = new ArrayList<Purchase>();
		
		while(rs.next()){
			Purchase purchase = new Purchase();
			purchase.setTranNo(rs.getInt("tran_no"));
			
			purchase.setPurchaseProd(new Product());
			purchase.getPurchaseProd().setProdNo(rs.getInt("prod_no"));
			
			purchase.setBuyer(new User());
			purchase.getBuyer().setUserId(rs.getString("buyer_id"));
			
			purchase.setOderDate(rs.getDate("order_date"));
			purchase.setTranCode(rs.getString("tran_status_code"));
			
			list.add(purchase);
		}

		//==> totalCount 정보 저장
		map.put("totalCount", new Integer(totalCount));
		//==> currentPage 의 게시물 정보 갖는 List 저장
		map.put("list", list);

		rs.close();
		pStmt.close();
		con.close();

		return map;
	}
	
		
	// 게시판 Page 처리를 위한 전체 Row(totalCount)  return
	private int getTotalCount(String sql) throws Exception {
		
		sql = "SELECT COUNT(*) "+
		          "FROM ( " +sql+ ") countTable";
		
		Connection con = DBUtil.getConnection();
		PreparedStatement pStmt = con.prepareStatement(sql);
		ResultSet rs = pStmt.executeQuery();
		
		int totalCount = 0;
		if( rs.next() ){
			totalCount = rs.getInt(1);
		}
		
		pStmt.close();
		con.close();
		rs.close();
		
		return totalCount;
	}
		
	// 게시판 currentPage Row 만  return 
	private String makeCurrentPageSql(String sql , Search search){
		sql = 	"SELECT * "+ 
					"FROM (		SELECT inner_table. * ,  ROWNUM AS row_seq " +
									" 	FROM (	"+sql+" ) inner_table "+
									"	WHERE ROWNUM <="+search.getCurrentPage()*search.getPageSize()+" ) " +
					"WHERE row_seq BETWEEN "+((search.getCurrentPage()-1)*search.getPageSize()+1) +" AND "+search.getCurrentPage()*search.getPageSize();
		
		System.out.println("PurchaseDAO :: make SQL :: "+ sql);	
		
		return sql;
	}

}//end of class