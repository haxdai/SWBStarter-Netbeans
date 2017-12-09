<%-- 
    Document   : artdetail.jsp
    Created on : 5/12/2017, 11:48:36 AM
    Author     : sergio.tellez
--%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="org.semanticwb.portal.api.SWBResourceURL"%>
<%@page import="org.semanticwb.portal.api.SWBParamRequest"%>
<%@page import="java.util.List, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.DigitalObject, mx.gob.cultura.portal.response.Rights"%>
<%
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    Entry entry = (Entry)request.getAttribute("entry");
	List<DigitalObject> digitalobjects = entry.getDigitalobject();
%>
<main role="main" class="container-fluid detalle">
    <div class="row row-offcanvas row-offcanvas-right">
        <div class="col-12 col-md-9" id="contenido">
            <p class="float-right d-md-none">
                <button type="button" class="btn btn-sm" data-toggle="offcanvas">
                    <span class="ion-chevron-left"> Mostrar ficha </span>
                    <span class="ion-chevron-right"> Ocultar ficha </span>
                </button>
            </p>
            <div id="carouselExampleIndicators" class="carousel slide" data-ride="carousel">
                <ol class="carousel-indicators">
                    <% 
                                int i = 0;
                                for (DigitalObject r : digitalobjects) {
                                                if (r.getFormat().equals("image/jpeg")) {
                                    if (i == 0) {
                                        %>
                                        <li data-target="#carouselExampleIndicators" data-slide-to="0" class="active"></li>
                            <%		
                                    }else {
                            %>
                                        <li data-target="#carouselExampleIndicators" data-slide-to="<% out.println(""+i); %>"></li>
                            <% 
                                    }
                                    i++;
                             }
                                }
                    %>
                </ol>
                <div class="carousel-inner">
                    <% 
                                int index = 0;
                                for (DigitalObject r : digitalobjects) {
                                        if (r.getFormat().equals("image/jpeg")) {
                                    if (index == 0) {
                                        %>
                                        <div class="carousel-item active">
                            <%		
                                    }else {
                            %>	
                                        <div class="carousel-item">
                            <%	
                                    } 
                            %>
                                            <img src="<%=r.getUrl()%>" alt="siguiente">
                                        </div>
                            <% 
                                    index++;
                                        }
                                } 
                    %>    
                </div>
                <a class="carousel-control-prev" href="#carouselExampleIndicators" role="button" data-slide="prev">
                    <i class="fa fa-long-arrow-left" aria-hidden="true"></i>
                    <span class="sr-only">anterior</span>
                </a>
                <a class="carousel-control-next" href="#carouselExampleIndicators" role="button" data-slide="next">
                    <i class="fa fa-long-arrow-right" aria-hidden="true"></i>
                    <span class="sr-only">siguiente</span>
                </a>
            </div>
        </div>
        <div class="col-6 col-md-3 sidebar-offcanvas" id="sidebar">
            <div>
                <p>Ficha técnica</p>
                <hr>
                <ul>
                    <li><strong>Tipo de objeto</strong> <%=entry.getIdentifier().get(0).getType()%></li>
                    <li><strong>Autor</strong> <%=entry.getCreator().get(0)%></li>
                    <li><strong>Título</strong> <%=entry.getTitle().get(0).getValue()%></li>
                    <li><strong>Fecha de creación</strong> <%=entry.getDatecreated().getValue()%></li>
                    <li><strong>Técnica</strong> <%=entry.getTechnique()%></li>
                    <li><strong>Institución</strong> <%=entry.getInstitution()%></li>
                    <li><strong>Fondo o colección</strong> <%=entry.getCollection()%></li>
                    <li><strong>Identificador</strong> <%=entry.getIdentifier().get(0).getValue()%></li>
                </ul>
            </div>
            <div class="vermas">
                <a href="#">Ver más <span class="ion-android-add-circle"></span></a>
            </div>
        </div>
    </div>
    <div class="row detalle-acciones ">
        <div class="col-xs-12 col-sm-7 col-md-6 col-lg-6 col-xl-6 offset-sm-0 offset-md-0 offset-lg-0 offset-xl-1">
            <i class="fa fa-long-arrow-down rojo-bg" aria-hidden="true"></i>
            <p class="oswB"><%=entry.getTitle().get(0).getValue()%></p>
            <p><%=entry.getPeriodcreated().getDatestart().getValue()%></p>
        </div>
        <div class="col-xs-12 col-sm-2 col-md-3 col-lg-3 col-xl-2">
            <a href="#"><i class="fa fa-search-plus" aria-hidden="true"></i></a>
            <a href="#"><i class="fa fa-heart" aria-hidden="true"></i></a>
            <a href="#"><i class="fa fa-share-alt" aria-hidden="true"></i></a>
            <a href="#"><i class="fa fa-download" aria-hidden="true"></i></a>
        </div>
        <div class="col-xs-12 col-sm-2 col-md-3 col-lg-3 col-xl-3 rojo">
            <p>¿Puedo usarlo?</p>
        </div>
    </div>
    <div class="row detalleinfo azul-bg">
        <p class="col-md-11 offset-md-1">
            <%=entry.getDescription()%>
        </p>
    </div>
</main>