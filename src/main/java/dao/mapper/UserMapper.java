package dao.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import logic.User;

public interface UserMapper {
	
	@Insert("insert into usersecurity (userid,username,password,"
			+" birthday, phoneno, postcode, address, email, channel) values"
			+" (#{userid},#{username},#{password},#{birthday},"
			+ "#{phoneno},#{postcode},#{address},#{email},#{channel})")
	void insert(User user);

	@Select({"<script>",
			"select * from usersecurity ",
			"<if test = 'userid != null'> where userid =#{userid}</if>",
			"<if test = 'userids != null'> where userid in "
			+"<foreach collection ='userids' item='id' separator=',' "
			+ "open='(' close=')'> #{id}"
			+ "</foreach> </if>",
			"</script>"})
	List<User> select(Map<String, Object> param);

	@Update("update usersecurity set username=#{username}, birthday=#{birthday},"
		+ " phoneno=#{phoneno},postcode=#{postcode}, address=#{address},"
		+ " email=#{email} where userid=#{userid}")
	void update(User user);

	@Delete("delete from usersecurity where userid=#{userid}")
	void delete(Map<String,Object> param);

	@Update("update usersecurity set password=#{chgpass} where userid=#{userid}")
	void pwupdate(Map<String, Object> param);

	@Select({"<script>",
			"select ${col} from usersecurity where email=#{email} and phoneno=#{phoneno}",
			"<if test='userid != null'> and userid =#{userid} </if>",
			"</script>"})
	String search(Map<String, Object> param);

	@Select("select * from usersecurity where phoneno = #{value}")
	List<User> phoneList(String phoneno);
}
