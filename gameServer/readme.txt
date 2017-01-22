特效：
1.具备默认的页面定向转发支持. http://localhost:8080/mvc/UserRank/userUpload.do
2.具备Spring MVC的json返回支持. http://localhost:8080/mvc/UserRank/getUserRank.do
3.暂时不支持数据源。下一步准备支持mybatis



==================================== 遇到问题：

Spring MVC 转发不到受理类。
解决：原因是class路径不再webContent下导致。修改输出class路径为：SpringTest/WebContent/WEB-INF/classes 后解决

@RequestBody 注解报错 The annotation @RequestBody is disallowed for this location
解决：应该是@ResponseBody ，是@Response不是@request

==================================== 遇到问题】