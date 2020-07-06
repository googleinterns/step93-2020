package com.google.step.servlets;

import com.google.appengine.repackaged.com.google.gson.Gson;
import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("search")
public class testSearch extends HttpServlet {
  private static final String elasticsearchHostname = "34.67.78.46";
  //static final String elasticsearchHostname = "localhost";

  private final RestHighLevelClient client = new RestHighLevelClient(
      RestClient.builder(
          new HttpHost(elasticsearchHostname, 9200)
      )
  );
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    GetRequest getRequest = new GetRequest(
        "twitter",
        "1");

    GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

    if (getResponse.isExists()) {
      Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();

      response.setContentType("application/json");

      Gson gson = new Gson();
      response.getWriter().println(gson.toJson(sourceAsMap));

    } else {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
  }
}
