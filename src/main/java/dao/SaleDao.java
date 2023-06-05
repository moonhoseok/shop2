package dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Repository;


import dao.mapper.SaleMapper;
import logic.Sale;

@Repository
public class SaleDao {
	@Autowired
	private SqlSessionTemplate template;
	Class<SaleMapper> cls = SaleMapper.class;
	
	public int getMaxSaleId() { // saleid 최대값 조회
		return template.getMapper(cls).maxid();
		
		/*
		 * return template.queryForObject // 레코드 한건만 가져오셈
		 * ("select ifnull(max(saleid),0) from sale", param, Integer.class);
		 */
	}

	public void insert(Sale sale) { // sale 테이블에 테이터 추가
		template.getMapper(SaleMapper.class).insert(sale);
		
		/*
		 * SqlParameterSource param = new BeanPropertySqlParameterSource(sale); String
		 * sql = " insert into sale (saleid, userid, saledate)" +
		 * " values(:saleid, :userid, now() )"; template.update(sql, param);
		 */
	}

	public List<Sale> list(String userid) {
		return template.getMapper(SaleMapper.class).list(userid);
		
		/*
		 * String sql = "select * from sale where userid=:userid" +
		 * " order by saleid desc"; //최근 주문 순서로 조회 param.clear(); param.put("userid",
		 * userid); return template.query(sql,param,mapper);
		 */
	}

}
