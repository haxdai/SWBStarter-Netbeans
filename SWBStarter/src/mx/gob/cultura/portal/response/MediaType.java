package mx.gob.cultura.portal.response;

/**
 * Class to encapsulate MediaType information for a digital object in search responses.
 * @author Hasdai Pacheco
 */
public class MediaType {
    private String name;
    private String mime;

    /**
     * Gets human readable name (e.g. JPEG image, PNG image, PDF document)
     * @return Human readable name of media type.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a human readable name for the media type.
     * @param name Human readable name of media type.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets mime-type string for media type.
     * @return mime-type string for media type.
     */
    public String getMime() {
        return mime;
    }

    /**
     * Sets mime-type string for media type.
     * @param mime mime-type string for media type.
     */
    public void setMime(String mime) {
        this.mime = mime;
    }
}
