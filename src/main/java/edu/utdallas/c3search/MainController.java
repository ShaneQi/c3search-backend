package edu.utdallas.c3search;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.glass.ui.EventLoop;
import org.apache.catalina.startup.RealmRuleSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Shadow on 11/30/16.
 */
@Controller
@RequestMapping("/search")
public class MainController {


    @RequestMapping(value="{query}", method = RequestMethod.GET)
    public @ResponseBody
    QueryResult getShopInJSON(@PathVariable String query) {
        return search(parseQuery(query));
    }

    private Query parseQuery(String query) {
        final ArrayList<String> keywords = new ArrayList<>();
        keywords.add("NOT");
        keywords.add("AND");
        keywords.add("OR");
        Query parsed = new Query();
        ArrayList<String> first = new ArrayList<>();
        ArrayList<String> second = new ArrayList<>();
        String[] words = query.split(" ");
        String keyword = null;
        for (String word: words) {
            if (keyword == null) {
                if (keywords.contains(word)){
                    keyword = word;
                } else {
                    first.add(word);
                }
            } else {
                second.add(word);
            }
        }
        parsed.setFirst(first);
        parsed.setSecond(second);
        if (keyword != null) {
            switch (keyword) {
                case "NOT":
                    parsed.setKeyword(Query.Keyword.NOT);
                    break;
                case "AND":
                    parsed.setKeyword(Query.Keyword.AND);
                    break;
                case "OR":
                    parsed.setKeyword(Query.Keyword.OR);
                    break;
            }
        } else { parsed.setKeyword(null); }
        return parsed;
    }

    private QueryResult search(Query query) {
        QueryResult queryResult = new QueryResult();
        ArrayList<WebEntry> entryResults = new ArrayList<>();
        final String shiftedUrls = "SHIFTED_URLS.shifted_desc";
        String querySting = "SELECT DISTINCT URLS.url, URLS.title, URLS.desc, URLS.hits FROM URLS, SHIFTED_URLS WHERE SHIFTED_URLS. url = URLS.url AND (";
        if (query.getKeyword() == null) {
            querySting += (shiftedUrls + " LIKE \'%" + query.getFirst().get(0) + "%\'");
            for (int i = 1; i < query.getFirst().size(); i++) {
                querySting += (" AND " + shiftedUrls + " LIKE \'%" + query.getFirst().get(i) + "%\'");
            }
        } else if (query.getKeyword() == Query.Keyword.AND) {
            ArrayList<String> words = new ArrayList<String>(query.getFirst());
            words.addAll(query.getSecond());
            querySting += (shiftedUrls + " LIKE \'%" + words.get(0) + "%\'");
            for (int i = 1; i < words.size(); i++) {
                querySting += (" AND " + shiftedUrls + " LIKE \'%" + words.get(i) + "%\'");
            }
        } else if (query.getKeyword() == Query.Keyword.NOT) {
            querySting += (shiftedUrls + " LIKE \'%" + query.getFirst().get(0) + "%\'");
            for (int i = 1; i < query.getFirst().size(); i++) {
                querySting += (" AND " + shiftedUrls + " LIKE \'%" + query.getFirst().get(i) + "%\'");
            }
            for (int i = 0; i < query.getSecond().size(); i++) {
                querySting += (" AND " + shiftedUrls + " NOT LIKE \'%" + query.getSecond().get(i) + "%\'");
            }
        } else if (query.getKeyword() == Query.Keyword.OR) {
            querySting += (shiftedUrls + " LIKE \'%" + query.getFirst().get(0) + "%\'");
            for (int i = 1; i < query.getFirst().size(); i++) {
                querySting += (" AND " + shiftedUrls + " LIKE \'%" + query.getFirst().get(i) + "%\'");
            }
            for (int i = 0; i < query.getFirst().size(); i++) {
                querySting += (" OR " + shiftedUrls + " LIKE \'%" + query.getFirst().get(i) + "%\'");
            }
        }
        querySting += ");";
        Connection connection = SQLite.getConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            System.out.println(querySting);
            ResultSet resultSet = statement.executeQuery(querySting);
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String url = resultSet.getString("url");
                String description = resultSet.getString("desc");
                WebEntry we = new WebEntry(title, url, description);
                entryResults.add(we);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        queryResult.setQuery(query.toString());
        queryResult.setCount(entryResults.size());
        queryResult.setResults(entryResults);
        return queryResult;
    }

    private Set<WebEntry> search(ArrayList<String> words) {
        try {
            Statement statement = SQLite.getConnection().createStatement();
            statement.setEscapeProcessing(true);
            statement.setQueryTimeout(5);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    static List<String> shift(String line) {
        List<String> result = new ArrayList<>();
        String[] words = line.split(" ");
        int count = words.length;
        for (int i = 0; i < count; i++) {
            result.add(shift(words, i));
        }
        return result;
    }

    static String shift(String[] words, int start) {
        List<String> shiftedWords = new ArrayList<>();
        int pointer = start;
        do {
            shiftedWords.add(words[pointer]);
            pointer += 1;
            if (pointer == words.length) { pointer = 0; }
        } while (pointer != start);
        return String.join(" ", shiftedWords);
    }

    public static void mainAAA(String[] args) throws ClassNotFoundException
    {
        // load the sqlite-JDBC driver using the current class loader
        Class.forName("org.sqlite.JDBC");

        Connection connection = null;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:/Users/Shadow/Desktop/db.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(5);  // set timeout to 30 sec.

            String contents = null;
            try {
                contents = new String(Files.readAllBytes(Paths.get("/Users/Shadow/Desktop/urls.json")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            JsonElement jelement = new JsonParser().parse(contents);
            JsonArray jarray = jelement.getAsJsonArray();


//            JsonArray jso = new JsonArray();
//            ResultSet rs = statement.executeQuery("SELECT shifted_desc FROM SHIFTED_URLS;");
//            while (rs.next()) {
//                String desc = rs.getString("shifted_desc");
//                String[] words = desc.split(" ");
//                Set<String> wordss = new HashSet<>();
//                if (words.length > 0) { wordss.add(words[0]); }
//                String finalString = String.join(" ", wordss);
//                jso.add(finalString);
//            }
//            System.out.println(jso.toString());
//            if (true) { return; }

            statement.executeUpdate("DELETE FROM URLS;");
            statement.executeUpdate("DELETE FROM SHIFTED_URLS;");
            for (JsonElement jsonElement: jarray) {
                JsonObject object = jsonElement.getAsJsonObject();
                String title = object.get("tittle").toString().replace("\"", "").replace("\'", "\'\'").replace("\n", "").replace("\\n", "");
                String url = object.get("url").toString().replace("\"", "").replace("\'", "\'\'").replace("\\n", "");
                String desc = "";
                if (object.get("desc").getAsJsonArray().size() > 0) {
                    desc = object.get("desc").getAsJsonArray().get(0).toString().replace("\n", "").replace("\"", "").replace("\'", "\'\'").replace("\\n", "");
                }

                System.out.println("INSERT INTO URLS VALUES ('" + url + "', '" + title + "', '" + desc + "', '0');");
                statement.executeUpdate("INSERT INTO URLS VALUES ('" + url + "', '" + title + "', '" + desc + "', '0');");



                List<String> shifted = shift(desc);

                for (String shiftedDesc: shifted) {
                    System.out.println("INSERT INTO SHIFTED_URLS VALUES ('"
                            + shiftedDesc + "', '" + url + "');");
                    statement.executeUpdate(("INSERT INTO SHIFTED_URLS VALUES ('"
                            + shiftedDesc + "', '" + url + "');"));

                }

            }
            statement.executeUpdate("DELETE FROM SHIFTED_URLS WHERE shifted_desc='';");
//            statement.executeUpdate("create table person (id integer, name string)");
//            statement.executeUpdate("insert into person values(1, 'leo')");
//            statement.executeUpdate("insert into person values(2, 'yui')");
//            ResultSet rs = statement.executeQuery("select * from person");
//            while(rs.next())
//            {
//                // read the result set
//                System.out.println("name = " + rs.getString("name"));
//                System.out.println("id = " + rs.getInt("id"));
//            }
        }
        catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e);
            }
        }
    }

}
