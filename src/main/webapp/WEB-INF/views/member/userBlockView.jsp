<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<script src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	//유효성체크 (공백입력 방지)
	$("#submit").on("click", function(){
		if($("#pw").val()==""){
			alert("비밀번호를 입력해주세요.");
			$("#pw").focus();
			return false;
		}
	});
})
</script>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원 탈퇴 (블락처리)</title>

</head>
<body>
	<%@include file="../header.jsp"%>
	<h3 class="title">회원 탈퇴</h3>
	<form action="/member/userBlock" method="post">
		<div>
			<input type="hidden" id="userId" name="userId" placeholder="아이디" value="${login.userId}"/>
		</div>
		<div>
			<label for="pw">패스워드를 입력해주세요</label>
			<input type="password" id="pw" name="pw" placeholder="패스워드" />
		</div>
		<div>
			<button type="submit" id="submit">회원탈퇴</button>
		</div>
	</form>
	<div>
		<c:if test="${msg == false}">
			<script type="text/javascript">
				alert("비밀번호가 틀렸습니다");
			</script>
		</c:if>
	</div>
</body>
</html>