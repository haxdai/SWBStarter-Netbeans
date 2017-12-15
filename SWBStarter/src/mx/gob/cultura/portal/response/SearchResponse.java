package mx.gob.cultura.portal.response;

import java.io.Serializable;
import java.util.List;

public class SearchResponse implements Serializable {
    private String took;
    private int total;
    List<Entry> records;

    public String getTook() {
        return took;
    }

    public void setTook(String took) {
        this.took = took;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Entry> getRecords() {
        return records;
    }

    public void setRecords(List<Entry> records) {
        this.records = records;
    }
}
