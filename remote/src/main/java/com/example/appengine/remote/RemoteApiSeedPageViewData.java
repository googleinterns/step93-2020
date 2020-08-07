package com.example.appengine.remote;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;
import com.google.step.clients.RestaurantClient;
import com.google.step.data.Restaurant;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RemoteApiSeedPageViewData {
  public static void main(String[] args) throws IOException {
    String serverString = args[0];
    RemoteApiOptions options;
    if (serverString.equals("localhost")) {
      options = new RemoteApiOptions().server(serverString, 8080).useDevelopmentServerCredential();
    } else {
      options = new RemoteApiOptions().server(serverString, 443).useApplicationDefaultCredential();
    }
    RemoteApiInstaller installer = new RemoteApiInstaller();
    installer.install(options);
    try {
      RestaurantClient restaurantClient = new RestaurantClient();
      DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
      final String STATUS_GOOD = "GOOD";
      final String STATUS_OKAY = "OKAY";
      final String STATUS_STRUGGLING = "STRUGGLING";
      Restaurant restaurant1 = new Restaurant(null, "Wildfire Restaurant",
              new GeoPt((float) 42.1784405, (float) -87.9284299),
              "Swanky American steakhouse",
              Arrays.asList("Steakhouse", "American"), "847-234-5678",
              "https://wildfirerestaurant.com", STATUS_STRUGGLING);
      long restaurantKey1 = restaurantClient.putRestaurant(restaurant1, "wildfire@gmail.com");

      Entity pageViews1 = new Entity("PageViews");
      pageViews1.setProperty("restaurantKey", restaurantKey1);
      pageViews1.setProperty("restaurantName", "Wildfire Restaurant");
      pageViews1.setProperty("year", 2020);
      pageViews1.setProperty("week", 26);
      pageViews1.setProperty("count", 39);

      Entity pageViews2 = new Entity("PageViews");
      pageViews2.setProperty("restaurantKey", restaurantKey1);
      pageViews2.setProperty("restaurantName", "Wildfire Restaurant");
      pageViews2.setProperty("year", 2020);
      pageViews2.setProperty("week", 27);
      pageViews2.setProperty("count", 40);

      Entity pageViews3 = new Entity("PageViews");
      pageViews3.setProperty("restaurantKey", restaurantKey1);
      pageViews3.setProperty("restaurantName", "Wildfire Restaurant");
      pageViews3.setProperty("year", 2020);
      pageViews3.setProperty("week", 28);
      pageViews3.setProperty("count", 26);

      Entity pageViews4 = new Entity("PageViews");
      pageViews4.setProperty("restaurantKey", restaurantKey1);
      pageViews4.setProperty("restaurantName", "Wildfire Restaurant");
      pageViews4.setProperty("year", 2020);
      pageViews4.setProperty("week", 29);
      pageViews4.setProperty("count", 21);

      Entity pageViews5 = new Entity("PageViews");
      pageViews5.setProperty("restaurantKey", restaurantKey1);
      pageViews5.setProperty("restaurantName", "Wildfire Restaurant");
      pageViews5.setProperty("year", 2020);
      pageViews5.setProperty("week", 30);
      pageViews5.setProperty("count", 29);

      Restaurant restaurant2 = new Restaurant(null, "Lazy Dog Restaurant and Bar",
              new GeoPt((float) 42.1784888, (float) -87.9284888),
              "Relaxed, lodge-chic chain serving global comfort fare, including stir-fries, pot roast & pastas.",
              Arrays.asList("Comfort food", "American"), "847-780-7977",
              "https://lazydogrestaurants.com", STATUS_OKAY);
      long restaurantKey2 = restaurantClient.putRestaurant(restaurant2, "lazydog@gmail.com");

      Entity pageViews6 = new Entity("PageViews");
      pageViews6.setProperty("restaurantKey", restaurantKey2);
      pageViews6.setProperty("restaurantName", "Lazy Dog Restaurant & Bar");
      pageViews6.setProperty("year", 2020);
      pageViews6.setProperty("week", 26);
      pageViews6.setProperty("count", 11);

      Entity pageViews7 = new Entity("PageViews");
      pageViews7.setProperty("restaurantKey", restaurantKey2);
      pageViews7.setProperty("restaurantName", "Lazy Dog Restaurant & Bar");
      pageViews7.setProperty("year", 2020);
      pageViews7.setProperty("week", 27);
      pageViews7.setProperty("count", 18);

      Entity pageViews8 = new Entity("PageViews");
      pageViews8.setProperty("restaurantKey", restaurantKey2);
      pageViews8.setProperty("restaurantName", "Lazy Dog Restaurant & Bar");
      pageViews8.setProperty("year", 2020);
      pageViews8.setProperty("week", 28);
      pageViews8.setProperty("count", 21);

      Entity pageViews9 = new Entity("PageViews");
      pageViews9.setProperty("restaurantKey", restaurantKey2);
      pageViews9.setProperty("restaurantName", "Lazy Dog Restaurant & Bar");
      pageViews9.setProperty("year", 2020);
      pageViews9.setProperty("week", 29);
      pageViews9.setProperty("count", 31);

      Entity pageViews10 = new Entity("PageViews");
      pageViews10.setProperty("restaurantKey", restaurantKey2);
      pageViews10.setProperty("restaurantName", "Lazy Dog Restaurant & Bar");
      pageViews10.setProperty("year", 2020);
      pageViews10.setProperty("week", 30);
      pageViews10.setProperty("count", 10);

      Restaurant restaurant3 = new Restaurant(null, "Alinea in Residence",
              new GeoPt((float) 41.1784888, (float) -88.9284888),
              "Chef Grant Achatz draws foodies with New American tasting menus featuring highly creative plates.",
              Arrays.asList("Molecular gastronomy", "American"), "847-888-9999",
              "https://alinearestaurant.com", STATUS_STRUGGLING);
      long restaurantKey3 = restaurantClient.putRestaurant(restaurant3, "alinea@gmail.com");
 
      Entity pageViews11 = new Entity("PageViews");
      pageViews11.setProperty("restaurantKey", restaurantKey3);
      pageViews11.setProperty("restaurantName", "Alinea in Residence");
      pageViews11.setProperty("year", 2020);
      pageViews11.setProperty("week", 26);
      pageViews11.setProperty("count", 19);
 
      Entity pageViews12 = new Entity("PageViews");
      pageViews12.setProperty("restaurantKey", restaurantKey3);
      pageViews12.setProperty("restaurantName", "Alinea in Residence");
      pageViews12.setProperty("year", 2020);
      pageViews12.setProperty("week", 27);
      pageViews12.setProperty("count", 21);
 
      Entity pageViews13 = new Entity("PageViews");
      pageViews13.setProperty("restaurantKey", restaurantKey3);
      pageViews13.setProperty("restaurantName", "Alinea in Residence");
      pageViews13.setProperty("year", 2020);
      pageViews13.setProperty("week", 28);
      pageViews13.setProperty("count", 16);

      Entity pageViews14 = new Entity("PageViews");
      pageViews14.setProperty("restaurantKey", restaurantKey3);
      pageViews14.setProperty("restaurantName", "Alinea in Residence");
      pageViews14.setProperty("year", 2020);
      pageViews14.setProperty("week", 29);
      pageViews14.setProperty("count", 12);
 
      Entity pageViews15 = new Entity("PageViews");
      pageViews15.setProperty("restaurantKey", restaurantKey3);
      pageViews15.setProperty("restaurantName", "Alinea in Residence");
      pageViews15.setProperty("year", 2020);
      pageViews15.setProperty("week", 30);
      pageViews15.setProperty("count", 4);

      Restaurant restaurant4 = new Restaurant(null, "Big Bowl Chinese and Thai",
              new GeoPt((float) 42.1784404, (float) -87.9284100),
              "Restaurant serving Chinese & Thai dishes, cocktails & craft beers in a relaxed, stylish space.",
              Arrays.asList("Chinese", "Thai", "Dumplings"), "847-517-8881",
              "https://bigbowl.com", STATUS_GOOD);
      long restaurantKey4 = restaurantClient.putRestaurant(restaurant4, "bigbowl@gmail.com");
    
      Entity pageViews16 = new Entity("PageViews");
      pageViews16.setProperty("restaurantKey", restaurantKey4);
      pageViews16.setProperty("restaurantName", "Big Bowl Chinese and Thai");
      pageViews16.setProperty("year", 2020);
      pageViews16.setProperty("week", 26);
      pageViews16.setProperty("count", 34);
    
      Entity pageViews17 = new Entity("PageViews");
      pageViews17.setProperty("restaurantKey", restaurantKey4);
      pageViews17.setProperty("restaurantName", "Big Bowl Chinese and Thai");
      pageViews17.setProperty("year", 2020);
      pageViews17.setProperty("week", 27);
      pageViews17.setProperty("count", 31);
 
      Entity pageViews18 = new Entity("PageViews");
      pageViews18.setProperty("restaurantKey", restaurantKey4);
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
      pageViews20.setProperty("restaurantKey", restaurantKey4);
      pageViews20.setProperty("restaurantName", "Big Bowl Chinese and Thai");
      pageViews20.setProperty("year", 2020);
      pageViews20.setProperty("week", 30);
      pageViews20.setProperty("count", 28);

      Restaurant restaurant5 = new Restaurant(null, "Mickey Finns Bar and Grill",
              new GeoPt((float) 43.1784404, (float) -86.9284100),
              "Neighborhood brewery producing ales & lagers & offering a family-friendly menu & regular live music.",
              Arrays.asList("Burgers", "Brewery", "Bar food"), "847-362-6688",
              "https://mickeyfunnsbrewery.com", STATUS_GOOD);
      long restaurantKey5 = restaurantClient.putRestaurant(restaurant5, "mickeyfinns@gmail.com");
    
      Entity pageViews21 = new Entity("PageViews");
      pageViews21.setProperty("restaurantKey", restaurantKey5);
      pageViews21.setProperty("restaurantName", "Mickey Finns Bar and Grill");
      pageViews21.setProperty("year", 2020);
      pageViews21.setProperty("week", 26);
      pageViews21.setProperty("count", 17);
 
      Entity pageViews22 = new Entity("PageViews");
      pageViews22.setProperty("restaurantKey", restaurantKey5);
      pageViews22.setProperty("restaurantName", "Mickey Finns Bar and Grill");
      pageViews22.setProperty("year", 2020);
      pageViews22.setProperty("week", 27);
      pageViews22.setProperty("count", 22);
    
      Entity pageViews23 = new Entity("PageViews");
      pageViews23.setProperty("restaurantKey", restaurantKey5);
      pageViews23.setProperty("restaurantName", "Mickey Finns Bar and Grill");
      pageViews23.setProperty("year", 2020);
      pageViews23.setProperty("week", 28);
      pageViews23.setProperty("count", 29);
    
      Entity pageViews24 = new Entity("PageViews");
      pageViews24.setProperty("restaurantKey", restaurantKey5);
      pageViews24.setProperty("restaurantName", "Mickey Finns Bar and Grill");
      pageViews24.setProperty("year", 2020);
      pageViews24.setProperty("week", 29);
      pageViews24.setProperty("count", 23);
    
      Entity pageViews25 = new Entity("PageViews");
      pageViews25.setProperty("restaurantKey", restaurantKey5);
      pageViews25.setProperty("restaurantName", "Mickey Finns Bar and Grill");
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
