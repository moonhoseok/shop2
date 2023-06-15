package dao;

import java.util.HashMap;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dao.mapper.ExchangeMapper;
import logic.Exchange;

@Repository
public class ExDao {
	@Autowired
	private SqlSessionTemplate template;
	private Map<String, Object> param = new HashMap<>();
	private Class<ExchangeMapper> cls = ExchangeMapper.class;
	
	public void insert(Exchange ex) {
		template.getMapper(cls).insert(ex);
	}
}
