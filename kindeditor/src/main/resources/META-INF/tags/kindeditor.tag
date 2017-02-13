<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="head.tag"%>
<%@ attribute name="formName" type="java.lang.String" required="true" description="formName for kindeditor"%>
<%@ attribute name="content" type="java.lang.String" required="true" description="name for kindeditor content"%>
<script>
    Sniper.KindEditor.init('<c:out value="formName"/>','<c:out value="content"/>');
</script>