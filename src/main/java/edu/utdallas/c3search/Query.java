package edu.utdallas.c3search;

import java.util.ArrayList;

/**
 * Created by Shadow on 11/30/16.
 */
public class Query {

    public enum Keyword { AND, OR, NOT }

    private ArrayList<String> first;
    private ArrayList<String> second;
    private Keyword keyword;

    public ArrayList<String> getFirst() {
        return first;
    }

    public ArrayList<String> getSecond() {
        return second;
    }

    public Keyword getKeyword() {
        return keyword;
    }

    public void setFirst(ArrayList<String> first) {
        this.first = first;
    }

    public void setKeyword(Keyword keyword) {
        this.keyword = keyword;
    }

    public void setSecond(ArrayList<String> second) {
        this.second = second;
    }
}
