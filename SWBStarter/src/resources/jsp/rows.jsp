<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.util.List,mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Identifier, mx.gob.cultura.portal.response.DigitalObject, mx.gob.cultura.portal.response.Title"%>
<%
	List<Entry> references = (List<Entry>)session.getAttribute("PAGE_LIST");
%>
<% if (!references.isEmpty()) {  %>
    <div id="references">
        <div id="recientes" class="row">
    <%      for (Entry reference : references) {
                Identifier identifier = new Identifier();
                DigitalObject digital = new DigitalObject();
                List<Title> titles = reference.getTitle();
                List<DigitalObject> digitalobject = reference.getDigitalobject();
                List<Identifier> identifiers = reference.getIdentifier();
                if (!digitalobject.isEmpty()) digital = digitalobject.get(0);
                for (Identifier id : identifiers) {
                    if (id.isPreferred()) identifier = id;
                }
    %>	
                    <div class="pieza">
                        <div>
                            <a href="/swb/cultura/detalle?id=<%=identifier.getValue()%>">
                                <img src="<%=digital.getUrl()%>" />
                            </a>
                        </div>
                        <p class="oswB azul tit"><a href="#"><%=titles.get(0).getValue()%></a></p>
                        <p class="azul autor"><a href="#"><%=reference.getCreator().get(0)%></a></p>
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