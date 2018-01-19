package mx.gob.cultura.portal.resources;

import com.hp.hpl.jena.ontology.OntModel;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Iterator;
import javax.security.auth.Subject;
import org.semanticwb.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.semanticwb.SWBPlatform;
import org.semanticwb.SWBPortal;
import org.semanticwb.SWBUtils;
import org.semanticwb.model.User;
import org.semanticwb.model.UserRepository;
import org.semanticwb.platform.SemanticObject;
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
    
    private static final String FACEBOOK = "facebook";
    
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
                createSignedSession(request, response, paramRequest);
            }
        }
    }
    
    /**
     * Genera la interface de usuario correspondiente a la vista de estrategia
     * @param request
     * @param response
     * @param paramRequest 
     */
    private void showStrategyView(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) {
        
        StringBuilder text = new StringBuilder(256);
        String mainLabel;
        
        if (!paramRequest.getUser().isSigned()) {
            String faceAppId;
            String faceVersion;
            try {
                mainLabel = paramRequest.getLocaleString("lbl_main");
            } catch (SWBResourceException swbe) {
                mainLabel = "Inicia sesión";
            }
            try {
                faceAppId = paramRequest.getLocaleString("vl_facebook_appid");
            } catch (SWBResourceException swbe) {
                faceAppId = "";
                swbe.printStackTrace(System.err);
                LOG.error("There is no app id configured for Facebook");
            }
            try {
                faceVersion = paramRequest.getLocaleString("vl_facebook_version");
            } catch (SWBResourceException swbe) {
                faceVersion = "v2.11";
            }
            
            text.append("<script>\n");
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
            //text.append("    FB.AppEvents.logPageView();\n");
            System.out.println("Revisa estado de login -inmediato a la carga de la pag.");
            text.append("    FB.getLoginStatus(function(response) {\n");
            text.append("      mystatusChangeCallback(response);\n");
            text.append("    });");
            text.append("  };\n");
            text.append("  console.log('Se cargó la página!');\n");
            text.append("  function checkLoginState() {\n");
            text.append("    FB.getLoginStatus(function(response) { console.log('Va a revisar usuario!');\n");
            text.append("      console.log(JSON.stringify(response));\n");
            text.append("      mystatusChangeCallback(response);\n");
            text.append("    });\n");
            text.append("  }\n");
            text.append("  (function(d, s, id){\n");
            text.append("     var js, fjs = d.getElementsByTagName(s)[0];\n");
            text.append("     if (d.getElementById(id)) {return;}\n");
            text.append("     js = d.createElement(s); js.id = id;\n");
            text.append("     js.src = \"https://connect.facebook.net/en_US/sdk.js\";\n");
            text.append("     fjs.parentNode.insertBefore(js, fjs);\n");
            text.append("  }(document, 'script', 'facebook-jssdk'));\n");
            text.append("  function mystatusChangeCallback(response) {\n");
            text.append("    if (response.status && response.status === 'connected') {\n");
            text.append("      //reenviar a seccion con id de usuario de facebook y crear sesion con SWB\n");
            text.append("      console.log('Listo para iniciar sesion SWB:.... ' + JSON.stringify(response));\n");
//            text.append("      var thisIsMe = response.authResponse.userID;\n");
//            text.append("      FB.api('/' + thisIsMe, function(response) {\n");
//            text.append("        console.log('Successful login for: ' + JSON.stringify(response));\n");
//            text.append("      });\n");
            text.append("      openSWBSession();");
            text.append("    } else {\n");
            text.append("      console.log('El usuario no está en sesión!');\n");
            text.append("      ");
            text.append("    }\n");
            text.append("  }\n");
            
            String url = paramRequest.getActionUrl().setCallMethod(
                         SWBParamRequest.Call_DIRECT).toString();
            text.append("  function openSWBSession() {\n");
            text.append("    FB.api('/me', function(response) {\n");
            text.append("      var name = response.name ? response.name : '';\n");
            text.append("      var faceId = response.id ? response.id : '';\n");
            text.append("      var email = response.email ? response.email : '';\n");
            text.append("      var xhttp = new XMLHttpRequest();\n");
            text.append("      xhttp.onreadystatechange = function() {");
            text.append("        if (this.readyState == 4 && this.status == 200) {");
            text.append("          location.reload();");
            text.append("        }");
            text.append("      };");
            text.append("      xhttp.open(\"POST\", \"");
            text.append(url);
            text.append("\", false);\n"); //false = sincrona
            text.append("      xhttp.setRequestHeader(\"Content-type\", \"application/x-www-form-urlencoded\");\n");
            text.append("      xhttp.send(\"id=\"+faceId+\"&email=\"+email+\"&name=\"+name+\"&source=");
            text.append(SessionInitializer.FACEBOOK);
            text.append("\");\n");
            text.append("    });");
            text.append("    ");
            text.append("    ");
            text.append("  }\n");
            text.append("</script>\n");
            //FB.login() - abre el cuadro de dialogo de inicio de sesión
            text.append("");
            text.append("");
            text.append("<p class=\"navlog\"><span class=\"ion-person\"></span> ");
            text.append(mainLabel);
            text.append("</p>\n");
            text.append("<div>\n");
            text.append("  <div>\n");
            text.append("    Mostrar formulario para que el usuario se firme en el sitio, ");
            text.append("    o use una red social:\n");
            text.append("<div class=\"fb-login-button\" data-width=\"400\" data-max-rows=\"1\""
                    + " data-size=\"medium\" data-button-type=\"continue_with\" "
                    + "data-show-faces=\"false\" data-auto-logout-link=\"true\" "
                    + "data-scope=\"public_profile,email\" "
                    + "data-use-continue-as=\"false\" onlogin=\"openSWBSession();\"></div>\n");
            /*text.append("<fb:login-button scope=\"public_profile,email\" onlogin=\"checkLoginState();\">");
            text.append("</fb:login-button>");*/



            text.append("  </div>\n");
            text.append("</div>\n");
            text.append("");
            text.append("");
            text.append("");
            text.append("");
            text.append("");
            text.append("");
            text.append("</div>\n");
        } else {
            try {
                mainLabel = paramRequest.getLocaleString("lbl_out");
            } catch (SWBResourceException swbe) {
                mainLabel = "Termina sesión";
            }
            text.append("<p class=\"navlog\"><span class=\"ion-person\"></span> ");
            text.append(mainLabel);
            text.append("</p>\n");
        }
        
        try {
            PrintWriter out = response.getWriter();
            out.println(text.toString());
        } catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }
    }
    
    private void showContentView(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) {
        
    }
    
    private void createSignedSession(HttpServletRequest request, HttpServletResponse response,
            SWBParamRequest paramRequest) {
        
        
    }

    //Atiende la peticion para registrar al usuario como firmado en la aplicacion
    @Override
    public void processAction(HttpServletRequest request, SWBActionResponse response)
            throws SWBResourceException, IOException {
        
        UserRepository userRepo = response.getWebPage().getWebSite().getUserRepository();
        User user = null;
        boolean isSocialNetUser = false;
        System.out.println("Ejecucion en processAction!!!");
        //solo crear usuarios si usan una red social
        if (request.getParameter("source") != null && !request.getParameter("source").isEmpty()) {
            System.out.println("Va a entrar a crear usuario...\n");
            user = getSWBUser(request, response, userRepo);
            isSocialNetUser = true;
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
                user.setLanguage("es");   //forzar lenguage si no se dio de alta.
            }
        }
        response.setMode(SessionInitializer.REDIRECT_MODE);
//        System.out.println("Redireccionando a: " + response.getWebPage().getRealUrl() + "?logged=in");
//        response.sendRedirect(response.getWebPage().getRealUrl() + "?logged=in");
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
            super.processRequest(request, response, paramRequest); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
}
