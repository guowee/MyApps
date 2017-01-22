<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta charset="UTF-8">
  <title>排行榜</title>
  <style>
html{font-family:sans-serif;-ms-text-size-adjust:100%;-webkit-text-size-adjust:100%}
body{margin:0}
article,aside,details,figcaption,figure,footer,header,hgroup,main,menu,nav,section,summary{display:block}
audio,canvas,progress,video{display:inline-block;vertical-align:baseline}
audio:not([controls]){display:none;height:0}[hidden],template{display:none}a{background-color:transparent}a:active,a:hover{outline:0}abbr[title]{border-bottom:1px dotted}b,strong{font-weight:bold}dfn{font-style:italic}h1{font-size:2em;margin:0.67em 0}mark{background:#ff0;color:#000}small{font-size:80%}sub,sup{font-size:75%;line-height:0;position:relative;vertical-align:baseline}sup{top:-0.5em}sub{bottom:-0.25em}img{border:0}svg:not(:root){overflow:hidden}figure{margin:1em 40px}hr{-moz-box-sizing:content-box;-webkit-box-sizing:content-box;box-sizing:content-box;height:0}pre{overflow:auto}code,kbd,pre,samp{font-family:monospace, monospace;font-size:1em}button,input,optgroup,select,textarea{color:inherit;font:inherit;margin:0}button{overflow:visible}button,select{text-transform:none}button,html input[type="button"],input[type="reset"],input[type="submit"]{-webkit-appearance:button;cursor:pointer}button[disabled],html input[disabled]{cursor:default}button::-moz-focus-inner,input::-moz-focus-inner{border:0;padding:0}input{line-height:normal}input[type="checkbox"],input[type="radio"]{-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;padding:0}input[type="number"]::-webkit-inner-spin-button,input[type="number"]::-webkit-outer-spin-button{height:auto}input[type="search"]{-webkit-appearance:textfield;-moz-box-sizing:content-box;-webkit-box-sizing:content-box;box-sizing:content-box}input[type="search"]::-webkit-search-cancel-button,input[type="search"]::-webkit-search-decoration{-webkit-appearance:none}fieldset{border:1px solid #c0c0c0;margin:0 2px;padding:0.35em 0.625em 0.75em}legend{border:0;padding:0}textarea{overflow:auto}optgroup{font-weight:bold}table{border-collapse:collapse;border-spacing:0}td,th{padding:0}

</style>
  <!-- <link rel='stylesheet prefetch' href='css/bootstrap.min.css'> -->
    <style>

.container{
	margin:0 auto;
	width:800px;
}
.table {
/* width:960px; */
	margin-top: 12px;
  border:solid 5px #000000;
/* -moz-border-radius-topleft: 26px;
-moz-border-radius-topright:26px;
-moz-border-radius-bottomleft:26px;
-moz-border-radius-bottomright:26px;
-webkit-border-top-left-radius:26px;
-webkit-border-top-right-radius:26px;
-webkit-border-bottom-left-radius:26px;
-webkit-border-bottom-right-radius:26px;
border-top-left-radius:26px;
border-top-right-radius:26px;
border-bottom-left-radius:26px;
border-bottom-right-radius:26px;
 */
background:-webkit-gradient(linear, 72% 20%, 10% 21%, from(#00BFFF), to(#629BFF));
background: -ms-linear-gradient(#00BFFF 0%,#629BFF 100%);
background:-moz-linear-gradient(#00BFFF 0%,#629BFF 100%);
width:800px;
}
/* 
.biankuang{
	border:2px solid;
	border-radius:25px;
	background:-webkit-gradient(linear, 72% 20%, 10% 21%, from(#00BFFF), to(#629BFF));
} */


@-moz-document url-prefix() {
  fieldset {
    display: table-cell;
  }
}
tr:nth-child(odd) {
  background: WhiteSmoke;
}

thead tr {
  background: SlateGray !important;
  color: WhiteSmoke;
}

th {
  font-family: 'Black Ops One', cursive;
  padding: 5px 0;
}

tr {
  font-family: 'Boogaloo', cursive;
  font-size: 150%;
  color: black;
  text-align: center;
}
tr td:nth-child(1) {
  width: 25%;
}
tr td:nth-child(2) {
  width: 25%;
}
tr td:nth-child(3) {
	font-weight:bold;
  width: 25%;
}
tr td:nth-child(4) {
  width: 25%;
}
tr th:nth-child(1) {
  width: 25%;
}
tr th:nth-child(2) {
  width: 25%;
}
tr th:nth-child(3) {
  width: 25%;
}
tr th:nth-child(4) {
  width: 25%;
}


.circle {
  font-family: 'Black Ops One', cursive;
  font-size: 130%;
  width: 35px;
  height: 32px;
  background: DimGray;
  color: white;

  text-align: center;
  border-radius: 100%;
  -moz-border-radius: 100%;  
  -webkit-border-radius: 100%;  
}

.first {
  background: Coral;
}

.second {
  background: Goldenrod;
}

.third {
  background: Red;
}

h2{
	font-weight: bold;
}

</style>

<script type="text/javascript" src="js/jquery-1.11.1.js"></script>
<script type="text/javascript">

</script>
</head>
<body>
<div class="container">
<h2 align="center">${start_date} 00:00:00 至${end_date} 23:59:59 排名</h2>
<div class="biankuang">
      <table class="table" id="tb">
        <thead>
          <tr>
            <th>排名</th>
            <th>分数</th>
            <th>昵称</th>
            <th>手机号</th>
          </tr>
        </thead>       
        <tbody id="content">
         	<c:forEach items="${userBalls }" var="userBall" varStatus="status">
         		<tr>
         			<c:choose>
         				<c:when test="${status.index==0 || status.index==3 || status.index==6 || status.index==9}">
		         				<td align="center"><div class="circle first">${status.index + 1}</div></td>
			         			<td>${userBall.score}</td>
			         			<td>${userBall.nickname}</td>
			         			<td>${userBall.phone}</td>
         				</c:when>
         				<c:when test="${status.index==1 || status.index==4 || status.index==7}">
		         				<td align="center"><div class="circle second">${status.index + 1}</div></td>
			         			<td>${userBall.score}</td>
			         			<td>${userBall.nickname}</td>
         				</c:when>
         				<c:when test="${status.index==2 || status.index==5 || status.index==8}">
		         				<td align="center"><div class="circle third">${status.index + 1}</div></td>
			         			<td>${userBall.score}</td>
			         			<td>${userBall.nickname}</td>
         				</c:when>
         				<c:otherwise>
	         				<td>${status.index + 1}</td>
		         			<td>${userBall.score}</td>
		         			<td>${userBall.nickname}</td>
         				</c:otherwise>
         			</c:choose>
         		</tr>
         	</c:forEach>
        </tbody>
      </table>
</div>
</div>
</body>
</html>