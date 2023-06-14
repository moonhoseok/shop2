package controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("naver")
public class naverContoller {
	// localhost:8080/nvaer/search 요청 => /WEB-INF/view/nvaer/search.jsp 뷰
	@GetMapping("*")
	public String naver() {
		return null; // 뷰 이름 . null : url과 같은 이름의 뷰를 선택
	}
	
	 // /naver/search.jsp 페이지에서 ajax로 요청됨. 뷰없이 직접 json 객체로 전달
	@RequestMapping("naversearch")
	@ResponseBody // 뷰없이 바로 데이터를 클라이언트로 전송
	public JSONObject naversearch(String data, int display, int start, String type) {
		//JSONObject jsonData = null;
		String clientId = "H5OOG6wmI8ivpoz44F3v"; // 애플리케이션 클라이언트 아이디값;
		String clientSecret="h6HTq4xdti";		// 애플리케이션 클라이언트 시크릿값;
		StringBuffer json = new StringBuffer();
		int cnt =((start)-1)* (display)+1; // 네이버에 요청시작 건수
		try {
			String text = URLEncoder.encode(data,"utf-8");
			String apiURL = "https://openapi.naver.com/v1/search/"+type+".json?query="
					+text+"&display="+display+"&start="+cnt; // json 결과
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("X-Naver-Client-Id", clientId);
			con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
			int responseCode = con.getResponseCode(); // 네이버에서 결과코드
			BufferedReader br; // 네이버가 전송한 데이터. 네이버에서 수신된 데이터 
			if(responseCode == 200){ // 정상응답. 검색결과 수신
				// con.getInputStream() : 입력스트림. 네이버데이터 수신.
				br = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));
			}else{ // 에러발생. 검색시 오류발생.
				// con.getErrorStream() : 입력스트림. 네이버데이터 수신 
				br = new BufferedReader(new InputStreamReader(con.getErrorStream(),"utf-8"));
			}
			String inputLine;
			// readLine() : 한줄입력
			while((inputLine = br.readLine())!= null){
				json.append(inputLine); // 최종 응답 코드 : StringBuffer 객체에 내용 추가 
			}
			br.close();
			
			//JSONParser jsonparser = new JSONParser();
			//jsonData = (JSONObject)jsonparser.parse(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// stringbuffer json : 동적 문자열 객체. 네이버에서 전송한 JSON형태의 문자열 데이터
		JSONObject jsonObj = null;
		JSONParser jsonparser = new JSONParser();
		try {
			// json.toString() : String 객체. 문자열 객체
			// jsonObj : json 객체 
			jsonObj = (JSONObject)jsonparser.parse(json.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return jsonObj;
	}
}
