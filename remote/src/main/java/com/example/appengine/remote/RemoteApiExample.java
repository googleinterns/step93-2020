package com.example.appengine.remote;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RemoteApiExample {
  public static void main(String[] args) throws IOException {
    String serverString = args[0];
    System.out.println(serverString);
    RemoteApiOptions options;
    if (serverString.equals("localhost")) {
      options = new RemoteApiOptions().server(serverString, 8080).useDevelopmentServerCredential();
    } else {
      options = new RemoteApiOptions().server(serverString, 443).useApplicationDefaultCredential();
    }
    RemoteApiInstaller installer = new RemoteApiInstaller();
    installer.install(options);
    try {
      DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
      Entity entity = new Entity("RestaurantInfo");
      entity.setProperty("restaurantKey", 2);
      entity.setProperty("name", "le macDo");
      entity.setProperty("location", new GeoPt((float) 42.1784405, (float) -87.9284299));
      entity.setProperty("story", "macDos");
      entity.setProperty("cuisine", Arrays.asList("Burgurz", "American"));
      entity.setProperty("phone", "82939348");
      entity.setProperty("website", "https://www.mcdonalds.com");
      ds.put(entity);
      System.out.println("success");
    } finally {
      installer.uninstall();
    }
  }
}
