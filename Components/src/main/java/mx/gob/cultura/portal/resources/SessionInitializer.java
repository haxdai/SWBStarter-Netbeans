package mx.gob.cultura.portal.resources;

import com.hp.hpl.jena.ontology.OntModel;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Iterator;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.servlet.RequestDispatcher;
import org.semanticwb.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.semanticwb.SWBPlatform;
import org.semanticwb.SWBPortal;
import org.semanticwb.SWBUtils;
import org.semanticwb.model.User;
import org.semanticwb.model.UserRepository;
import org.semanticwb.model.WebPage;
import org.semanticwb.platform.SemanticObject;
import org.semanticwb.platform.SemanticProperty;
import org.semanticwb.portal.SWBSessionObject;
import org.semanticwb.portal.api.GenericResource;
import org.semanticwb.portal.api.SWBActionResponse;
import org.semanticwb.portal.api.SWBParamRequest;
import org.semanticwb.portal.api.SWBResourceException;

/**
 * Initializes a user's session on the website
 * @author jose.jimenez
 */
public class SessionInitializer extends GenericResource {

    
    private static final Logger LOG = SWBUtils.getLogger(SessionInitializer.class);
    
    public static final String FACEBOOK = "facebook";
    
    private static final String TWITTER = "twitter";
    
    private static final String FACEBOOKID_URI =
            "http://www.semanticwebbuilder.org/swb4/ontology#facebookId";
    
    private static final String REDIRECT_MODE = "redirect";
    
    
    @Override
    public void doView(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        
        if (paramRequest.getMode().equals(SessionInitializer.REDIRECT_MODE)) {
            doRedirect(request, response, paramRequest);
        } else {
            if (paramRequest.getCallMethod() == SWBParamRequest.Call_STRATEGY) {
                showStrategyView(request, response, paramRequest);
            } else if (paramRequest.getCallMethod() == SWBParamRequest.Call_CONTENT) {
                showContentView(request, response, paramRequest);
            } else if (paramRequest.getCallMethod() == SWBParamRequest.Call_DIRECT) {
                
            }
        }
    }
    
    /**
     * Genera la interface de usuario correspondiente a la vista de estrategia
     * @param request la peticion del cliente
     * @param response la respuesta al cliente
     * @param paramsRequest reune parametros de SWB utiles para la respuesta de la peticion
     * @throws SWBResourceException si se genera algun problema en la ejecucion 
     *         de las solicitudes a la API de SWB
     */
    private void showStrategyView(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramsRequest) throws SWBResourceException {
        
        String url = "/swbadmin/jsp/rnc/sessionInitializerMenu.jsp";
        RequestDispatcher rd = request.getRequestDispatcher(url);
/*
        StringBuilder text = new StringBuilder(256);
        String mainLabel;
        String faceAppId;
        String faceVersion;
        boolean isSocialNetUser = false;
        
        try {
            mainLabel = paramsRequest.getLocaleString("lbl_main");
        } catch (SWBResourceException swbe) {
            mainLabel = "Iniciar sesión";
        }
        faceAppId = paramsRequest.getWebPage().getWebSite().getModelProperty("facebook_appid");
        if (null == faceAppId || faceAppId.isEmpty()) {
            throw new SWBResourceException("There is no app id configured for Facebook");
        }
        try {
            faceVersion = paramsRequest.getLocaleString("vl_facebook_version");
        } catch (SWBResourceException swbe) {
            faceVersion = "v2.11";
        }
        
        if (paramsRequest.getUser().isSigned()) {
            if (null != request.getSession(false) &&
                    request.getSession(false).getAttribute("isSocialNetUser") != null) {
                isSocialNetUser = Boolean.parseBoolean(
                        (String) request.getSession(false)
                                .getAttribute("isSocialNetUser"));
            }
        }
        
        text.append("<script>\n");
        text.append("  (function(d, s, id){\n");
        text.append("     var js, fjs = d.getElementsByTagName(s)[0];\n");
        text.append("     if (d.getElementById(id)) {return;}\n");
        text.append("     js = d.createElement(s); js.id = id;\n");
        text.append("     js.src = \"https://connect.facebook.net/es_MX/sdk.js\";\n");
        text.append("     fjs.parentNode.insertBefore(js, fjs);\n");
        text.append("  }(document, 'script', 'facebook-jssdk'));\n");
        text.append("  window.fbAsyncInit = function() {\n");
        text.append("    FB.init({\n");
        text.append("      appId      : '");
        text.append(faceAppId);
        text.append("',\n");
        text.append("      cookie     : true,\n");
        text.append("      xfbml      : true,\n");
        text.append("      version    : '");
        text.append(faceVersion);
        text.append("'\n");
        text.append("    });\n");
        text.append("    FB.getLoginStatus(function(response) {mystatusChangeCallback(response);});\n");
        text.append("  };\n");
        text.append("  function mystatusChangeCallback(response) {\n");
        
        //si no hay sesion con SWB:
        if (!paramsRequest.getUser().isSigned()) {
            
            //si hay sesion con FB, terminarla, no iniciarla automagicamente
            text.append("    console.log('Revisando status:.... ' + JSON.stringify(response));\n");
            text.append("    if (response.status && response.status === 'connected') {\n");
            text.append("      FB.logout();\n");
            text.append("    }\n");
            text.append("  }\n");
            
            String sessionUrl = paramsRequest.getActionUrl().setAction("openSession")
                    .setCallMethod(SWBParamRequest.Call_DIRECT).toString();
            text.append("  function openSWBSession() {\n");
            text.append("    FB.api('/me', function(response) {\n");
            text.append("      var name = response.name ? response.name : '';\n");
            text.append("      var faceId = response.id ? response.id : '';\n");
            text.append("      var email = response.email ? response.email : '';\n");
            text.append("      var xhttp = new XMLHttpRequest();\n");
            text.append("      xhttp.onreadystatechange = function() {\n");
            text.append("        if (this.readyState == 4 && this.status == 200) {\n");
            text.append("          location.reload();\n");
            text.append("        }\n");
            text.append("      };\n");
            text.append("      xhttp.open(\"POST\", \"");
            text.append(sessionUrl);
            text.append("\", false);\n"); //false = sincrona
            text.append("      xhttp.setRequestHeader(\"Content-type\", \"application/x-www-form-urlencoded\");\n");
            text.append("      xhttp.send(\"id=\"+faceId+\"&email=\"+email+\"&name=\"+name+\"&source=");
            text.append(SessionInitializer.FACEBOOK);
            text.append("\");\n");
            text.append("    });\n");
            text.append("  }\n");
            
            text.append("  function faceLogin() {\n");
            text.append("    FB.login(function(response) {\n");
            text.append("      console.log(JSON.stringify(response));\n");
            text.append("      if (response.authResponse) {\n");
            text.append("        openSWBSession();\n");
            text.append("      }\n");
            text.append("    });\n");
            text.append("  }\n");
            text.append("    \n");
            text.append("</script>\n");
            text.append("<div class=\"sesion dropdown show\">\n");
            text.append("  <a class=\"\" href=\"#\" role=\"button\" id=\"dropdownMenuLink\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\" >");
            text.append("    <span class=\"ion-person\"></span> <i>");
            text.append(mainLabel);
            text.append("</i><span class=\"ion-arrow-down-b\"></span></a>\n");
            
            String loginUrl = new StringBuilder().append(SWBPlatform.getContextPath())
                    .append("/login/")
                    .append(paramsRequest.getWebPage().getWebSiteId())
                    .append("/")
                    .append(paramsRequest.getWebPage().getId()).toString();
            text.append("  <div class=\"sesiondisplay dropdown-menu\" aria-labelledby=\"dropdownMenuLink\">\n");
            text.append("    <p>");
            try {
                text.append(paramsRequest.getLocaleString("lbl_dialogTitle"));
            } catch (SWBResourceException swbe) {
                text.append("Si tienes una cuenta, inicia ahora");
            }
            text.append("</p>");
            text.append("    <fieldset>\n");
            text.append("      <form action=\"");
            text.append(loginUrl);
            text.append("\" method=\"post\">\n");
            text.append("        <div class=\"form-group\">\n");
            text.append("          <label for=\"wb_username\">");
            try {
                text.append(paramsRequest.getLocaleString("lbl_userField"));
            } catch (SWBResourceException swbe) {
                text.append("Usuario:");
            }
            text.append("</label>\n");
            text.append("          <input type=\"text\" id=\"wb_username\" class=\"form-control\" name=\"wb_username\" aria-describedby=\"emailHelp\" placeholder=\"");
            try {
                text.append(paramsRequest.getLocaleString("lbl_userplaceHldr"));
            } catch (SWBResourceException swbe) {
                text.append("nombre de usuario");
            }
            text.append("\"/>\n");
            text.append("        </div>\n");
            text.append("        <div class=\"form-group\">\n");
            text.append("          <label for=\"wb_password\">");
            try {
                text.append(paramsRequest.getLocaleString("lbl_pswdField"));
            } catch (SWBResourceException swbe) {
                text.append("Contraseña:");
            }
            text.append("</label>\n");
            text.append("          <input type=\"password\" id=\"wb_password\" name=\"wb_password\" class=\"form-control\" placeholder=\"");
            try {
                text.append(paramsRequest.getLocaleString("lbl_pwdplaceHldr"));
            } catch (SWBResourceException swbe) {
                text.append("********");
            }
            text.append("\"/>\n");
            text.append("        </div>\n");
//            text.append("        <div class=\"form-check\">\n");
//            text.append("          <input type=\"checkbox\" class=\"form-check-input\" id=\"dropdownCheck\" >\n");
//            text.append("          <label class=\"form-check-label\" for=\"dropdownCheck\">Recuérdame</label>\n");
//            text.append("        </div>\n");
            text.append("        <button type=\"submit\" class=\"btn btn-negro\">");
            try {
                text.append(paramsRequest.getLocaleString("lbl_submitBtn"));
            } catch (SWBResourceException swbe) {
                text.append("Iniciar sesión");
            }
            text.append("</button>\n");
//            text.append("        <p><a href=\"#\">Olvidé mi usuario o contraseña</a></p>\n");
            text.append("        <hr>\n");
            text.append("        <p>");
            try {
                text.append(paramsRequest.getLocaleString("lbl_socialLogin"));
            } catch (SWBResourceException swbe) {
                text.append("Inicia con: ");
            }
//            text.append("          <div class=\"fb-login-button\" data-max-rows=\"1\"" +
//                    " data-size=\"small\" data-button-type=\"login_with\" " +
//                    "data-show-faces=\"false\" data-auto-logout-link=\"false\" " +
//                    "data-scope=\"public_profile,email\" " +
//                    "data-use-continue-as=\"false\" onlogin=\"openSWBSession();\"></div>\n");
            text.append("          <a href=\"#\" onclick=\"javascript:faceLogin();\"><img src=\"/work/models/");
            text.append(paramsRequest.getWebPage().getWebSiteId());
            text.append("/img/icono-fb.png\" ></a>\n");
            text.append("        </p>\n");
//            text.append("        <div class=\"nocuenta\">\n");
//            text.append("          <p>¿No tienes una cuenta?</p>\n");
//            text.append("          <p><a href=\"#\" class=\"btn\" >Crea aquí tu cuenta</a></p>\n");
//            text.append("        </div>\n");
            text.append("      </form>\n");
            text.append("    </fieldset>\n");
            text.append("  </div>\n");
            text.append("</div>\n");
            text.append("    \n");
        } else {  //si si existe el usuario en sesion de SWB
            
            if (isSocialNetUser) {
                //revisar que la sesion de la red social este activa tambien
                //esto es parte de la funcion mystatusChangeCallback
                text.append("    console.log('response: ' + JSON.stringify(response));");
                text.append("    if (response.status && response.status !== 'connected') {\n");
                text.append("      //reenviar a seccion con id de usuario de facebook y crear sesion con SWB\n");
                text.append("      alert('");
                try {
                    text.append(paramsRequest.getLocaleString("msg_sessionAlert"));
                } catch (SWBResourceException swbe) {
                    text.append("La sesión de Facebook ha terminado. Favor de iniciar sesión de nuevo.");
                }
                text.append("');\n");
                text.append("      closeSWBSession();\n");
                text.append("    }\n");
                text.append("  }\n");

                String url = paramsRequest.getActionUrl().setAction("closeSession")
                        .setCallMethod(SWBParamRequest.Call_DIRECT).toString();
                text.append("  function closeSWBSession() {\n");
                text.append("    FB.logout(function(response) {\n");
                text.append("      var xhttp = new XMLHttpRequest();\n");
                text.append("      xhttp.onreadystatechange = function() {\n");
                text.append("        if (this.readyState == 4 && this.status == 200) {\n");
                text.append("          location.reload();\n");
                text.append("        }\n");
                text.append("      };\n");
                text.append("      xhttp.open(\"GET\", \"");
                text.append(url);
                text.append("\", false);\n"); //false = sincrona
                text.append("      xhttp.send();\n");
                text.append("    });\n");
            }
            //dar la opcion a terminar la sesion
            try {
                mainLabel = paramsRequest.getLocaleString("lbl_out");
            } catch (SWBResourceException swbe) {
                mainLabel = "Terminar sesión";
            }
            text.append("  }\n");
            text.append("</script>\n");
            text.append("<div class=\"sesion\">\n");
            if (isSocialNetUser) {
                text.append("  <a class=\"\" href=\"#\" role=\"button\" onclick=\"");
                text.append("closeSWBSession();");
                text.append("\">");
                text.append("<span class=\"ion-person\"></span><i> ");
                text.append(mainLabel);
                text.append("</i></a>\n");
            } else {
                String logoutUrl = new StringBuilder()
                        .append(SWBPlatform.getContextPath())
                        .append("/login/")
                        .append(paramsRequest.getWebPage().getWebSiteId())
                        .append("/")
                        .append(paramsRequest.getWebPage().getId()).toString();
                text.append("  <a class=\"\" role=\"button\" href=\"");
                text.append(logoutUrl);
                text.append("?wb_logout=true");
                text.append("\">");
                text.append("<span class=\"ion-person\"></span><i> ");
                text.append(mainLabel);
                text.append("</i></a>\n");
            }
            text.append("</div>/n");
            text.append("");
        }
        */
        try {
//            PrintWriter out = response.getWriter();
//            out.println(text.toString());
            request.setAttribute("paramRequest", paramsRequest);
            rd.include(request, response);
        } catch (Exception se) {
            se.printStackTrace(System.err);
        }
    }
    
    private void showContentView(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) {
        
    }
    
    //Atiende la accion de una peticion para registrar al usuario como firmado en la aplicacion,
    //o para desasociarlo de la sesion activa y darla por terminada
    @Override
    public void processAction(HttpServletRequest request, SWBActionResponse response)
            throws SWBResourceException, IOException {
        
        String action = response.getAction();
        System.out.println("Ejecucion en processAction!!!");
        
        if (action.equals("openSession")) {
            createSignedSession(request, response);
        } else if (action.equals("closeSession")) {
            terminateSession(request, response);
        }
        
        response.setMode(SessionInitializer.REDIRECT_MODE);
    }
    
    /**
     * Agrega al usuario propiamente a la sesion creada para SWB
     * @param request peticion del cliente con los datos del usuario
     * @param response respuesta de SWB para la accion solicitada en la peticion
     * @throws SWBResourceException en caso de que el usuario especificado en la peticion
     * ya este registrado en una sesion
     */
    private void createSignedSession(HttpServletRequest request, SWBActionResponse response)
            throws SWBResourceException {
        
        UserRepository userRepo = response.getWebPage().getWebSite().getUserRepository();
        User user = null;
        boolean isSocialNetUser = false;
        
        //solo crear usuarios si usan una red social
        if (request.getParameter("source") != null && !request.getParameter("source").isEmpty()) {
            System.out.println("Va a entrar a crear usuario...\n");
            user = getSWBUser(request, response, userRepo);
            isSocialNetUser = true;
            request.getSession(true).setAttribute("isSocialNetUser", "true");
        }
        if (SWBPlatform.getSecValues().isMultiple()) {
            String login = !isSocialNetUser ? request.getParameter("wb_username")
                            : request.getParameter("email");
            Iterator<SWBSessionObject> llist = SWBPortal.getUserMgr().listSessionObjects();
            while (llist.hasNext()) {
                SWBSessionObject so = llist.next();
                Iterator<Principal> lpri = so.getSubjectByUserRep(userRepo.getId())
                         .getPrincipals().iterator();
                if (lpri.hasNext() && login.equalsIgnoreCase(((User)lpri.next()).getLogin())) {
                    throw new SWBResourceException("User already logged in");
                }
            }
        }
        if (null != user) {
            //se agrega el usuario a la sesion
            User oldUser = response.getUser();
            String id = request.getParameter("id");
            try {
                System.out.println("Ejecuta verificacion de credencial con: " + id);
                user.checkCredential(id.toCharArray());
                System.out.println("Usuario firmado: " + user.isSigned() +
                        "\ncontraseña: " + user.getPassword() +
                        "cadena: " + SWBUtils.CryptoWrapper.comparablePassword(id, "SHA-512"));
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
            
            
            Subject subject = SWBPortal.getUserMgr().getSubject(request,
                                response.getWebPage().getWebSiteId());
            subject.getPrincipals().clear();
            subject.getPrincipals().add(user);
            oldUser = user;
            if (null == user.getLanguage()) {
                user.setLanguage("es");   //forzar lenguaje si no se dio de alta.
            }
        }
    }

    /**
     * Termina la sesion del usuario, eliminando la asociación de este con el sitio en uso
     * @param request peticion del cliente
     * @param response respuesta de SWB para la accion solicitada en la peticion
     */
    private void terminateSession(HttpServletRequest request, SWBActionResponse response) {
        
        WebPage webpage = response.getWebPage();
        UserRepository ur = webpage.getWebSite().getUserRepository();
        String context = ur.getLoginContext();
        Subject subject = SWBPortal.getUserMgr().getSubject(request, webpage.getWebSiteId());
        User user = null;
        Iterator it = subject.getPrincipals().iterator();
        
        if (it.hasNext()) {
            user = (User) it.next();
        }
        LoginContext lc;
        try {
            if (null != user) {
                //Invalidate user even in cluster
                user.checkCredential("{123}456".toCharArray());
            }
            lc = new LoginContext(context, subject);
            lc.logout();
            request.getSession(false).invalidate();
            String url = request.getParameter("wb_goto");
            if ((url == null)) {
                url = webpage.getUrl(response.getUser().getLanguage());
            }
            SessionInitializer.LOG.debug("LOGOUT (Path, uri, url): " +
                    SWBPlatform.getContextPath() + "   |   " +
                    request.getRequestURI() + "    |  " + url);
            return;
        } catch (Exception elo) {
            SessionInitializer.LOG.error("LoggingOut " + subject, elo);
        }
    }
    
    /**
     * Busca un usuario en el repositorio de usuarios correspondiente al sitio en uso,
     * con los datos contenidos en la peticion, si no lo encuentra, lo crea.
     * @param request peticion del usuario con datos para crear un registro de usuario
     * @param response respuesta al usuario
     * @param userRepo repositorio de usuarios al que debe pertenecer el usuario
     * @return una instancia {@code User} con los datos contenidos en la peticion
     */
    private User getSWBUser(HttpServletRequest request, SWBActionResponse response, UserRepository userRepo) {
        
        String source = request.getParameter("source");
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String login = !"".equals(email) ? email : source + "_" + id;
        
        System.out.println("source: " + source);
        System.out.println("id: " + id);
        System.out.println("name: " + name);
        System.out.println("email: " + email);
        
        OntModel ont = SWBPlatform.getSemanticMgr().getSchema().getRDFOntModel();
        User user = userRepo.getUserByLogin(login);
        if (null == user) { //Si no existe usuario con ese login
            String encryptdPwd;
            try {
                encryptdPwd = SWBUtils.CryptoWrapper.passwordDigest(id);
            } catch (Exception e) {
                encryptdPwd = id;
            }
            User newUser = userRepo.createUser();
            newUser.setLogin(!"".equals(email) ? email : source + "_" + id);
            newUser.setPassword(encryptdPwd);
            newUser.setLanguage("es");
            newUser.setFirstName(name);
            newUser.setLastName("");
            newUser.setSecondLastName("");
            newUser.setEmail(email);
            newUser.setActive(true);
            
            if (source.equals(SessionInitializer.FACEBOOK)) {
                SemanticObject obj = newUser.getSemanticObject();
                obj.getRDFResource().addLiteral(ont.createDatatypeProperty(
                        SessionInitializer.FACEBOOKID_URI), id);
            }
            user = newUser;
            
        } else {  //si existe un usuario con ese correo, revisar si viene de la misma red social
            //revisar que el usuario tenga un identificador correspondiente a la red social
            if (SessionInitializer.FACEBOOK.equals(source)) {
                if (user.getSemanticObject().getRDFResource()
                        .getProperty(ont.createDatatypeProperty(
                                SessionInitializer.FACEBOOKID_URI)) == null) {
                    SemanticObject obj = user.getSemanticObject();
                    obj.getRDFResource().addLiteral(ont.createDatatypeProperty(
                            SessionInitializer.FACEBOOKID_URI), id);
                }
            }
        }
        
        System.out.println("Propiedad literal facebookId: "
                + user.getSemanticObject().getRDFResource().getProperty(ont.createDatatypeProperty(SessionInitializer.FACEBOOKID_URI)).getString());
//                + ((String) user.getSemanticObject().getRDFResource().getProperty(ont.createDatatypeProperty(SessionInitializer.FACEBOOKID_URI)).getLiteral().getValue().getClass().getName()));
        
        Iterator<SemanticProperty> iSP = user.getSemanticObject().listProperties();
        while (iSP.hasNext()) {
            SemanticProperty sp = iSP.next();
            System.out.println("  - propiedad de user: " + sp.getName());
        }
        
        return user;
    }
    
    /**
     * Provoca que el navegador del cliente muestre la ruta indicada en {@code url}
     * @param request 
     * @param response 
     * @param paramRequest 
     */
    public void doRedirect(HttpServletRequest request, HttpServletResponse response,
            org.semanticwb.portal.api.SWBParamRequest paramRequest) {
        
        try {
            String url = paramRequest.getWebPage().getRealUrl();
            if (paramRequest.getAction().equals("closeSession")) {
                WebPage home = WebPage.ClassMgr.getWebPage("home", paramRequest.getWebPage().getWebSite());
                url = home.getRealUrl(paramRequest.getUser().getLanguage());
            }
            response.setContentType("Text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><head><meta http-equiv=\"Refresh\" CONTENT=\"0; URL=" +
                    url + "\" /><script>window.location='" + url +
                    "';</script></head></html>");
            out.flush();
        } catch (IOException e) {
            LOG.error("Redirecting user", e);
        }
    }

    //Dirige la peticion al metodo correspondiente al modo solicitado
    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) throws SWBResourceException, IOException {
        
        if (paramRequest.getMode().equals(SessionInitializer.REDIRECT_MODE)) {
            doRedirect(request, response, paramRequest);
        } else {
            super.processRequest(request, response, paramRequest);
        }
    }
    
}
