package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import exception.LoginException;
import logic.Mail;
import logic.ShopService;
import logic.User;

/*
 * AdminController 의 모든 메서드들은 반드시 관리자로 로그인 해야만 실행가능함.
 * AOP 설정 : AdminLoginAspect 클래스. adminCheck 메서드
 * 	1. 로그아웃상태 : 로그인하세요 login페이지로 이동
 * 	2. 관리자 로그인이 아닌 경우 : 관리자만 가능한 거래입니다. mypage로이동
 */
@Controller
@RequestMapping("admin")
public class AdminController {
	@Autowired
	private ShopService service;
	
	@GetMapping("*") // * : 설정되지 않은 모든요청시 호출되는 메서드
	public ModelAndView join () {
		ModelAndView mav = new ModelAndView();
		mav.addObject(new User());
		return mav;
	}
	
	@RequestMapping("list")
	public ModelAndView list(String sort,HttpSession session) {
		ModelAndView mav = new ModelAndView();
		//list : db에 등록된 모든 회원 정보 저장목록
		List<User> list = service.userList(); //전체 회원목록 조회
		if(sort != null) {
			switch (sort) {
				case "10" : 
					Collections.sort(list,new Comparator<User>() {
						@Override
						public int compare(User u1, User u2) {
							return u1.getUserid().compareTo(u2.getUserid());
						}
					});
					break;
				case "11" : 
					Collections.sort(list,(u1,u2)->u2.getUserid().compareTo(u1.getUserid()));
					break;
				case "20" :
					Collections.sort(list,(u1,u2)->u2.getUsername().compareTo(u1.getUsername()));
					break;
				case "21" :
					Collections.sort(list,(u1,u2)->u1.getUsername().compareTo(u2.getUsername()));
					break;
				case "30" :
					Collections.sort(list,(u1,u2)->u2.getPhoneno().compareTo(u1.getPhoneno()));
					break;
				case "31" :
					Collections.sort(list,(u1,u2)->u1.getPhoneno().compareTo(u2.getPhoneno()));
					break;
				case "40" :
					Collections.sort(list,(u1,u2)->u2.getBirthday().compareTo(u1.getBirthday()));
					break;
				case "41" :
					Collections.sort(list,(u1,u2)->u1.getBirthday().compareTo(u2.getBirthday()));
					break;
				case "50" :
					Collections.sort(list,(u1,u2)->u2.getEmail().compareTo(u1.getEmail()));
					break;
				case "51" :
					Collections.sort(list,(u1,u2)->u1.getEmail().compareTo(u2.getEmail()));
					break;
			}
		}
		mav.addObject("list",list);
		return mav;
	}
	
	@RequestMapping("mailForm")
	public ModelAndView mailform(String[] idchks, HttpSession session) {
		// String[] idchks : idchks 파라미터의 값 여러개 가능. request.getParamaterValues("파라미터")
		ModelAndView mav = new ModelAndView("admin/mail");
		if(idchks == null || idchks.length == 0) {
			throw new LoginException("메일을 보낼 대상자를 선택하세용","list");
		}
		List<User> list = service.getUserList(idchks);
		mav.addObject("list",list);
		return mav;
	}
	   @RequestMapping("mail")
	//   @MSLogin("loginAdminCheck")
	public ModelAndView mailSend(Mail mail, HttpSession session) {
		 ModelAndView mav= new ModelAndView("alert");
		 Properties prop = new Properties();
		 try {
			 //mail.properties : resources 폴더에 생성
			 // java, resources 폴더의 내용은 : WEB-INF/classes 에 복사됨.
			FileInputStream fis = new FileInputStream
			(session.getServletContext().getRealPath("/")
							+"/WEB-INF/classes/mail.properties");
			prop.load(fis);
			prop.put("mail.smtp.user",mail.getNaverid());
		} catch (Exception e) {
			e.printStackTrace();
		}
		 maileSend(mail,prop);
		 mav.addObject("message","메일전송이 완료됨");
		 mav.addObject("url","list");
		 return mav;
	 }
	private void maileSend(Mail mail, Properties prop) {
		//auth : 인증객체
		MyAuthenticator auth = new MyAuthenticator
				(mail.getNaverid(),mail.getNaverpw());
		System.out.println(mail+"zzzzzz");
		//javax.mail
		Session session = Session.getInstance(prop,auth);
		MimeMessage msg = new MimeMessage(session);
		try {
			// 보내는사람메일
			msg.setFrom(new InternetAddress(mail.getNaverid()+"@naver.com"));
			// 받는사람메일정보
			List<InternetAddress> addrs = new ArrayList<InternetAddress>();
			String[] emails = mail.getRecipient().split(",");
			for(String email : emails) {
				try {
					addrs.add(new InternetAddress(new String(email.trim().getBytes("utf-8"),"8859_1")));
				} catch (UnsupportedEncodingException ue) {
					ue.printStackTrace();
				}
			}
			// 수신자에 해당하는 메일을 배열로 처리
			InternetAddress[] arr = new InternetAddress[emails.length];
			for(int i=0; i<addrs.size();i++) {
				arr[i] = addrs.get(i);
			}
			msg.setRecipients(Message.RecipientType.TO, arr);//수신자메일설정
			msg.setSentDate(new Date());// 전송일자
			msg.setSubject(mail.getTitle());// 제목
			MimeMultipart multipart = new MimeMultipart(); // 내용, 첨부파일...
			MimeBodyPart message = new MimeBodyPart(); // text형태
			message.setContent(mail.getContents(),mail.getMtype());
			multipart.addBodyPart(message);
			//첨부파일 파일 추가
			// mf : 첨부된 파일 중 한개 
			for(MultipartFile mf : mail.getFile1()) {  
				if ((mf != null) && (!mf.isEmpty())) {
					multipart.addBodyPart(bodyPart(mf));
				}
			}
			// msg : 메일전체객체 (전송메일주소, 수신메일주소, 제목, 전송일자, 내용, 첨부파일)
			// 내용, 첨부파일 : Multipart객체가 관리
			msg.setContent(multipart);
			Transport.send(msg);
		} catch (MessagingException me) {
			me.printStackTrace();
		}
	}
	private BodyPart bodyPart(MultipartFile mf) {
		MimeBodyPart body = new MimeBodyPart();
		String orgFile = mf.getOriginalFilename();
		String path = "c:/mailupload/"; //업로드된 파일 저장되는 공간
		File f1 = new File(path);
		if(!f1.exists()) f1.mkdirs();
		File f2 = new File(path+orgFile);
		try {
			mf.transferTo(f2); // 파일업로드
			body.attachFile(f2); // 이메일로 첨부
			//첨부된 파일의 파일명
			body.setFileName(new String(orgFile.getBytes("utf-8"),"8859_1"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return body;
	}
	//내부클래스 : private 사용가능 AdminCotroller에서만 사용가능
	   // final : 상속 불가 
	 private final class MyAuthenticator extends Authenticator {
	     private String id;
	     private String pw;
	     public MyAuthenticator(String id, String pw) {
	    	 this.id = id;
	    	 this.pw = pw;
	     }
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(id,pw);
		}
	 }
}
