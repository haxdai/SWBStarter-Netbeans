<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.response.DigitalObject,mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Identifier, mx.gob.cultura.portal.response.Title, java.util.List"%>
<%
    String mode = "row";
    List<Entry> references = (List<Entry>)session.getAttribute("PAGE_LIST");
    if (null != request.getAttribute("mode"))
        mode = (String)request.getAttribute("mode");
%>
<% if (!references.isEmpty()) {  %>
    <div id="references">
        <div id="recientes" class="<%=mode%>">
    <%      for (Entry reference : references) {
                Identifier identifier = new Identifier();
                DigitalObject digital = new DigitalObject();
                List<Title> titles = reference.getRecordtitle();
				List<String> creators = reference.getCreator();
                List<DigitalObject> digitalobject = reference.getDigitalobject();
                if (!digitalobject.isEmpty()) digital = digitalobject.get(0);
				String creator = creators.size() > 0 ? creators.get(0) : "";
    %>	
                <div class="pieza">
                    <div>
                        <a href="/swb/cultura/detalle?id=<%=reference.getId()%>">
                            <img src="<%=digital.getUrl()%>" />
                        </a>
                    </div>
                    <p class="oswB azul tit"><a href="#"><%=titles.get(0).getValue()%></a></p>
                    <p class="azul autor"><a href="#"><%=creator%></a></p>
                </div>
    <%       
            }
    %>
        </div>
        <jsp:include page="pager.jsp" flush="true"/>
    </div>
<%
    }
%>