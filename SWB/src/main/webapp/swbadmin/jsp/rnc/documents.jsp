<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="mx.gob.cultura.portal.response.Entry"%>
<%@page import="mx.gob.cultura.portal.response.Utils, mx.gob.cultura.portal.response.DigitalObject"%>
<%@page import="mx.gob.cultura.portal.response.Title,org.semanticwb.model.WebSite, org.semanticwb.portal.api.SWBParamRequest, org.semanticwb.portal.api.SWBResourceURL, java.util.List, java.util.ArrayList"%>
<script type="text/javascript" src="/swbadmin/js/dojo/dojo/dojo.js" djConfig="parseOnLoad: true, isDebug: false, locale: 'en'"></script>
<%
    SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    SWBResourceURL pageURL = paramRequest.getRenderUrl().setMode("PAGE");
    pageURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    SWBResourceURL pagesURL = paramRequest.getRenderUrl().setMode("PAGES");
    pagesURL.setCallMethod(SWBParamRequest.Call_DIRECT);
    WebSite site = paramRequest.getWebPage().getWebSite();
    Integer last = (Integer)session.getAttribute("LAST_RECORD");
    Integer first = (Integer)session.getAttribute("FIRST_RECORD");
    Integer total = (Integer)session.getAttribute("NUM_RECORDS_TOTAL");
    String word = (String)request.getAttribute("word");
    if (null != word) word = Utils.suprXSS(word);
    List references = (List)session.getAttribute("PAGE_LIST");
    if (null == references) references = new ArrayList();

    //= null != session.getAttribute("PAGE_LIST") ? (List<Entry>)session.getAttribute("PAGE_LIST") : new ArrayList<>();
%>
<script type="text/javascript">
    function setList() { doPage(1, 'l', 'relvdes'); }
    function setGrid() { doPage(1, 'g', 'relvdes'); }
    function doPage(p, m, f) {
        dojo.xhrPost({
            url: '<%=pageURL%>?p='+p+'&m='+m+'&sort='+f+'&word=<%=word%>',
            load: function(data) {
                dojo.byId('references').innerHTML=data;
				location.href = '#showPage';
            }
        });
    }
</script>
<div class="row resultadosbar">
    <div class="col-md-3"><a class=" oswL" href="javascript:history.go(-1)"><i aria-hidden="true" class="fa fa-long-arrow-left"></i> Regresar</a></div>
    <div class="col-md-9">
        <p class=" oswL">
            <% if (null != word) { %>
                Colección / Resultados de: <%=word%>
            <% }else { out.println("Debe proporcionar un criterio de búsqueda"); } %>
        </p>
    </div>
</div>
<div class="row offcanvascont">
    <div class="offcanvas rojo-bg">
	<span onclick="openNav()" id="offcanvasAbre">
            <em class="fa fa-sliders" aria-hidden="true"></em> Filtros <i class="ion-chevron-right " aria-hidden="true"></i>
        </span>
        <span onclick="closeNav()" id="offcanvasCierra">
            <em class="fa fa-sliders" aria-hidden="true"></em> Filtros <i class="ion-close " aria-hidden="true"></i>
        </span>
    </div>
    <jsp:include page="filters.jsp" flush="true"/>
    <div id="contenido">
	<a name="showPage"></a>
	<% if (!references.isEmpty()) {  %>
        <div id="references">
            <div class="ruta row">
		<div class="col-12 col-sm-8 col-md-8">
                    <p class="oswLc"><%=first%>-<%=last%> de <%=total%> resultados</p>
		</div>
		<div class="col-12 col-sm-4 col-md-4 ordenar">
                    <a href="#" onclick="setGrid();"><i class="fa fa-th select" aria-hidden="true"></i></a>
                    <a href="#" onclick="setList();"><i class="fa fa-th-list" aria-hidden="true"></i></a>
		</div>
            </div>
            <div id="resultados" class="card-columns">
                <%
                    int position = first;
                    for (Object o : references) {
                        Entry reference = (Entry)o;
                        Title title = new Title();
                        reference.setPosition(position);
                        DigitalObject digital = new DigitalObject();
                        List<String> creators = reference.getCreator();
			List<Title> titles = reference.getRecordtitle();
			List<String> resourcetype = reference.getResourcetype();
                        List<DigitalObject> digitalobject = reference.getDigitalobject();
                        if (!titles.isEmpty()) title = titles.get(0);
                        String creator = creators.size() > 0 ? creators.get(0) : "";
			String type = resourcetype.size() > 0 ? resourcetype.get(0) : "";
			if (!digitalobject.isEmpty()) digital = digitalobject.get(0);
                %>
                        <div class="pieza-res card">
                            <a href="/swb/<%=site.getId()%>/detalle?id=<%=reference.getId()%>">
                                <img src="<%=digital.getUrl()%>" />
                            </a>
                            <div>
                                <p class="oswB azul tit"><a href="#"><%=title.getValue()%></a></p>
                                <p class="azul autor"><a href="#"><%=creator%></a></p>
                                <p class="tipo"><%=type%></p>
                            </div>
                        </div>
                <%
                        position++;
                    }
                %>
            </div>
            <jsp:include page="pager.jsp" flush="true"/>
        </div>
	<%
            }else if (null != word) { out.println("No se encontraron resultados para la búsqueda " + word); }
        %>
	<!-- PIE -->
        <footer class="gris21-bg">
            <div class="container">  
		<div class="logo-cultura">
                    <img src="/work/models/repositorio/img/logo-cultura.png" class="img-responsive">
                </div>            
                <div class="row pie-sube">
                    <a href="#top">
                        <i class="ion-ios-arrow-thin-up" aria-hidden="true"></i>
                    </a>
                </div>
                <div class="row datos">
                    <!-- order-sm-2 order-2 -->
                    <div class="col-7 col-sm-6 col-md-4 col-lg-4 datos1">
                        <ul>
                            <li><a href="#">Quiénes somos y qué hacemos</a></li>
                            <li><a href="#">Nuestros proveedores de datos</a></li>
                            <li><a href="#">Cómo colaborar con nosotros</a></li>
                        </ul>
                    </div>
                    <div class="col-5 col-sm-6 col-md-4 col-lg-4 datos2">
                        <ul>
                            <li><a href="#">Declaración de derechos</a></li>
                            <li><a href="#">Documentación</a></li>
                            <li><a href="#">Red de Preservación</a></li>
                        </ul>
                    </div>
                    <hr class="d-md-none">
                    <div class="col-7 col-sm-6 col-md-4 col-lg-4 datos3">
                        <ul>
                            <li><a href="#">Contacto</a></li>
                            <li><a href="#">1234 5678 ext. 123 y 456</a></li>
                            <li><a href="#">email@cultura.gob.mx</a></li>
                        </ul>
                    </div>
                    <hr class="d-none d-sm-none d-md-block">
                    <div class="col-5 col-sm-6 col-md-12 col-lg-12 datos4">
                        <ul class="row">
                            <li class="col-md-4"><a href="#">Mapa de sitio</a></li>
                            <li class="col-md-4"><a href="#">Política de Privacidad</a></li>
                            <li class="col-md-4"><a href="#">Términos de uso</a></li>
                        </ul>
                    </div>
                </div>
            </div>
        </footer>
        <div class="container-fluid pie-derechos">
            <p>Secretaría de Cultura, 2017. Todos los derechos reservados.</p>
        </div>
    </div>
</div>