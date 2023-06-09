package config;

import java.util.Properties;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import interceptor.BoardInterceptor;
// 내가바로 환경설정 파일이다!
@Configuration// xml 방식의 설정을 대신해주는 자바클래스
@ComponentScan(basePackages= {"controller","logic","dao","aop","websocket","util"})
@EnableAspectJAutoProxy // AOP관련 어노테이션 사용
@EnableWebMvc // MVC관련 어노테이션 사용 . 유효성검증 사용
public class MvcConfig implements WebMvcConfigurer{
	@Bean
	public HandlerMapping handlerMapping() { // 요청 url 
		RequestMappingHandlerMapping hm = new RequestMappingHandlerMapping();
		hm.setOrder(0);
		return hm;
	}
	
	@Bean
	public ViewResolver viewResolver() { // view 결정자 
		InternalResourceViewResolver vr = new InternalResourceViewResolver();
		vr.setPrefix("/WEB-INF/view/");
		vr.setSuffix(".jsp");
		return vr;
	}
	
	// messages.properties 파일의 코드값으로 message처리
	@Bean 
	public MessageSource messageSource() {  
		ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
		ms.setBasename("messages");
		return ms;
	}
	// 파일 업로드를 위한 설정
	@Bean
	public MultipartResolver multipartResolver() { 
		CommonsMultipartResolver mr = new CommonsMultipartResolver();
		mr.setMaxInMemorySize(10485760); // 업로드시 가능한 메모리 크기 지정
		mr.setMaxUploadSize(104857600); // 최대업로드 가능파일의 크기 지정
		return mr;
	}

	
	// 예외처리
	@Bean
	public SimpleMappingExceptionResolver exceptionHandler() {
		SimpleMappingExceptionResolver ser = new SimpleMappingExceptionResolver();
		Properties pr = new Properties();
		//       발생되는 예외클래스 					 호출되는 view
		pr.put("exception.CartEmptyException", "exception");
		pr.put("exception.LoginException", "exception");
		pr.put("exception.BoardException", "exception");
		ser.setExceptionMappings(pr);
		return ser;
	}
	// 인터셉터관련 설정
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new BoardInterceptor())
		.addPathPatterns("/board/write")
		.addPathPatterns("/board/update")
		.addPathPatterns("/board/delete");
	}
	
	// 사진업로드 : 기본 웹 파일처리를 위한 설정
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
}
