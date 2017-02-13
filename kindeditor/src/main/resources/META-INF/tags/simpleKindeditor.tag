<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ include file="head.tag"%>
<%@ attribute name="content" type="java.lang.String" required="true" description="name for kindeditor content"%>
<script>
    Sniper.KindEditor.simple('<c:out value="content"/>');
</script>