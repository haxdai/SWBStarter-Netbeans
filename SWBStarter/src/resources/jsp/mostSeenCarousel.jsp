<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>    
<%@page import="mx.gob.cultura.portal.response.Entry, mx.gob.cultura.portal.response.Identifier, mx.gob.cultura.portal.response.DigitalObject, java.util.List"%>
<%
    List<Entry> itemsList = (List<Entry>) request.getAttribute("mostSeenList");
    if (itemsList != null && !itemsList.isEmpty()) {
        int total = itemsList.size();
%>
<section id="buscado" class="azul-bg">
    <div class="container">
        <div class="row">
            <div class="col-xs-12 col-sm-12 col-md-2 col-lg-2 car-nav2">
                <div class="owl-nav col">
                    <h3 class="oswB blanco">Lo más consultado</h3>
                    <div class="bus customPrevBtn">
                        <i class="fa fa-long-arrow-left" aria-hidden="true"></i>
                    </div>
                    <div class="bus customNextBtn">
                        <i class="fa fa-long-arrow-right" aria-hidden="true"></i>
                    </div>
                </div>
            </div>
            <div class="col-xs-12 col-sm-12 col-md-10 col-lg-10 car-img2">
                <div id="owl-2" class="card-deck owl-carousel owl-theme col bus">
<%
        for (Entry item : itemsList) {
%>
                    <div class="item">
                        <div class="card">
                            <div class="card-header"><%=!item.getResourcetype().isEmpty() ? item.getResourcetype().get(0) : "&nbsp;" %><span class="ion-android-more-vertical"/>
                            </div>
<%
            for (DigitalObject digObj : item.getDigitalobject()) {
                if (digObj.getMediatype().getMime().startsWith("image") && !digObj.getUrl().isEmpty()) {
%>
                            <div class="card-img-cont">
                                <img class="card-img-top" src="<%=digObj.getUrl()%>"/>
                            </div>
<%
                    break;
                }
            }
            Identifier preferredId = new Identifier();
            for (Identifier id : item.getIdentifier()) {
                if (id.isPreferred()) {
                    preferredId = id;
                }
            }
            int views = 0; //item.getResourcestats().getViews();
%>
                            <div class="card-body">
                                <h4 class="card-title"><%=item.getRecordtitle().get(0).getValue()%></h4>
                                <p class="card-text">&nbsp;<!--no hay descripcion--></p>
                            </div>
                            <div class="card-footer">
                                <small class="text-muted">
                                    <div>
                                        <span class="ion-eye"/> <%=views%>
                                    </div>
                                    <div><a class="rojo" href="/swb/cultura/detalle?id=<%=preferredId.getValue()%>">Ver más +</a></div>
                                </small>
                            </div>
                        </div>
                    </div>
<%
        }
%>                    
                </div>
            </div>
        </div>
    </div>
</section>
<%
}
%>