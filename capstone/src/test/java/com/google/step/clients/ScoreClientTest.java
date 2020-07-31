package com.google.step.clients;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.step.data.RestaurantScore;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScoreClientTest {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    ScoreClient scoreClient = new ScoreClient();

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void checkScore() {
        // Test checks if the score is being calculated properly and gets the accurate result.

        Entity entity1 = new Entity("PageViews");
        entity1.setProperty("restaurantKey", "1");
        entity1.setProperty("year", 2015);
        entity1.setProperty("week", 4);
        entity1.setProperty("count", 10);

        Entity entity2 = new Entity("PageViews");
        entity2.setProperty("restaurantKey", "1");
        entity2.setProperty("year", 2015);
        entity2.setProperty("week", 5);
        entity2.setProperty("count", 20);

        Entity entity3 = new Entity("PageViews");
        entity3.setProperty("restaurantKey", "1");
        entity3.setProperty("year", 2015);
        entity3.setProperty("week", 6);
        entity3.setProperty("count", 30);

        Entity entity4 = new Entity("PageViews");
        entity4.setProperty("restaurantKey", "2");
        entity4.setProperty("year", 2015);
        entity4.setProperty("week", 4);
        entity4.setProperty("count", 40);

        Entity entity5 = new Entity("PageViews");
        entity5.setProperty("restaurantKey", "2");
        entity5.setProperty("year", 2015);
        entity5.setProperty("week", 5);
        entity5.setProperty("count", 50);

        datastoreService.put(Arrays.asList(
                entity1,
                entity2,
                entity3,
                entity4,
                entity5
        ));

        // Score for expected score calculated by hand.
        List<RestaurantScore> expected = new ArrayList<>();
        expected.add(new RestaurantScore((long) 1, 1.0d));
        expected.add(new RestaurantScore((long) 2, 0.0d));

        List<RestaurantScore> actual =  scoreClient.calculateScores();
        for (int i = 0; i < expected.size(); i++) {
            Assert.assertEquals("Restaurant", expected.get(i).getRestaurantKey(), actual.get(i).getRestaurantKey());
            Assert.assertEquals(expected.get(i).getScore(), actual.get(i).getScore(), 0.0002d);
        }
    }

}
