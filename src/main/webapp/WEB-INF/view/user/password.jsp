<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- /shop1/src/main/webapp/WEB-INF/view/user/password.jsp -->
<%@ include file="/WEB-INF/view/jspHeader.jsp" %>  
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title> 비밀번호 수정 </title>
<script type="text/javascript">
	function inchk(f){
		if(f.chgpass.value != f.chgpass2.value){
			alert("변경 비밀번호와 변경 비밀번호 재입력이 다릅니다.");
			f.chgpass2.value="";
			f.chgpass2.focus();
			return false;
		}
		return true;
	}
</script>
</head>
<body>
<h2>비밀번호변경</h2>
<form action="password" method="post" name="f" onsubmit="return inchk(this)">
	<table class="w3-table">
		<tr><th>현재비밀번호</th><td><input type="password" name="password" class="w3-input"></td></tr>
		<tr><th>변경비밀번호</th><td><input type="password" name="chgpass"class="w3-input"></td></tr>
		<tr><th>변경재입력</th><td><input type="password" name="chgpass2"class="w3-input"></td></tr>
		<tr><td colspan="2"><input type="submit" value="비밀번호 변경"class="w3-input"></td></tr>
	</table>
</form>
</body>
</html>