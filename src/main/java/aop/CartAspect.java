package aop;


import javax.servlet.http.HttpSession;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import exception.CartEmptyException;
import exception.LoginException;
import logic.Cart;
import logic.User;

@Component
@Aspect
public class CartAspect {
	/* pointcut : 
	 * CartController 클래스의 매개변수의 마지막이 HttpSession인 check로 시작하는 메서드
	 */
	@Before("execution(* controller.cart*.check*(..)) && args(..,session)")
	public void cartCheck(HttpSession session)throws Throwable{
		User loginUser = (User)session.getAttribute("loginUser");
		System.out.println(loginUser);
		if(loginUser == null) {
			throw new LoginException("회원만가능", "../user/login");
		}
		Cart cart =(Cart)session.getAttribute("CART");
		if(cart ==null || cart.getItemSetList().size() ==0) {
			throw new CartEmptyException("장바구니 비었음", "../item/list");
		}
	}
}
