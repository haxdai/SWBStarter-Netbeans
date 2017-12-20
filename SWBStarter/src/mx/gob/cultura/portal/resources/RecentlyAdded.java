package mx.gob.cultura.portal.resources;

import com.google.gson.Gson;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.gob.cultura.portal.response.Entry;
import mx.gob.cultura.portal.response.SearchResponse;
import org.semanticwb.SWBUtils;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;

/**
 * Muestra los n elementos recien agregados al repositorio
 * @author jose.jimenez
 */
public class RecentlyAdded extends GenericResource {
    
    
    private static final Logger LOG = Logger.getLogger(MostSeen.class.getName());
    
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) throws SWBResourceException, java.io.IOException {
        
        List<Entry> references;
        //List<Entry> publicationList = new ArrayList<>();
        String url = "/swbadmin/jsp/rnc/recentlyAddedCarousel.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(url);
        try {
            references = getReferences(request);
            request.setAttribute("recentlyAddedList", references);
            request.setAttribute("paramRequest", paramRequest);
            rd.include(request, response);
        } catch (ServletException se) {
            se.printStackTrace();
            LOG.info(se.getMessage());
        }
    }
    
    private List<Entry> getReferences(HttpServletRequest request) {
        String uri = getResourceBase().getAttribute("endpointURL", "https://search.innovatic.com.mx") + "/api/v1/search?sort=-indexcreated&size=10";
        List<Entry> publicationList = new ArrayList<>();
        try {
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            InputStream is = connection.getInputStream();
            String jsonText = SWBUtils.IO.readInputStream(is, "UTF-8");
            Gson gson = new Gson();
            SearchResponse resp = gson.fromJson(jsonText, SearchResponse.class);
            publicationList = resp.getRecords();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            publicationList = new ArrayList<>();
            LOG.info(e.getMessage());
        }
        return publicationList;
    }
}
