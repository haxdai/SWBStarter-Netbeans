package mx.gob.cultura.portal.resources;

import mx.gob.cultura.portal.request.ListBICRequest;
import mx.gob.cultura.portal.response.Document;
import mx.gob.cultura.portal.response.Entry;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.semanticwb.model.WebSite;

/**
 * Muestra los n elementos recien agregados al repositorio
 * @author jose.jimenez
 */
public class RecentlyAdded extends GenericResource {
    
    
    private static final Logger LOG = Logger.getLogger(RecentlyAdded.class.getName());

    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response,
                       SWBParamRequest paramRequest) throws java.io.IOException {

        List<Entry> references;
        String url = "/swbadmin/jsp/rnc/recentlyAddedCarousel.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(url);
        try {
            references = getReferences(paramRequest.getWebPage().getWebSite());
            request.setAttribute("recentlyAddedList", references);
            request.setAttribute("paramRequest", paramRequest);
            rd.include(request, response);
        } catch (ServletException se) {
            se.printStackTrace(System.err);
            LOG.info(se.getMessage());
        }
    }

    /**
     * Obtiene los n elementos agregados mas recientemente a la base de datos
     * @param webSite el sitio del cual se obtiene la ruta del endPoint a consultar
     * @return la lista de elementos agregados mas recientemente al sitio indicado. Si el sitio no
     *         contiene una propiedad con el nombre {@literal search_endPoint}, la lista estara vacia.
     */
    private List<Entry> getReferences(WebSite webSite) {
        String baseUri = webSite.getModelProperty("search_endPoint");
//        String uri = getResourceBase().getAttribute("endpointURL", "https://search.innovatic.com.mx") + "/api/v1/search?sort=-indexcreated&size=10";
        List<Entry> publicationList = new ArrayList<>();
        
        if (null != baseUri) {
            ListBICRequest req = new ListBICRequest(baseUri +
                    "/api/v1/search?sort=-indexcreated&size=10");

            Document resp = req.makeRequest();
            if (null != resp) {
                publicationList = resp.getRecords();
            }
        }
        return publicationList;
    }
}
