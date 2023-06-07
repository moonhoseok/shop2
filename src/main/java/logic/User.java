package logic;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class User {
	@Size(min=3,max=10,message="아이디는 3자이상 10자이하로 입력하세요")
	private String userid;
	@Size(min=3,max=10,message="비밀번호는 3자이상 10자이하로 입력하세요")
	private String password;
	@NotEmpty(message="이름 입력하셈")
	private String username;
	private String phoneno;
	private String postcode;
	private String address;
	@NotEmpty(message="email 입력하셈") // 빈문자열, null, 
	@Email(message="email 형식 맞추셈")
	private String email;
	@NotNull(message="생일을 쫌! 입력하세요")
	@Past(message = "생일은 과거만 가능")
	// 입력받은 형식을 format에 맞춰서 Date 타입으로 변환
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthday;
	
}
