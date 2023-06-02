package controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.jasper.tagplugins.jstl.core.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import exception.BoardException;
import exception.LoginException;
import logic.Board;
import logic.ShopService;

@Controller
@RequestMapping("board")
public class BoardController {
	@Autowired
	private ShopService service;
	
	@GetMapping("*") // 설정되지 않은 모든 요청시 호출되는 메서드 
	public ModelAndView write() {
		ModelAndView mav = new ModelAndView();
		mav.addObject(new Board());
		return mav;
	}
	/*
	 * 1. 유효성 검증
	 * 2. db의 board 테이블에 내용 저장, 파일 업로드
	 * 3. 등록 성공 : list 요청
	 *    등록 실패 : write 요청
	 */
	@PostMapping("write")
	public ModelAndView writePost(@Valid Board board, BindingResult bresult,
			HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		if(bresult.hasErrors()) {
			mav.getModel().putAll(bresult.getModel());
			return mav;
		}
		String boardid = (String)request.getSession().getAttribute("boardid");
		if(boardid == null) boardid = "1";
		request.getSession().setAttribute("boardid", boardid);
		board.setBoardid(boardid);
		service.boardWrite(board, request);
		mav.setViewName("redirect:list?boardid="+boardid);
		return mav;
	}
	/*
	 * @RequestParam : 파라미터값을 객체와 맵핑하여 저장하는 기능 
	 * 	 파라미터값 저장 
	 * 		1. 파라미터이름과 매개변수이름이 같은경우
	 * 		2. Bean클래스 객체의 프로퍼티 이름과 파라미터이름이 같은경우
	 * 		3. Map 객체를 이용하는 경우 <= 이 방식 사용
	 * 
	 * 검색기능 추가
	 */
	@RequestMapping("list")
	public ModelAndView list (@RequestParam Map<String,String> param,
												HttpSession session) {
		System.out.println(param);
		String column = param.get("searchtype");
		String find = param.get("searchcontent");
		System.out.println("찾기 : "+column + find);
		// 검색타입 단어 여부판단
		if(column == null || column.trim().equals("")) {
	    	column = null;
	    	find = null;
	    }
	    if(find == null || find.trim().equals("")) {
	    	column = null;
	    	find = null;
	    }
		Integer pageNum = null;
		if(param.get("pageNum") != null) {
			pageNum = Integer.parseInt(param.get("pageNum"));
		}
		String boardid = param.get("boardid");
		ModelAndView mav = new ModelAndView();
		if(pageNum ==null || pageNum.toString().equals("")) {
			pageNum =1;
		}
		if(boardid == null || boardid.equals("")) {
			boardid = "1";
		}
		session.setAttribute("boardid", boardid);
		String boardName = null;
		switch(boardid) {
			case "1" : boardName = "공지사항"; break;
			case "2" : boardName = "자유게시판"; break;
			case "3" : boardName = "QnA"; break;	
		}
		int limit = 10; // 한페이지당 보여줄 게시물 건수
		int listcount = service.boardcount(boardid,column,find); //등록된 게시물 건수
		// boardlist : 현재 페이지에 보여줄 게시물 목록
		List<Board> boardlist = service.boardlist(pageNum,limit,boardid,column,find);
		//페이징 처리를 위한 값들
		int maxpage = (int)((double)listcount/limit+0.95); //등록 건수에 따른 최대 페이지 수
		int startpage = (int)((pageNum/10.0 +0.9)-1) *10 +1; // 페이지의 시작 번호
		int endpage = startpage +9; // 페이지의 끝 번호 
		if(endpage > maxpage) endpage = maxpage;	// 페이지의 끝 번호는 최대 페이지보다 작다.
		int boardno = listcount - (pageNum-1)*limit; //화면에 보여지는 게시물 번호 
		String today = new SimpleDateFormat("yyyyMMdd").format(new Date());// 오늘 날짜를 문자열로 저장
		mav.addObject("boardid",boardid);
		mav.addObject("boardName",boardName);
		mav.addObject("pageNum",pageNum);
		mav.addObject("maxpage",maxpage);
		mav.addObject("startpage",startpage);
		mav.addObject("endpage",endpage);
		mav.addObject("listcount",listcount);
		mav.addObject("boardlist",boardlist);
		mav.addObject("boardno",boardno);
		mav.addObject("today",today);
		return mav;
	}
	
	@RequestMapping("detail")
	public ModelAndView detail (Integer num){
		ModelAndView mav = new ModelAndView();
		Board board = service.getBoard(num);
		service.addReadcnt(num); // 조회수 1증가
		if(board.getBoardid() ==null || board.getBoardid().equals("1")) {
			mav.addObject("boardName","공지사항");
		}else if(board.getBoardid().equals("2")) {
			mav.addObject("boardName","자유게시판");
		}else if(board.getBoardid().equals("3")) {
			mav.addObject("boardName","QnA");
		}
		mav.addObject("board",board);
		return mav;
	}
	
	@GetMapping({"reply","update","delete"})
	public ModelAndView replyform(Integer num) {
		ModelAndView mav = new ModelAndView();
		Board board = service.getBoard(num);
		if(board.getBoardid() ==null || board.getBoardid().equals("1")) {
			mav.addObject("boardName","공지사항");
		}else if(board.getBoardid().equals("2")) {
			mav.addObject("boardName","자유게시판");
		}else if(board.getBoardid().equals("3")) {
			mav.addObject("boardName","QnA");
		}
		mav.addObject("board",board);
		return mav;
	}
	/*
	 * 1. 유효성 검사하기-파라미터값 저장.
	 * 	- 원글정보 : num,grp,grpstep,grplevel,boardid
	 * 	- 답글정보 : writer, pass, title, content
	 * 2. db에 insert => service.boardReply()
	 * 	- 원글의 grpstep보다 큰 이미 등록된 답글의 grpstep 값을 +1
	 * 		=> boardDao.grpstepAdd()
	 * 	- num : maxNum()+1
	 * 	- db에 insert => boardDao.insert()
	 * 	  grp : 원글과 동일
	 *    grplevel : 원글의 grplevel +1
	 *    grpstep : 원글dml grpstep +1
	 * 3. 등록성공 : list로 이동
	 * 	  등록실패 : "답변등록오류발생" reply페이지로 이동
	 */
	
	/*@PostMapping("reply")
	public ModelAndView reply (@Valid Board board, BindingResult bresult,
			@RequestParam Map<String,String> param) {
		ModelAndView mav = new ModelAndView();
		if(bresult.hasErrors()) {
			mav.getModel().putAll(bresult.getModel());
			return mav;
		}
		Board rboard = service.boardReply(param);
		int num = Integer.parseInt(param.get("num"));
		if(rboard == null) {
			throw new LoginException("답글등록 실패", "reply?num="+num);
		}
		return mav;
	}*/
	@PostMapping("reply")
	public ModelAndView reply(@Valid Board board, BindingResult bresult) {
		ModelAndView mav = new ModelAndView();
		
		if(board.getBoardid() ==null || board.getBoardid().equals("1")) {
			mav.addObject("boardName","공지사항");
		}else if(board.getBoardid().equals("2")) {
			mav.addObject("boardName","자유게시판");
		}else if(board.getBoardid().equals("3")) {
			mav.addObject("boardName","QnA");
		}
		//유효성 검증 bresult 내부적으로 board객체를 가지고있음
    	if(bresult.hasErrors()) {
    		Board dbboard = service.getBoard(board.getNum()); //원글 정보를 db에서 읽기
    		Map<String, Object> map = bresult.getModel();
    		Board b = (Board)map.get("board");// 화면에서 입력받은 값을 저장한 Board객체
    		b.setTitle(dbboard.getTitle());//원글의 제목으로 변경
			mav.getModel().putAll(bresult.getModel());
			return mav;
    	}
    	try {
     	   service.boardReply(board);
     	   mav.setViewName("redirect:list?boardid="+board.getBoardid());
     	} catch(Exception e) {
     		e.printStackTrace();
     		throw new LoginException("답변등록시 오류 발생","reply?num="+board.getNum());
     	}
 	    return mav;    	
	}
	/*
	 * 1. 유효성 검증.
	 * 2. 비밀번호 검증 => 검증오류 : 비번틀림 메세지출력 후 update로 이동
	 * 3. db에 내용 수정. 업로드된 파일이 있는 경우 파일업로드
	 * 4. 수정완료 : detail 페이지 이동
	 * 	  수정실패 : 수정실패 메시지 출력후 update로 이동
	 */
	@PostMapping("update")
	public ModelAndView update(@Valid Board board, BindingResult bresult,
			HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		//1
		if(bresult.hasErrors()) {
			mav.getModel().putAll(bresult.getModel());
			return mav;
		}
		//2
		Board getboard = service.getBoard(board.getNum());
		System.out.println(getboard.getPass() +"=="+ board.getPass());
		// 데이터베이스 비밀번호 == 입력된 비밀번호
		if(!getboard.getPass().equals(board.getPass())) {
			throw new BoardException("비밀번호 틀렸습니다.","update?num="+board.getNum());	
		}
		//3,4
		try{
			service.boardUpdate(board,request); //파일업로드,db게시글 수정
			mav.setViewName("redirect:detail?num="+board.getNum());
		} catch (Exception e) {
			e.printStackTrace();
     		throw new BoardException("게시글 수정 오류발생","update?num="+board.getNum());
		}
		return mav;
	}
	/*
	 * 1. num,pass 파라미터 저장=> 매개변수처리
	 * 2. 비밀번호 검증 : db에서 num게시글 조회. db에 등록된 비밀번호와 입력비밀번호 비교
	 * 		비밀번호 오류 : error.board.password코드값 설정 => delete.jsp로 전달
	 * 3. 비밀번호 일치 : db에서 num게시글 삭제
	 * 		삭제성공 : list 페이지
	 * 		삭제실패 :
	 */
	@PostMapping("delete")
	public ModelAndView delete(Board board, BindingResult bresult) {
		ModelAndView mav = new ModelAndView();
		if(board.getPass() == null || board.getPass().trim().equals("")) {
			bresult.reject("error.required.password");
			return mav;
		}
		Board getboard = service.getBoard(board.getNum());
		System.out.println(getboard.getPass() +"=="+ board.getNum());
		// 데이터베이스 비밀번호 == 입력된 비밀번호
		if(!getboard.getPass().equals(board.getPass())) {
			bresult.reject("error.board.password");
			return mav;
		}
		try{
			service.boardDelete(board.getNum()); //파일업로드,db게시글 수정
			mav.setViewName("redirect:list?boardid="+getboard.getBoardid());
		} catch (Exception e) {
			e.printStackTrace();
     		bresult.reject("error.board.fail");
		}
		return mav;
	}
	
	@RequestMapping("imgupload")
	public String imgupload(MultipartFile upload, String CKEditorFuncNum,
			HttpServletRequest request, Model model) {
		/*반드시 upload여야함
		 * upload : CKEditor 모듈에서 업로드되는 이미지의 이름.
		 * 			업로드 되는 이미지파일의 내용. 이미지값
		 * CKEditorFuncNum : CKEditor 모듈에서 파라미터로 전달되는 값.
		 * model : ModelAndView 중 Model에 해당하는 객체
		 * 			뷰에 전달할 데이터 정보 저장할 객체
		 * return 타입이 String : 뷰의 이름
		 */
		//업로드되는 위치 폴더 = path
		//request.getServletContext().getRealPath("/") : 웹어플리케이션의 절대경로값.
		String path = request.getServletContext().getRealPath("/")+"board/imgfile/";
		service.uploadFileCreate(upload,path);//upload(파일의내용), path(업로드되는 폴더)
		//request.getContextPath() : 프로젝트명(웹어플리케이션이름). shop1/
		//비글사진
		//http://localhost:8080/shop1/board/imgfile/비글.jpeg
		String fileName = request.getContextPath()// 웹어플리케이션 경로. 웹url정보
				+"/board/imgfile/"+upload.getOriginalFilename();
		model.addAttribute("fileName",fileName);
		return "ckedit"; // view이름. /WEB-INF/view/ckedit.jsp
	}
}
