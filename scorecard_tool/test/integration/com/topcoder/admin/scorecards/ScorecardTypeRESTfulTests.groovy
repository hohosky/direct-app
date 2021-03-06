/*
* Copyright (C) 2011 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.admin.scorecards

import grails.test.*
import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient


/**
* This class is unit test class for the RESTful service of <code>ScorecardType</code>
* domain.
*
* @author TCSASSEMBER
* @version 1.0
*/
class ScorecardTypeRESTfulTests extends GroovyTestCase {
    /**
    * Set up the environment.
    */
    protected void setUp() {
        super.setUp()
    }

    /**
    * Tear down the environment.
    */
    protected void tearDown() {
        super.tearDown()
    }

    /**
    * Test the RESTful functionality.
    */
    void testRESTfulCURD() {
        def client = new RESTClient("http://localhost:8080/scorecard_tool/json/")
        def response
        
        // POST, create a new instance
        response = client.post(
            path: "scorecard-type",
            body: [ data: [ name: "test scorecard type", description: "test scorecard type", dateCreated: new Date(), lastUpdated: new Date() ] ],
            requestContentType : ContentType.JSON.toString()
        )
        assertTrue response.data.success
        assertEquals "test scorecard type", response.data.data.name
        assertEquals "test scorecard type", response.data.data.description
        
        def id = response.data.data.id
        
        // GET (list)
        response = client.get(path: "scorecard-type")
        assertTrue response.data.success
        
        // GET (single item)
        response = client.get(path: "scorecard-type/${id}")
        assertTrue response.data.success
        assertEquals "test scorecard type", response.data.data.name
        assertEquals "test scorecard type", response.data.data.description
        
        // PUT
        response = client.put(
            path: "scorecard-type/${id}",
            body: [ data: [ name: "test scorecard type2", description: "test scorecard type2", dateCreated: new Date(), lastUpdated: new Date() ] ],
            requestContentType : ContentType.JSON.toString()
        )
        assertTrue response.data.success
        assertEquals "test scorecard type2", response.data.data.name
        assertEquals "test scorecard type2", response.data.data.description
        
        // DELETE
        response = client.delete(path: "scorecard-type/${id}")
        assertTrue response.data.success
        assertEquals "test scorecard type2", response.data.data.name
        assertEquals "test scorecard type2", response.data.data.description
        
        // verify the item is deleted
        try {
            response = client.get(path: "scorecard-type/${id}")
            fail "the item should be deleted"
        } catch (HttpResponseException e) {
            assert e.response.status == 404
            assert e.response.data.success == false
            assert e.response.data.message.contains("not found")
        }
    }
    
    /**
    * Error will occurs when saving the instance.
    */
    void testRESTfulSavingFail() {
        def client = new RESTClient("http://localhost:8080/scorecard_tool/json/")
        try {
            client.post(
                path: "scorecard-type",
                body: [ data: [ : ] ],
                requestContentType : ContentType.JSON.toString()
            )
            fail "Should throw HttpResponseException"
        } catch (HttpResponseException e) {
            assert e.response.status == 500
            assert e.response.data.success == false
            assert e.response.data.message.contains("null")
        }
    }
}
