// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.step.clients;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import static org.junit.Assert.*;

import com.google.appengine.repackaged.com.google.common.geometry.S2;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;




import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class MetricsClientTests {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    private final MetricsClient metricsClient = new MetricsClient();

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    public void checkIfDataStoreWorks() {

        assertEquals(0, datastoreService.prepare(new Query("User")).countEntities(withLimit(10)));
        datastoreService.put(new Entity("User"));
        datastoreService.put(new Entity("User"));
        assertEquals(2, datastoreService.prepare(new Query("User")).countEntities(withLimit(10)));
    }

    @Test
    public void testCheckType1() {
        checkIfDataStoreWorks();
    }

    @Test
    public void testCheckType2() {
        checkIfDataStoreWorks();
    }

    @Test
    public void testSeveralLikes() {
        Entity entity1 = new Entity("Likes");
        entity1.setProperty("restaurantKey", "1");
        entity1.setProperty("count", 20);

        Entity entity2 = new Entity("Likes");
        entity2.setProperty("restaurantKey", "2");
        entity2.setProperty("count", 10);

        datastoreService.put(entity1);
        datastoreService.put(entity2);

        int result1 = metricsClient.getRestaurantLikes("1");
        int expected1 = Integer.parseInt(entity1.getProperty("count").toString());
        Assert.assertEquals("Count", expected1, result1);

        int result2 = metricsClient.getRestaurantLikes("2");
        int expected2 = Integer.parseInt(entity2.getProperty("count").toString());
        Assert.assertEquals("Count", expected2, result2);
    }



}
