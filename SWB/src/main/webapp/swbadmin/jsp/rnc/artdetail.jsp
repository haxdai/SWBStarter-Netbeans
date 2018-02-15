<%--
    Document   : artdetail.jsp
    Created on : 5/12/2017, 11:48:36 AM
    Author     : sergio.tellez
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="mx.gob.cultura.portal.response.DateDocument, mx.gob.cultura.portal.response.DigitalObject"%>
<%@ page import="mx.gob.cultura.portal.resources.ArtDetail, mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Title, org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, java.util.ArrayList, java.util.List"%>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<%
    int index = 0;
    String title = "";
    String period = "";
    String creator = "";
    List<Title> titles = new ArrayList<>();
    List<String> creators = new ArrayList<>();
    DateDocument datestart = new DateDocument();
    List<DigitalObject> digitalobjects = new ArrayList<>();
    Entry entry = (Entry)request.getAttribute("entry");
    if (null != entry && null != entry.getDigitalobject()) {
        creators = entry.getCreator();
        titles = entry.getRecordtitle();
        digitalobjects = entry.getDigitalobject();
        datestart = entry.getPeriodcreated().getDatestart();
        creator = creators.size() > 0 ? creators.get(0) : "";
        period = null != datestart ? datestart.getValue() : "";
        if (!titles.isEmpty()) title = titles.get(0).getValue();
    }
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    WebSite site = paramRequest.getWebPage().getWebSite();
%>
<script>
    function add(id) {
	dojo.xhrPost({
            url: '/swb/<%=site.getId()%>/favorito?id='+id,
            load: function(data) {
                dojo.byId('addCollection').innerHTML=data;
		$('#addCollection').modal('show');
            }
        });
    }
    function loadDoc(id) {
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
            if (this.readyState == 4 && this.status == 200) {
                jQuery("#addCollection-tree").html(this.responseText);
                $("#addCollection" ).dialog( "open" );
            }else if (this.readyState == 4 && this.status == 403) {
                jQuery("#addCollection-tree").text("Regístrate o inicia sesión para crear tus colecciones.");
                $("#addCollection" ).dialog( "open" );
            }
        };
	xhttp.open("GET", "/swb/<%=site.getId()%>/favorito?id="+id, true);
	xhttp.send();
    }
</script>
<div class="row row-offcanvas row-offcanvas-right" id="detallecont">
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
                    if (r.getMediatype().getMime().equals("image/jpeg")) {
                        if (i == 0) {
                            %>
                            <li data-target="#carouselExampleIndicators" data-slide-to="0" class="active"></li>
                            <%
                        } else {
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
                for (DigitalObject r : digitalobjects) {
                    if (r.getMediatype().getMime().equals("image/jpeg")) {
                        String css = index == 0 ? "carousel-item active" : "carousel-item";
                        %>
                        <div class="<%=css%>">
                            <img src="<%=r.getUrl()%>" alt="siguiente"></img>
                        </div>
                        <%
                        index++;
                    }
                }
                %>
            </div>
            <%
            if (index > 1) {
                %>
                <a class="carousel-control-prev" href="#carouselExampleIndicators" role="button" data-slide="prev">
                    <i class="fa fa-long-arrow-left" aria-hidden="true"></i>
                    <span class="sr-only">anterior</span>
                </a>
                <a class="carousel-control-next" href="#carouselExampleIndicators" role="button" data-slide="next">
                    <i class="fa fa-long-arrow-right" aria-hidden="true"></i>
                    <span class="sr-only">siguiente</span>
                </a>
                <%
            }
            %>
        </div>
    </div>
    <div class="col-6 col-md-3 sidebar-offcanvas" id="sidebar">
        <div>
            <p>Ficha técnica</p>
            <hr>
            <ul>
                <%
                if (null != entry) {
                    if (null != entry.getResourcetype() && !entry.getResourcetype().isEmpty()) {
                        %><li><strong>Tipo de objeto</strong> <%=entry.getResourcetype()%></li><%
                    }
                    if (null != creator && !creator.isEmpty()) {
                        %><li><strong>Autor</strong> <%=creator%></li><%
                    }
                    if (null != titles && !titles.isEmpty()) {
                        String t = titles.get(0).getValue();
                        %><li><strong>Título</strong> <%=t%></li><%
                    }
                    if (null != entry.getDatecreated() && null != entry.getDatecreated().getValue() && !entry.getDatecreated().getValue().isEmpty()) {
                        %><li><strong>Fecha de creación</strong> <%=entry.getDatecreated().getValue()%></li><%
                    }
                    if (null != entry.getIdentifiers() && ! entry.getIdentifiers().isEmpty()) {
                        %><li><strong>Identificador</strong> <%=entry.getIdentifiers()%></li><%
                    }
                } else {
                    out.println("<li><strong>No se encontró información del objeto</strong></li>");
                }
                %>
            </ul>
        </div>
        <div class="vermas">
            <a href="#">Ver más <span class="ion-android-add-circle"></span></a>
        </div>
    </div>
</div>
<div id="detallesube" class="row detalle-acciones ">
    <div class="col-xs-12 col-sm-7 col-md-6 col-lg-6 col-xl-6 offset-sm-0 offset-md-0 offset-lg-0 offset-xl-1">
        <a href="#detallesube" id="subir"><i class="fa fa-long-arrow-down rojo-bg" aria-hidden="true"></i></a>
        <p class="oswB"><%=title%></p>
        <p><%=period%></p>
    </div>
    <div class="col-xs-12 col-sm-2 col-md-3 col-lg-3 col-xl-2">
        <a href="#"><i class="fa fa-search-plus" aria-hidden="true"></i></a>
        <a href="#" onclick="loadDoc('<%=entry.getId()%>');"><i class="fa fa-heart" aria-hidden="true"></i></a>
        <a href="#"><i class="fa fa-share-alt" aria-hidden="true"></i></a>
        <a href="#"><i class="fa fa-download" aria-hidden="true"></i></a>
    </div>
    <div class="col-xs-12 col-sm-2 col-md-3 col-lg-3 col-xl-3 rojo">
        <p>¿Puedo usarlo?</p>
    </div>
</div>
<% if (null != entry && null != entry.getDescription() && !entry.getDescription().isEmpty()) { %>
<section id="detalleinfo">
    <a></a>
    <div class="cointainer-fluid">
        <div class="row detalleinfo azul-bg">
            <p class="col-md-10 offset-md-1">
                <%=entry.getDescription()%>
            </p>
        </div>
    </div>
</section>
<% } %>

<div id="dialog-message-tree" title="error">
    <p>
	<span class="ui-icon ui-icon-circle-check" style="float:left; margin:0 7px 50px 0;"></span>
	<div id="dialog-text-tree"></div>
    </p>
</div>
<div id="dialog-success-tree" title="éxito">
    <p>
	<span class="ui-icon ui-icon-circle-check" style="float:left; margin:0 7px 50px 0;"></span>
	<div id="dialog-msg-tree"></div>
    </p>
</div>

<div id="addCollection" title="Agregar a colección">
    <p>
	<span class="ui-icon ui-icon-circle-check" style="float:left; margin:0 7px 50px 0;"></span>
	<div id="addCollection-tree"></div>
    </p>
</div>