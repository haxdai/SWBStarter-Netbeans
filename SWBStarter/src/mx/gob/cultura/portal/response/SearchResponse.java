package mx.gob.cultura.portal.response;

import java.io.Serializable;
import java.util.List;

/**
 * Class to encapsulate search response information.
 * @author Hasdai Pacheco
 */
public class SearchResponse implements Serializable {
    List<Entry> records;
    private String took;
    private int total;

    /**
     * Gets string with response time information.
     * @return String in format ##ms with milliseconds taken by search request.
     */
    public String getTook() {
        return took;
    }

    /**
     * Sets string with response time information
     * @param took String in format ##ms with milliseconds taken by search request.
     */
    public void setTook(String took) {
        this.took = took;
    }

    /**
     * Gets total number of results found.
     * @return total number of results found.
     */
    public int getTotal() {
        return total;
    }

    /**
     * Sets total number of results found.
     * @param total total number of results found.
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * Gets the list of records found by the search query.
     * @return List of {@link Entry} objects.
     */
    public List<Entry> getRecords() {
        return records;
    }

    /**
     * Sets the list of records found by the search query.
     * @param records List of {@link Entry} objects.
     */
    public void setRecords(List<Entry> records) {
        this.records = records;
    }
}
