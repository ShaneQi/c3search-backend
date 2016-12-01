package edu.utdallas.c3search;

/**
 * Created by Shadow on 11/30/16.
 */
public class WebEntry {
    private String title;
    private String url;
    private String description;

    public WebEntry(String title, String url, String description) {
        this.title = title;
        this.url = url;
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
