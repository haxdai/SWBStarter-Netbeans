<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="org.semanticwb.portal.api.SWBResourceURL"%>
<%@page import="org.semanticwb.portal.api.SWBParamRequest"%>
<%@page import="java.util.List,mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Identifier, mx.gob.cultura.portal.response.DigitalObject, mx.gob.cultura.portal.response.Title"%>
<%
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    List<Entry> references = (List<Entry>)request.getAttribute("references");
    String word = (String)request.getAttribute("word");
    String pages = null != request.getAttribute("pages") ? (String)request.getAttribute("pages") : "";
%> 
<% if (!references.isEmpty()) {  %>
    <%  if (null == word) {  %>
            <div><h2>Resultados para la búsqueda <%=word%></h2></div>
	<%  }  %>
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
<%      } 
    }else if (null != word) { out.println("No se encontraron resultados para la búsqueda " + word); }
%>
<%=pages%>