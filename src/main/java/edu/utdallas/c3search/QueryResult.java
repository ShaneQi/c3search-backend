package edu.utdallas.c3search;

import java.util.ArrayList;

/**
 * Created by Shadow on 11/30/16.
 */
public class QueryResult {
    private String query;
    private int count;
    private ArrayList<WebEntry> results;


    public void setQuery(String query) {
        this.query = query;
    }

    public void setResults(ArrayList<WebEntry> results) {
        this.results = results;
    }


    public ArrayList<WebEntry> getResults() {
        return results;
    }

    public String getQuery() {
        return query;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
