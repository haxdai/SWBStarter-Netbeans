<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="org.semanticwb.portal.api.SWBResourceURL"%>
<%@page import="org.semanticwb.portal.api.SWBParamRequest"%>
<%@page import="java.util.List,java.util.ArrayList,mx.gob.cultura.portal.response.Aggregation, mx.gob.cultura.portal.response.CountName"%>
<%
	boolean showFilters = false;
	List<CountName> dates = new ArrayList<>();
	List<CountName> holders = new ArrayList<>();
	List<CountName> resourcetypes = new ArrayList<>();
	String word = (String)request.getAttribute("word");
	List<Aggregation> aggs = (List<Aggregation>)request.getAttribute("aggs");
	SWBParamRequest paramRequest = (SWBParamRequest)request.getAttribute("paramRequest");
    SWBResourceURL pageURL = paramRequest.getRenderUrl().setMode("SORT");
    pageURL.setCallMethod(SWBParamRequest.Call_DIRECT);
	if (null != aggs && null != word) {
		showFilters = true;
		for (Aggregation a : aggs) {
			if (null !=  a.getHolders()) holders.addAll(a.getHolders());
			if (null !=  a.getDates()) dates.addAll(a.getDates());
			if (null !=  a.getResourcetypes()) resourcetypes.addAll(a.getResourcetypes());
		}
	}
%>
<script type="text/javascript">
    function doSort(w, f) {
        dojo.xhrPost({
            url: '<%=pageURL%>?word='+w+'&sort='+f,
            load: function(data) {
				dojo.byId('references').innerHTML=data;
            }
        });
    }
</script>
	<div class="" id="sidebar">
        <div id="accordion" role="tablist">
			<% if (!holders.isEmpty()) { %>
				<div class="card">
					<div class="" role="tab" id="headingOne">
						<a data-toggle="collapse" href="#collapse1" aria-expanded="true" aria-controls="collapseOne">Tipos de objeto <span>+</span></a>
					</div>
					<div id="collapse1" class="collapse show" role="tabpanel" aria-labelledby="headingOne" data-parent="#accordion">
						<ul>
							<li>
								<label class="form-check-label"><input class="form-check-input" type="checkbox" value="">Todos</label>
								<ul>
							<%
								for (CountName r : resourcetypes) {
							%>
										<li><label class="form-check-label"><input class="form-check-input" type="checkbox" value=""><%=r.getName()%></label></li>
							<%
								}
							%>
								</ul>
							</li>
						</ul>
					</div>
				</div>
			<% } %>
			<% if (!holders.isEmpty()) { %>
				<div class="card">
					<div class="" role="tab" id="headingOne">
						<a data-toggle="collapse" href="#collapse3" aria-expanded="true" aria-controls="collapseOne">Institución <span>+</span></a>
					</div>
					<div id="collapse3" class="collapse show" role="tabpanel" aria-labelledby="headingOne" data-parent="#accordion">
						<ul>
							<li>
								<label class="form-check-label"><input class="form-check-input" type="checkbox" value="">Todas</label>
								<ul>
								<%
									for (CountName h : holders) {
								%>
											<li><label class="form-check-label"><input class="form-check-input" type="checkbox" value=""><%=h.getName()%></label></li>
								<%
									}
								%>
								</ul>
							</li>
						</ul>
					</div>
				</div>
			<% } %>
			<% if (!dates.isEmpty()) { %>
				<div class="card">
					<div class="" role="tab" id="headingOne">
						<a data-toggle="collapse" href="#collapse4" aria-expanded="true" aria-controls="collapseOne">Fecha <span>+</span></a>
					</div>
					<div id="collapse4" class="collapse show" role="tabpanel" aria-labelledby="headingOne" data-parent="#accordion">
						<ul>
							<li>
								<% if (dates.size() > 1) { %>
									<label class="form-check-label"><input class="form-check-input" type="checkbox" value="">Todas</label>
								<% } %>
								<ul>
								<%
									for (CountName d : dates) {
								%>
											<li><label class="form-check-label"><input class="form-check-input" type="checkbox" value=""><%=d.getName().substring(0,4)%></label></li>
								<%
									}
								%>
								</ul>
							</li>
						</ul>
					</div>
				</div>
			<% 
				}
			    if (showFilters) {
			%>
					<button type="button" class="">Borrar filtros</button>
			<% } %>
		</div>
	</div>
	<div class="" id="contenido">
        <div class="ruta oswL">
			<a href="javascript:history.go(-1)" class="rojo"><i class="fa fa-long-arrow-left" aria-hidden="true"></i> Regresar</a> | 
             | Inicio / Resultados de la búsqueda
        </div>
        <div class="ordenar">
			<div class="row">
				<div class="col-lg-6">
                </div>
                <div class="col-lg-3 oswL dropdown show">
					<a class="dropdown-toggle" href="#" role="button" id="dropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
						Ordenar por:
                    </a>
                    <div class="dropdown-menu" aria-labelledby="dropdownMenuLink">
						<a class="dropdown-item" href="#" onclick="javascript:doSort(<% out.print("'"+word+"','datedes'"); %>)">Fecha</a>
						<a class="dropdown-item" href="#" onclick="javascript:doSort(<% out.print("'"+word+"','relvdes'"); %>)">Relevancia</a>
						<a class="dropdown-item" href="#" onclick="javascript:doSort(<% out.print("'"+word+"','statdes'"); %>)">Popularidad</a>
                    </div>
                </div>
                <div class="col-lg-3">
					<a href="#" onclick="setGrid();"><i class="fa fa-th select" aria-hidden="true"></i></a>
					<a href="#" onclick="setList();"><i class="fa fa-th-list" aria-hidden="true"></i></a>
                </div>
            </div>
        </div>
        <p class="float-left d-md-none">
			<button type="button" class="btn btn-sm rojo-bg" data-toggle="offcanvas">
				<span class="ion-chevron-left"> Ocultar filtros</span> 
                <span class="ion-chevron-right"> Mostrar filtros</span> 
		    </button>
        </p>