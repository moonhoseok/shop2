package dao.mapper;

import java.util.List;

import javax.validation.Valid;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


import logic.Comment;

public interface CommentMapper {

	@Select("select ifnull(max(seq),0) from comment where num = #{num}")
	int maxseq(int num);
	
	@Insert("insert into comment"
			+" (num, seq, writer, pass, content, regdate) "
			+ "values (#{num}, #{seq}, #{writer}, #{pass}, #{content}, now())")
	void insert(Comment comm);

	@Select("select * from comment where num=#{num} order by seq desc" )
	List<Comment> list(Integer num);

	@Delete("delete from comment where num=#{num} and seq=#{seq} and pass=#{pass}")
	void delete(@Param("num")int num,@Param("seq")int seq, @Param("pass")String pass);

	@Select("select * from comment where num=#{num} and seq=#{seq}")
	Comment selectOne(@Param("num")int num,@Param("seq")int seq);

	

}
