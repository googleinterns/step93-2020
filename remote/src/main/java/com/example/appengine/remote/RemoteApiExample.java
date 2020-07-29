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
      Entity pageViews1 = new Entity("PageViews");
      pageViews1.setProperty("restaurantKey", 1);
      pageViews1.setProperty("restaurantName", "Wildfire Restaurant");
      pageViews1.setProperty("year", 2020);
      pageViews1.setProperty("week", 26);
      pageViews1.setProperty("count", 39);

      Entity pageViews2 = new Entity("PageViews");
      pageViews2.setProperty("restaurantKey", 1);
      pageViews2.setProperty("restaurantName", "Wildfire Restaurant");
      pageViews2.setProperty("year", 2020);
      pageViews2.setProperty("week", 27);
      pageViews2.setProperty("count", 40);

      Entity pageViews3 = new Entity("PageViews");
      pageViews3.setProperty("restaurantKey", 1);
      pageViews3.setProperty("restaurantName", "Wildfire Restaurant");
      pageViews3.setProperty("year", 2020);
      pageViews3.setProperty("week", 28);
      pageViews3.setProperty("count", 26);

      Entity pageViews4 = new Entity("PageViews");
      pageViews4.setProperty("restaurantKey", 1);
      pageViews4.setProperty("restaurantName", "Wildfire Restaurant");
      pageViews4.setProperty("year", 2020);
      pageViews4.setProperty("week", 29);
      pageViews4.setProperty("count", 21);

      Entity pageViews5 = new Entity("PageViews");
      pageViews5.setProperty("restaurantKey", 1);
      pageViews5.setProperty("restaurantName", "Wildfire Restaurant");
      pageViews5.setProperty("year", 2020);
      pageViews5.setProperty("week", 30);
      pageViews5.setProperty("count", 29);

      Entity pageViews6 = new Entity("PageViews");
      pageViews6.setProperty("restaurantKey", 2);
      pageViews6.setProperty("restaurantName", "Lazy Dog Restaurant & Bar");
      pageViews6.setProperty("year", 2020);
      pageViews6.setProperty("week", 26);
      pageViews6.setProperty("count", 11);

      Entity pageViews7 = new Entity("PageViews");
      pageViews7.setProperty("restaurantKey", 2);
      pageViews7.setProperty("restaurantName", "Lazy Dog Restaurant & Bar");
      pageViews7.setProperty("year", 2020);
      pageViews7.setProperty("week", 27);
      pageViews7.setProperty("count", 18);

      Entity pageViews8 = new Entity("PageViews");
      pageViews8.setProperty("restaurantKey", 2);
      pageViews8.setProperty("restaurantName", "Lazy Dog Restaurant & Bar");
      pageViews8.setProperty("year", 2020);
      pageViews8.setProperty("week", 28);
      pageViews8.setProperty("count", 21);

      Entity pageViews9 = new Entity("PageViews");
      pageViews9.setProperty("restaurantKey", 2);
      pageViews9.setProperty("restaurantName", "Lazy Dog Restaurant & Bar");
      pageViews9.setProperty("year", 2020);
      pageViews9.setProperty("week", 29);
      pageViews9.setProperty("count", 31);

      Entity pageViews10 = new Entity("PageViews");
      pageViews10.setProperty("restaurantKey", 2);
      pageViews10.setProperty("restaurantName", "Lazy Dog Restaurant & Bar");
      pageViews10.setProperty("year", 2020);
      pageViews10.setProperty("week", 30);
      pageViews10.setProperty("count", 10);
 
      Entity pageViews11 = new Entity("PageViews");
      pageViews11.setProperty("restaurantKey", 3);
      pageViews11.setProperty("restaurantName", "Alinea in Residence");
      pageViews11.setProperty("year", 2020);
      pageViews11.setProperty("week", 26);
      pageViews11.setProperty("count", 19);
 
      Entity pageViews12 = new Entity("PageViews");
      pageViews12.setProperty("restaurantKey", 3);
      pageViews12.setProperty("restaurantName", "Alinea in Residence");
      pageViews12.setProperty("year", 2020);
      pageViews12.setProperty("week", 27);
      pageViews12.setProperty("count", 21);
 
      Entity pageViews13 = new Entity("PageViews");
      pageViews13.setProperty("restaurantKey", 3);
      pageViews13.setProperty("restaurantName", "Alinea in Residence");
      pageViews13.setProperty("year", 2020);
      pageViews13.setProperty("week", 28);
      pageViews13.setProperty("count", 16);

      Entity pageViews14 = new Entity("PageViews");
      pageViews14.setProperty("restaurantKey", 3);
      pageViews14.setProperty("restaurantName", "Alinea in Residence");
      pageViews14.setProperty("year", 2020);
      pageViews14.setProperty("week", 29);
      pageViews14.setProperty("count", 12);
 
      Entity pageViews15 = new Entity("PageViews");
      pageViews15.setProperty("restaurantKey", 3);
      pageViews15.setProperty("restaurantName", "Alinea in Residence");
      pageViews15.setProperty("year", 2020);
      pageViews15.setProperty("week", 30);
      pageViews15.setProperty("count", 4);
    
      Entity pageViews16 = new Entity("PageViews");
      pageViews16.setProperty("restaurantKey", 4);
      pageViews16.setProperty("restaurantName", "Big Bowl Chinese and Thai");
      pageViews16.setProperty("year", 2020);
      pageViews16.setProperty("week", 26);
      pageViews16.setProperty("count", 34);
    
      Entity pageViews17 = new Entity("PageViews");
      pageViews17.setProperty("restaurantKey", 4);
      pageViews17.setProperty("restaurantName", "Big Bowl Chinese and Thai");
      pageViews17.setProperty("year", 2020);
      pageViews17.setProperty("week", 27);
      pageViews17.setProperty("count", 31);
 
      Entity pageViews18 = new Entity("PageViews");
      pageViews18.setProperty("restaurantKey", 4);
      pageViews18.setProperty("restaurantName", "Big Bowl Chinese and Thai");
      pageViews18.setProperty("year", 2020);
      pageViews18.setProperty("week", 28);
      pageViews18.setProperty("count", 40);
    
      Entity pageViews19 = new Entity("PageViews");
      pageViews19.setProperty("restaurantKey", 4);
      pageViews19.setProperty("restaurantName", "Big Bowl Chinese and Thai");
      pageViews19.setProperty("year", 2020);
      pageViews19.setProperty("week", 29);
      pageViews19.setProperty("count", 22);
    
      Entity pageViews20 = new Entity("PageViews");
      pageViews20.setProperty("restaurantKey", 4);
      pageViews20.setProperty("restaurantName", "Big Bowl Chinese and Thai");
      pageViews20.setProperty("year", 2020);
      pageViews20.setProperty("week", 30);
      pageViews20.setProperty("count", 28);
    
      Entity pageViews21 = new Entity("PageViews");
      pageViews21.setProperty("restaurantKey", 5);
      pageViews21.setProperty("restaurantName", "Mickey Finn’s Bar and Grill");
      pageViews21.setProperty("year", 2020);
      pageViews21.setProperty("week", 26);
      pageViews21.setProperty("count", 17);
 
      Entity pageViews22 = new Entity("PageViews");
      pageViews22.setProperty("restaurantKey", 5);
      pageViews22.setProperty("restaurantName", "Mickey Finn’s Bar and Grill");
      pageViews22.setProperty("year", 2020);
      pageViews22.setProperty("week", 27);
      pageViews22.setProperty("count", 22);
    
      Entity pageViews23 = new Entity("PageViews");
      pageViews23.setProperty("restaurantKey", 5);
      pageViews23.setProperty("restaurantName", "Mickey Finn’s Bar and Grill");
      pageViews23.setProperty("year", 2020);
      pageViews23.setProperty("week", 28);
      pageViews23.setProperty("count", 29);
    
      Entity pageViews24 = new Entity("PageViews");
      pageViews24.setProperty("restaurantKey", 5);
      pageViews24.setProperty("restaurantName", "Mickey Finn’s Bar and Grill");
      pageViews24.setProperty("year", 2020);
      pageViews24.setProperty("week", 29);
      pageViews24.setProperty("count", 23);
    
      Entity pageViews25 = new Entity("PageViews");
      pageViews25.setProperty("restaurantKey", 5);
      pageViews25.setProperty("restaurantName", "Mickey Finn’s Bar and Grill");
      pageViews25.setProperty("year", 2020);
      pageViews25.setProperty("week", 30);
      pageViews25.setProperty("count", 18);

      // Do a batch operation to put all the entities in parallel
      List<Entity> pageViews = Arrays.asList(
          pageViews1, pageViews2, pageViews3, pageViews4, pageViews5,
          pageViews6, pageViews7, pageViews8, pageViews9, pageViews10, 
          pageViews11, pageViews12, pageViews13,pageViews14, pageViews15,
          pageViews16, pageViews17, pageViews18, pageViews19, pageViews20, 
          pageViews21, pageViews22, pageViews23, pageViews24, pageViews25);
      ds.put(pageViews);
      System.out.println("success");
    } finally {
      installer.uninstall();
    }
  }
}
