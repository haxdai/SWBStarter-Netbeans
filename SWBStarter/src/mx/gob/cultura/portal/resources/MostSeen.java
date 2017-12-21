package mx.gob.cultura.portal.resources;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import mx.gob.cultura.portal.response.Document;
import org.semanticwb.portal.api.GenericResource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.gob.cultura.portal.response.Entry;
import org.semanticwb.SWBUtils;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;


/**
 * Muestra los n elementos con mayor numero de consultas
 * @author jose.jimenez
 */
public class MostSeen extends GenericResource {
    
    
    private static final Logger LOG = Logger.getLogger(MostSeen.class.getName());

    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) throws SWBResourceException, java.io.IOException {
        
        List<Entry> references;
        //List<Entry> publicationList = new ArrayList<>();
        String url = "/swbadmin/jsp/rnc/mostSeenCarousel.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(url);
        try {
            references = getReferences(request);
            request.setAttribute("mostSeenList", references);
            request.setAttribute("paramRequest", paramRequest);
            rd.include(request, response);
        } catch (ServletException se) {
            se.printStackTrace();
            LOG.info(se.getMessage());
        }
    }
    
    private List<Entry> getReferences(HttpServletRequest request) {
        String uri = getResourceBase().getAttribute("endpointURL", "https://search.innovatic.com.mx") + "/api/v1/search?sort=-resourcestats.views&size=10";
        List<Entry> publicationList = new ArrayList<>();
        try {
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            InputStream is = connection.getInputStream();
            String jsonText = SWBUtils.IO.readInputStream(is, "UTF-8");
            Gson gson = new Gson();
            Type entryListType = new TypeToken<ArrayList<Entry>>(){}.getType();
            Document resp = gson.fromJson(jsonText, Document.class);
            publicationList = resp.getRecords();
        } catch (Exception e) {
            e.printStackTrace();
            publicationList = new ArrayList<>();
            LOG.info(e.getMessage());
        }
        return publicationList;
    }
    
}
