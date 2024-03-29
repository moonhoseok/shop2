package controller;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import exception.CartEmptyException;
import exception.LoginException;
import logic.Cart;
import logic.Item;
import logic.ItemSet;
import logic.Sale;
import logic.ShopService;
import logic.User;

@Controller
@RequestMapping("cart")
public class cartController {
	@Autowired
	private ShopService service;
	
	@RequestMapping("cartAdd")
	public ModelAndView add (Integer id, Integer quantity, 
			HttpSession session) {
		ModelAndView mav = new ModelAndView("cart/cart");
		Item item = service.getItem(id);
		Cart cart = (Cart)session.getAttribute("CART");
		if(cart ==null) {
			cart = new Cart();
			session.setAttribute("CART", cart);
		}
		cart.push(new ItemSet(item,quantity));
		mav.addObject("message",item.getName()+":"+quantity+"개 장바구니 추가");
		mav.addObject("cart",cart);
		return mav;
	}
	
	@RequestMapping("cartDelete")
	public ModelAndView delete(int index, HttpSession session) {
		ModelAndView mav = new ModelAndView("cart/cart");
		Cart cart = (Cart)session.getAttribute("CART");
		ItemSet robj = cart.getItemSetList().remove(index);
		mav.addObject("message",robj.getItem().getName()+"삭제!!");
		mav.addObject("cart",cart);
		return mav;
	}
	
	@RequestMapping("cartView")
	public ModelAndView cartView (HttpSession session) {
		ModelAndView mav = new ModelAndView("cart/cart");
		Cart cart = (Cart)session.getAttribute("CART");
		mav.addObject("cart",cart);
		return mav;
	}
	/*
	 * 주문 전 확인 페이지
	 * 1. 장바구니에 상품이 존재.
	 * 		상품이 없는 경우 : CartEmptyException 예외 발생
	 * 2. 로그인 된 상태여야 함.
	 * 		로그아웃 상태 : LoginException 예외 발생
	 * 		- exception.LoginException 예외틀래스 생성.
	 * 		- 예외 발생시 exception.jsp로 페이지 이동
	 */
	@RequestMapping("checkout")
	public String checkout(HttpSession session) {
	/*	Cart cart = (Cart)session.getAttribute("CART");
		// cart == null : session에 CART 이름의 속성이 없는경우
		// cart.getItemSetList().size() == 0 : CART 속성은 존재. CART 내부에 상품정보가 없는경우
		if(cart == null || cart.getItemSetList().size() == 0) {
			// throw : 강제 예외 발생
			throw new CartEmptyException("장바구니에 상품업음", "../item/list");
		}
		User loginUser = (User)session.getAttribute("loginUser");
		if(loginUser == null) {
			throw new LoginException("로그인하세요","../item/list");
		}*/
		return null; // view의 이름 리턴. null인 경우 url과 같은 이름을 호출
					// WEB-INF/view/cart/checkout.jsp
	}
	/*
	 *  kakao 결제 : ajax으로 요청됨. => 
	 *  	pg : "kakaopay",					// 상점구분, 카카오페이
 			pay_method : "card",				// 카드 결제
 			merchant_uid : json.merchant_uid,	// 주문번호 : 주문별로 유일한 값이 필요. userid-session id 값
 			name : json.name,					// 주문상품명. 사과 외 3건
 			amount : json.amount,				// 주문금액.
 			buyer_email : "ans4649@naver.com"	// 주문자 이메일 테스트.
 			buyer_name : json.buyer_name,		// 주문자 성명
 			buyer_tel : json.buyer_tel,			// 주문자전화번호
 			buyer_addr : json.buyer_addr,		// 주문자 주소
 			buyer_postcode : json.buyer_postcode// 주문자 우편번호
	 */
	@RequestMapping("kakao")
	@ResponseBody
	public Map<String, Object> kakao(HttpSession session){
		Map<String, Object> map = new HashMap<>();
		
		Cart cart = (Cart)session.getAttribute("CART");
		User loginUser = (User)session.getAttribute("loginUser");
		map.put("merchant_uid", loginUser.getUserid()+"-"+session.getId());
		map.put("name", cart.getItemSetList().get(0).getItem().getName()
				+"외"+(cart.getItemSetList().size()-1));
		map.put("amount", cart.getTotal());
		String email = service.emailDecrypt(loginUser);
		map.put("buyer_email", email); // 현재사용안함. 복호화필요
		map.put("buyer_name", loginUser.getUsername());
		map.put("buyer_tel", loginUser.getPhoneno());
		map.put("buyer_addr", loginUser.getAddress());
		map.put("buyer_email", loginUser.getPostcode());
		return map; // 클라이언트는 json 객체로 전달
	}
	
	/*
	 * 주문확정
	 * 1. 로그인상태, 장바구니 상품존재 => aop로 설정
	 * 2. 장바구니상품 saleitem 테이블에 저장. 주문정보(sale)테이블 저장
	 * 3. 장바구니 상품 제거
	 * 4. end.jsp 페이지에서 sale, saleitem 데이터 조회
	 */
	@RequestMapping("end")
	public ModelAndView checkend(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		Cart cart = (Cart)session.getAttribute("CART");
		User loginUser = (User)session.getAttribute("loginUser");
				// sale : 주문정보, 아이디정보, 상품목록, 상품정보
		Sale sale = service.checkend(loginUser, cart); //2
		session.removeAttribute("CART"); //3
		mav.addObject("sale",sale);
		return mav;
	}
}
