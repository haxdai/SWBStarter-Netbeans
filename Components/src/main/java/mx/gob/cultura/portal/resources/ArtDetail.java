/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.resources;

import java.io.IOException;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.GenericAdmResource;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URL;
import java.net.HttpURLConnection;

import java.util.logging.Logger;
import mx.gob.cultura.portal.response.Entry;
import mx.gob.cultura.portal.request.GetBICRequest;

/**
 *
 * @author sergio.tellez
 */
public class ArtDetail extends GenericAdmResource {
    
    private static final String IDENTIFIER = "id";
    private static final Logger LOG = Logger.getLogger(ArtDetail.class.getName());
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response, SWBParamRequest paramRequest) throws IOException {
        String uri = getResourceBase().getAttribute("url","http://localhost:8080") + "/api/v1/search?identifier=";
        String path = "/swbadmin/jsp/rnc/artdetail.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(path);
        try {
            if (null != request.getParameter(IDENTIFIER)) {
                uri += request.getParameter(IDENTIFIER);
                GetBICRequest req = new GetBICRequest(uri);
                Entry entry = req.makeRequest();
                if (null != entry) {
                    uri = getResourceBase().getAttribute("url","http://localhost:8080")
                            + "/api/v1/search/hits/"
                            + entry.getId();
                    URL url = new URL(uri);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.getOutputStream().close();
                    connection.getResponseCode();
                }
                request.setAttribute("entry", entry);
            }
            request.setAttribute("paramRequest", paramRequest);
            rd.include(request, response);
        } catch (ServletException se) {
            LOG.info(se.getMessage());
        }
    }
}
