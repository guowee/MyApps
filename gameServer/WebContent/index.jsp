<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta charset="UTF-8">
  <title>排行榜</title>

<style type="text/css">
body{
	margin:0px;
	width:100%;
	height:100%;
}
.container{
	position:absolute;
	top:50%;
	left:50%;
	width:500px;
	height:200px;
}
.form{
	position:relative;
	top:-50%;
	left:-50%;
	width:100%;
	height:100%;
	font-size: 20px;
}
#end_date{
	margin-top: 5%;
}
#submit{
	margin-top: 5%;
}
.Wdate{
	border:#999 1px solid;
	height:20px;
	background:#fff url(datePicker.gif) no-repeat right;
}
.Wdate::-ms-clear{display:none;}

.WdateFmtErr{
	font-weight:bold;
	color:red;
}
</style>
<script type="text/javascript" src="js/jquery-1.11.1.js"></script>
<script language="javascript" type="text/javascript" src="My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
$(function(){
	
	var d = new Date();  
	var str = d.getFullYear()+"-"+(d.getMonth()+1)+"-"+d.getDate();
	var str2 = d.getFullYear()+"-"+(d.getMonth()+1)+"-"+(d.getDate()+1);
	 
	var start=$("#start_date").val();
	var end=$("#end_date").val();
	if(start=="" || end==""){
		$("#start_date").val(str);
		$("#end_date").val(str2);
	}
	
	$("#submit").click(function(){
		var start=$("#start_date").val();
		var end=$("#end_date").val();
		start=$.trim(start);
		end=$.trim(end);
		
		if(start=="" || end==""){
			alert("请选择时间段");
			return false;
		}else if(end<start){
			alert("请选择正确的时间段");
			return false;
		}
	});
});
</script>
</head>
<body>
<!-- <select id="period">
		<option value="de" selected = "selected">请选择时间段</option>
		<option value="0">20-22</option>
		<option value="1">22-24</option>
</select>
<button id="button">该时段排名</button>

<p id="feedback"></p>
<div class="biankuang">
      <table class="table" id="tb">
        <thead>
          <tr>
            <th>排名</th>
            <th>分数</th>
            <th>姓名</th>
          </tr>
        </thead>       
        <tbody id="content">
         	
        </tbody>
      </table>
</div> -->
<h1 align="center">注意:默认日期为当前日期。如果选择的开始日期和结束日期分别为2014-12-20和2014-12-21，则查询的为<br>2014年12月20日0时0分0秒到2014年12月20日23时59分59秒的榜单数据。</h1>

<div class="container">



<form action="UserRank/getRankByTimeOrign.do" method="POST" align="center" class="form">
	开始日期: <input class="Wdate" id="start_date" type="text" name="start_date" onClick="WdatePicker()"> <br>
	结束日期: <input class="Wdate" id="end_date" type="text" name="end_date" onClick="WdatePicker()"> <br>
	<input type="submit" value="获取该时段榜单" id="submit">
</form>
	


</div>
</body>
</html>