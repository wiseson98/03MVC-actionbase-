package com.model2.mvc.service.product.dao;

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

public class ProductDAO {

	public ProductDAO() {		
	}
	
	public void insertProduct(Product product) throws Exception {
		Connection con = DBUtil.getConnection();
		
//		insert into PRODUCT values (seq_product_prod_no.nextval, '자전거2', '자전거', to_char(to_date('2021-03-25','YYYY-MM-DD'),'YYYYMMDD'), 25000, 'abc.jpg', sysdate)
//		String sql = "insert into PRODUCT values (seq_product_prod_no.nextval, ?, ?, to_char(to_date(?,'YYYY-MM-DD'),'YYYYMMDD'), ?, ?, sysdate) ";
		String sql = "INSERT INTO product VALUES (seq_product_prod_no.nextval, ?, ?, ?, ?, ?, sysdate) ";
		
		PreparedStatement pStmt = con.prepareStatement(sql);
		pStmt.setString(1, product.getProdName());
		pStmt.setString(2, product.getProdDetail());
		pStmt.setString(3, product.getManuDate());
		pStmt.setInt(4, product.getPrice());
		if(product.getFileName() == null || product.getFileName().equals("")) {
			pStmt.setString(5, "empty.GIF");
		}else {
			pStmt.setString(5, product.getFileName());
		}		
		pStmt.executeUpdate();
		
		con.close();
	}
	
	public Product findProduct(int prodNo) throws Exception {
		Connection con = DBUtil.getConnection();
		
//		select * from PRODUCT where PROD_NO = 10007;		
		String sql = "SELECT "
				  + " p.*, NVL(t.tran_status_code,'001') prod_status_code "
				  + " FROM product p, transaction t "
				  + " WHERE p.prod_no = t.prod_no(+) "
				  		+ " AND p.prod_no = ?";
		
		PreparedStatement pStmt = con.prepareStatement(sql);
		pStmt.setInt(1, prodNo);
		
		ResultSet rs = pStmt.executeQuery();
		
		Product product = null;
		while(rs.next()) {
			product = new Product();
			product.setProdNo(rs.getInt("prod_no"));
			product.setProdName(rs.getString("prod_name"));
			product.setProdDetail(rs.getString("prod_detail"));
			product.setManuDate(rs.getString("manufacture_day"));
			product.setPrice(rs.getInt("price"));
			product.setFileName(rs.getString("image_file"));
			product.setRegDate(rs.getDate("reg_date"));
			product.setProTranCode(rs.getString("prod_status_code"));
		}
		
		con.close();
		
		return product;
	}
	
	public void updateProduct(Product product) throws Exception{
		Connection con = DBUtil.getConnection();
		
//		update PRODUCT set PROD_NAME='오토바이', PROD_DETAIL='오토바이 판매', MANUFACTURE_DAY='20220325', PRICE=30000, IMAGE_FILE='abcdefg.jpg' where PROD_NO=10001;
		String sql = "UPDATE product SET prod_name=?, prod_detail=?, manufacture_day=?, price=?, image_file=? WHERE prod_no=?";
		
		PreparedStatement pStmt = con.prepareStatement(sql);
		pStmt.setString(1, product.getProdName());
		pStmt.setString(2, product.getProdDetail());
		pStmt.setString(3, product.getManuDate());
		pStmt.setInt(4, product.getPrice());
		pStmt.setString(5, product.getFileName());
		pStmt.setInt(6, product.getProdNo());
		pStmt.executeUpdate();
		
		con.close();
	}
	
	public Map<String, Object> getProductList(Search search) throws Exception{
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		Connection con = DBUtil.getConnection();
		
		// Original Query 구성		
		String sql = "SELECT "
					+ " p.prod_no, p.prod_name, p.price, p.reg_date, NVL(t.tran_status_code,'001') prod_status_code "
					+ " FROM product p, transaction t "
					+ " WHERE p.prod_no = t.prod_no(+) ";
		
		if(search.getSearchCondition() != null) {
			if(search.getSearchCondition().equals("0") && !search.getSearchKeyword().equals("")) {
				sql += " AND p.prod_no = " + search.getSearchKeyword();
			}else if(search.getSearchCondition().equals("1") && !search.getSearchKeyword().equals("")) {
				sql += " AND p.prod_name LIKE '%" + search.getSearchKeyword() + "%'";
			}else if(search.getSearchCondition().equals("2") && !search.getSearchKeyword().equals("")) {
				sql += " AND p.price = " + search.getSearchKeyword();
			}
		}
		sql += " ORDER BY p.reg_date DESC";
		
		System.out.println("ProductDAO::Original SQL :: " + sql);
		
		//==> TotalCount GET
		int totalCount = this.getTotalCount(sql);
		System.out.println("ProductDAO :: totalCount :: " + totalCount);
		
		//==> CurrentPage 게시물만 받도록 Query 다시구성
		sql = makeCurrentPageSql(sql, search);
		PreparedStatement pStmt = con.prepareStatement(sql);
		ResultSet rs = pStmt.executeQuery();
		
		System.out.println(search);
		
		List<Product> list = new ArrayList<Product>();
		
		while(rs.next()) {
			Product product = new Product();
			product.setProdNo(rs.getInt("prod_no"));
			product.setProdName(rs.getString("prod_name"));
			product.setPrice(rs.getInt("price"));
			product.setRegDate(rs.getDate("reg_date"));
			product.setProTranCode(rs.getString("prod_status_code"));
			list.add(product);
		}
		
		//==> totalCount 정보 저장
		map.put("totalCount", new Integer(totalCount));
		//==> currentPage의 게시물 정보 갖는 List 저장
		map.put("list", list);
		
		rs.close();
		pStmt.close();
		con.close();

 		return map;
	}

	// 게시판 Page 처리를 위한 전체 Row(totalCount) return
	private int getTotalCount(String sql) throws Exception {
		
		sql = "SELECT COUNT(*) " + 
				 "FROM ( " + sql + " ) countTable";
		
		Connection con = DBUtil.getConnection();
		PreparedStatement pStmt = con.prepareStatement(sql);
		ResultSet rs = pStmt.executeQuery();
		
		int totalCount = 0;
		if(rs.next()) {
			totalCount = rs.getInt(1);
		}
		
		rs.close();
		pStmt.close();
		con.close();
		
		return totalCount;
	}	
	
	// 게시판 currentPage Row만 return
	private String makeCurrentPageSql(String sql, Search search) {
		sql = "SELECT * "+ 
				 "FROM (		SELECT inner_table. * ,  ROWNUM AS row_seq " +
				 				  " FROM (	" + sql + " ) inner_table " +
				 				  " WHERE ROWNUM <= " + search.getCurrentPage() * search.getPageSize() + " ) " +
				"WHERE row_seq BETWEEN " + ((search.getCurrentPage() - 1) * search.getPageSize() + 1) + " AND " + search.getCurrentPage() * search.getPageSize();
		
		System.out.println("ProductDAO :: make SQL :: " + sql);
		
		return sql;
	}
}//end of class
