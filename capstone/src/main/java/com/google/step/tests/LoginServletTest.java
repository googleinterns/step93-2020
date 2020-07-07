package com.google.step.tests;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;



public class LoginServletTest {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    // Run this test twice to prove we're not leaking any state across tests
    public void checkIfRestaurantOrUserTest() {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        assertEquals(0, datastoreService.prepare(new Query("User")).countEntities(withLimit(10)));
        datastoreService.put(new Entity("User"));
        datastoreService.put(new Entity("User"));
        assertEquals(2, datastoreService.prepare(new Query("User")).countEntities(withLimit(10)));
    }

    @Test
    public void testInsert1() {
        checkIfRestaurantOrUserTest();
    }

    @Test
    public void testInsert2() {
        checkIfRestaurantOrUserTest();
    }

}
