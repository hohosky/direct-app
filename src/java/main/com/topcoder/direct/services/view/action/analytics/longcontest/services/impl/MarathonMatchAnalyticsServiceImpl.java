/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.direct.services.view.action.analytics.longcontest.services.impl;

import com.topcoder.commons.utils.LoggingWrapperUtility;
import com.topcoder.commons.utils.ValidationUtility;
import com.topcoder.direct.services.view.action.analytics.longcontest.services.MarathonMatchAnalyticsService;
import com.topcoder.direct.services.view.action.analytics.longcontest.services.MarathonMatchAnalyticsServiceException;
import com.topcoder.util.log.Log;
import com.topcoder.util.log.LogManager;
import com.topcoder.web.tc.rest.longcontest.resources.CompetitorResource;
import com.topcoder.web.tc.rest.longcontest.resources.MarathonMatchDetailsResource;
import com.topcoder.web.tc.rest.longcontest.resources.MarathonMatchItemResource;
import com.topcoder.web.tc.rest.longcontest.resources.MatchResultResource;
import com.topcoder.web.tc.rest.longcontest.resources.SearchResult;
import com.topcoder.web.tc.rest.longcontest.resources.SubmissionResource;
import com.topcoder.web.tc.rest.longcontest.resources.SystemTestResourceWrapper;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.rs.security.oauth2.client.OAuthClientUtils;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * The implementation of MarathonMatchAnalyticsService interface, it will used to call the remote rest service.
 * This class contains the method to get marathon match details, get registrants for a marathon match contest and
 * get competitor submission history.
 *
 * <p>
 * <strong>Thread Safety: </strong> This class is mutable and thus is not thread
 * safe.
 * </p>
 *
 * <p>
 *     Version 1.1 - Release Assembly - TopCoder Cockpit - Tracking Marathon Matches Progress - Competitors Tab
 *     <ol>
 *         <li>Add method {@link #getCompetitorSubmissionsHistory(long, String, int, int, String, String, String)}.</li>
 *         <li>Add static property {@link #HANDLE}</li>
 *     </ol>
 * </p>
 *
 * <p>
 *     Version 1.2 - Release Assembly - TopCoder Cockpit - Tracking Marathon Matches Progress -
 *                   Dashboard and Submissions Tab
 *     <ol>
 *         <li>
 *             Add method {@link #getMarathonMatchListings(String, String, String, int, int, String, String, String)}
 *         </li>
 *         <li>
 *             Add method {@link #getMatchResults(long, int, int, String, String, String)}.
 *         </li>
 *         <li>
 *             Fix unhandled exception in {@link #getMarathonMatchDetails(long, String, String)} and
 *             {@link #getRegistrants(long, String)}
 *         </li>
 *     </ol>
 * </p>
 *
 * <p>
 *     Version 1.3 - Release Assembly - TopCoder Cockpit - Tracking Marathon Matches Progress - Results Tab 2
 *     <ol>
 *         <li>Add method {@link #getSystemTests(long, Long, int, int, String, String, String)}.</li>
 *         <li>Add method {@link #getSystemTestsResultsEndPointUrl}.</li>
 *     </ol>
 * </p>
 *
 * @author Ghost_141
 * @since 1.0 (PoC Assembly - TopCoder Cockpit - Tracking Marathon Matches Progress)
 * @version 1.3
 */
public class MarathonMatchAnalyticsServiceImpl implements MarathonMatchAnalyticsService {

    /**
     * This field represents the qualified name of this class.
     */
    private static final String CLASS_NAME = MarathonMatchAnalyticsServiceImpl.class.getName();

    /**
     * The default value of page number.
     * It will be used in rest api.
     */
    private static final String DEFAULT_PAGE_NUMBER = "1";

    /**
     * The default value of page size.
     * It will be used in rest api.
     */
    private static final String DEFAULT_PAGE_SIZE = String.valueOf(Integer.MAX_VALUE);

    /**
     * The default value of sorting order.
     * It will be used in rest api.
     */
    private static final String DEFAULT_SORTING_ORDER = "desc";

    /**
     * The round id in rest path parameter.
     */
    private static final String ROUND_ID = "{round_id}";

    /**
     * The handle in rest path parameter.
     * @since 1.1
     */
    private static final String HANDLE = "{handle}";

    /**
     * Log instance.
     */
    private Log logger = LogManager.getLog(CLASS_NAME);

    /**
     * Represent the base url of the remote rest service.
     */
    private String remoteServiceBaseUrl;

    /**
     * The Get mM basic info end point url.
     */
    private String getMMBasicInfoEndPointUrl;

    /**
     * The Get marathon match end point listing url.
     */
    private String getMarathonMatchListingsEndPointUrl;

    /**
     * The Get mM registrants end point url.
     */
    private String getMMRegistrantsEndPointUrl;

    /**
     * The Get all mM submissions end point url.
     */
    private String getAllMMSubmissionsEndPointUrl;

    /**
     * The Get match results end point url.
     */
    private String getMatchResultsEndPointUrl;

    /**
     * The Get competitor details end point url.
     */
    private String getCompetitorDetailsEndPointUrl;

    /**
     * The url for getSystemTests api.
     * @since 1.3
     */
    private String getSystemTestsResultsEndPointUrl;

    /**
     * The data format used to format date string from rest response.
     */
    private DateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm z");

    /**
     * Represent the client id.
     */
    private String clientId;

    /**
     * Represent the client secret.
     */
    private String clientSecret;

    /**
     * Represent the api key of marathon match rest api.
     */
    private String apiKey;

    /**
     * Check the initialization of bean instance.
     */
    public void checkInit() {
        final String signature = CLASS_NAME + "#checkInit()";
        try {
            getMMBasicInfoEndPointUrl = fixUrl("getMMBasicInfoEndPointUrl", getMMBasicInfoEndPointUrl);
            getAllMMSubmissionsEndPointUrl = fixUrl("getAllMMSubmissionsEndPointUrl", getAllMMSubmissionsEndPointUrl);
            getMMRegistrantsEndPointUrl = fixUrl("getMMRegistrantsEndPointUrl", getMMRegistrantsEndPointUrl);
            getMatchResultsEndPointUrl = fixUrl("getMatchResultsEndPointUrl", getMatchResultsEndPointUrl);
            getCompetitorDetailsEndPointUrl =
                    fixUrl("getCompetitorDetailsEndPointUrl", getCompetitorDetailsEndPointUrl);
            getSystemTestsResultsEndPointUrl =
                    fixUrl("getSystemTestsResultsEndPointUrl", getSystemTestsResultsEndPointUrl);
            remoteServiceBaseUrl = fixUrl("remoteServiceBaseUrl", remoteServiceBaseUrl);

            ValidationUtility.checkNotNullNorEmptyAfterTrimming(clientId, "clientId", IllegalArgumentException.class);
            ValidationUtility.checkNotNullNorEmptyAfterTrimming(clientSecret,
                    "clientSecret", IllegalArgumentException.class);
            ValidationUtility.checkNotNullNorEmptyAfterTrimming(apiKey, "apiKey", IllegalArgumentException.class);
        } catch (IllegalArgumentException iae) {
            throw LoggingWrapperUtility.logException(logger, signature, iae);
        }
    }

    /**
     * Check the url whether start with '/'. If it's not fix it.
     * If the url is fine, just return them back.
     *
     * @param urlName the name of the url.
     * @param value the url value.
     * @return the fixed value.
     */
    private String fixUrl(String urlName, String value) {
        if(value == null || value.trim().length() == 0) {
            throw new IllegalArgumentException("The " + urlName + " should not be null nor empty");
        }
        value = value.trim();
        if(!value.startsWith("/") && !value.startsWith("http")) {
            value = "/" + value.trim();
        }
        return value;
    }

    /**
     * Get the marathon match details by given round id and group type.
     *
     * @param groupType the group type.
     * @param accessToken the access token.
     * @param roundId the round id.
     * @return the instance of MarathonMatchDetailsResource.
     * @throws MarathonMatchAnalyticsServiceException if any error occurred.
     */
    public MarathonMatchDetailsResource getMarathonMatchDetails(long roundId, String groupType, String accessToken)
            throws MarathonMatchAnalyticsServiceException{
        final String signature = CLASS_NAME + "#getMarathonMatchDetails(roundId, groupType, accessToken)";
        try {
            LoggingWrapperUtility.logEntrance(logger, signature, new String[] {"roundId", "groupType", "accessToken"},
                    new Object[] {roundId, groupType, accessToken});

            ValidationUtility.checkPositive(roundId, "roundId", IllegalArgumentException.class);
            ValidationUtility.checkNotNullNorEmptyAfterTrimming(groupType, "groupType", IllegalArgumentException.class);
            ValidationUtility.checkNotNullNorEmptyAfterTrimming(accessToken, "accessToken",
                    IllegalArgumentException.class);

            String jsonResult = callRestService(getMMBasicInfoEndPointUrl,
                    createParameterMap(new String[] {"groupType"}, new String[] {groupType}),
                    createParameterMap(new String[] {ROUND_ID}, new String[] {String.valueOf(roundId)}),
                    accessToken);

            ObjectMapper mapper = createObjectMapper();

            MarathonMatchDetailsResource result =
                    mapper.readValue(jsonResult.getBytes(), new TypeReference<MarathonMatchDetailsResource>() {});

            LoggingWrapperUtility.logExit(logger, signature, new Object[] {result});

            return result;
        } catch (IllegalArgumentException iae) {
            throw LoggingWrapperUtility.logException(logger, signature,
                    new MarathonMatchAnalyticsServiceException("The parameter is invalid.", iae));
        } catch (MarathonMatchAnalyticsServiceException e) {
            LoggingWrapperUtility.logException(logger, signature, e);
            throw e;
        } catch (IOException ioe) {
            LoggingWrapperUtility.logException(logger, signature, ioe);
            throw new MarathonMatchAnalyticsServiceException("Error occurred when process json string.", ioe);
        }
    }

    /**
     * Get the registrants data by calling rest service.
     *
     * @param roundId the round id.
     * @param accessToken the access token.
     * @return the registrants data.
     * @throws MarathonMatchAnalyticsServiceException if any error occurred.
     */
    public SearchResult<CompetitorResource> getRegistrants(long roundId,
            String accessToken) throws MarathonMatchAnalyticsServiceException {
        final String signature = CLASS_NAME
                + "#getRegistrants(roundId, accessToken)";
        try {
            LoggingWrapperUtility.logEntrance(logger, signature,
                    new String[] {"roundId", "accessToken"},
                    new Object[] {roundId, accessToken});

            ValidationUtility.checkPositive(roundId, "roundId", IllegalArgumentException.class);
            ValidationUtility.checkNotNullNorEmptyAfterTrimming(accessToken, "accessToken",
                    IllegalArgumentException.class);

            String jsonResult = callRestService(getMMRegistrantsEndPointUrl,
                    createParameterMap(new String[] {"pageSize", "pageNumber", "sortingOrder", "sortingField"},
                            new String[] {DEFAULT_PAGE_SIZE, DEFAULT_PAGE_NUMBER, DEFAULT_SORTING_ORDER, "rating"}),
                    createParameterMap(new String[] {ROUND_ID}, new String[] {String.valueOf(roundId)}),
                    accessToken);

            ObjectMapper mapper = createObjectMapper();

            SearchResult<CompetitorResource> result =
                    mapper.readValue(jsonResult.getBytes(), new TypeReference<SearchResult<CompetitorResource>>() {});

            LoggingWrapperUtility.logExit(logger, signature, new Object[] {result});

            return result;
        } catch (IllegalArgumentException iae) {
            throw LoggingWrapperUtility.logException(logger, signature,
                    new MarathonMatchAnalyticsServiceException("The parameter is invalid.", iae));
        } catch (MarathonMatchAnalyticsServiceException e) {
            LoggingWrapperUtility.logException(logger, signature, e);
            throw e;
        } catch (IOException e) {
            LoggingWrapperUtility.logException(logger, signature, e);
            throw new MarathonMatchAnalyticsServiceException("Error occurred when process json string.", e);
        }
    }

    /**
     * Get the submission history of one competitor with given round id, handle name of competitor, page size, page
     * number, sorting order, sorting field.
     *
     * @param sortingOrder the sorting order.
     * @param handle the handle name.
     * @param accessToken the access token.
     * @param sortingField the sorting field.
     * @param pageSize the page size.
     * @param pageNumber the page number.
     * @param roundId the round id.
     * @return the search result.
     * @throws MarathonMatchAnalyticsServiceException if any error occurred.
     * @since 1.1
     */
    public SearchResult<SubmissionResource> getCompetitorSubmissionsHistory(long roundId, String handle, int pageSize,
            int pageNumber, String sortingOrder, String sortingField, String accessToken)
            throws MarathonMatchAnalyticsServiceException {
        final String signature = CLASS_NAME +
                "#getCompetitorSubmissionsHistory(long roundId, String handle, int pageSize, int pageNumber, " +
                "String sortingOrder, String sortingField, String accessToken)";
        try {
            LoggingWrapperUtility.logEntrance(logger, signature,
                    new String[] {"roundId", "handle", "pageSize", "pageNumber", "sortingOrder", "sortingField",
                            "accessToken"},
                    new Object[] {roundId, handle, pageSize, pageNumber, sortingOrder, sortingField, accessToken});
            ValidationUtility.checkPositive(roundId, "roundId", IllegalArgumentException.class);
            ValidationUtility.checkNotNullNorEmptyAfterTrimming(handle, "handle", IllegalArgumentException.class);
            ValidationUtility.checkPositive(pageSize, "pageSize", IllegalArgumentException.class);
            ValidationUtility.checkPositive(pageNumber, "pageNumber", IllegalArgumentException.class);
            ValidationUtility.checkNotNullNorEmptyAfterTrimming(accessToken, "accessToken",
                    IllegalArgumentException.class);

            String jsonResult = callRestService(getAllMMSubmissionsEndPointUrl,
                    createParameterMap(new String[] {"pageSize", "pageNumber", "sortingOrder", "sortingField"},
                            new String[] {String.valueOf(pageSize), String.valueOf(pageNumber), sortingOrder,
                                    sortingField}),
                    createParameterMap(new String[] {ROUND_ID, HANDLE}, new String[] {String.valueOf(roundId), handle}),
                    accessToken);

            ObjectMapper mapper = createObjectMapper();

            SearchResult<SubmissionResource> result =
                    mapper.readValue(jsonResult.getBytes(), new TypeReference<SearchResult<SubmissionResource>>() {});

            LoggingWrapperUtility.logExit(logger, signature, new Object[] {result});
            return result;
        } catch (IllegalArgumentException iae) {
            throw LoggingWrapperUtility.logException(logger, signature,
                    new MarathonMatchAnalyticsServiceException("The parameter is invalid.", iae));
        } catch (IOException e) {
            throw LoggingWrapperUtility.logException(logger, signature,
                    new MarathonMatchAnalyticsServiceException("Error occurred when process json string.", e));
        }
    }

    /**
     * Get the marathon match lists from the TC platform api.
     *
     * @param type the contest type. It can only be 'active', 'past'. Can't be null.
     * @param date the date of matches. Can be null.
     * @param projectId the project that marathon match belong to. Can be null.
     * @param pageSize the page size. Can be null.
     * @param pageNumber the page number. Can be null.
     * @param sortingOrder the sort order of result. It can only be 'desc' and 'asc'. Can be null.
     * @param sortingField the sort field of result. It can only be roundId,fullName,shortName,startDate,endDate,
     *                     winnerHandle,winnerScore,currentlyTopRankingSubmissionHandle,currentlyTopProvisionalScore.
     *                     Can be null.
     * @param accessToken the access token used in rest call.
     * @return the result of marathon match list.
     * @throws MarathonMatchAnalyticsServiceException if any error occurred.
     * @since 1.2
     */
    public SearchResult<MarathonMatchItemResource> getMarathonMatchListings(String type, String date, String projectId,
            int pageSize, int pageNumber, String sortingOrder, String sortingField, String accessToken)
            throws MarathonMatchAnalyticsServiceException {
        final String signature = CLASS_NAME + "#getMarathonMatchListings(type, date, project, pageSize, pageNumber, " +
                "sortingOrder, sortingField, accessToken)";
        try {
            LoggingWrapperUtility.logEntrance(logger, signature,
                    new String[]{"type", "date", "projectId", "pageSize", "pageNumber", "sortingOrder", "sortingField",
                            "accessToken"},
                    new Object[]{type, date, projectId, pageSize, pageNumber, sortingOrder, sortingField, accessToken});
            ValidationUtility.checkNotNullNorEmptyAfterTrimming(type, "type", IllegalArgumentException.class);
            ValidationUtility.checkPositive(pageSize, "pageSize", IllegalArgumentException.class);
            ValidationUtility.checkPositive(pageNumber, "pageNumber", IllegalArgumentException.class);
            ValidationUtility.checkNotNullNorEmptyAfterTrimming(accessToken, "accessToken",
                    IllegalArgumentException.class);

            String jsonResult = callRestService(getMarathonMatchListingsEndPointUrl,
                    createParameterMap(new String[] {"type", "date", "project", "pageSize", "pageNumber",
                            "sortingOrder", "sortingField"},
                            new String[] {type, date, projectId, String.valueOf(pageSize), String.valueOf(pageNumber),
                                    sortingOrder, sortingField}),
                    createParameterMap(null, null),
                    accessToken);
            ObjectMapper mapper = createObjectMapper();

            SearchResult<MarathonMatchItemResource> result =
                    mapper.readValue(jsonResult.getBytes(),
                            new TypeReference<SearchResult<MarathonMatchItemResource>>() {
                            });

            LoggingWrapperUtility.logExit(logger, signature, new Object[] {result});

            return result;
        } catch (IllegalArgumentException iae) {
            throw LoggingWrapperUtility.logException(logger, signature,
                    new MarathonMatchAnalyticsServiceException("The parameter is invalid.", iae));
        } catch (MarathonMatchAnalyticsServiceException e) {
            LoggingWrapperUtility.logException(logger, signature, e);
            throw e;
        } catch (IOException e) {
            throw LoggingWrapperUtility.logException(logger, signature,
                    new MarathonMatchAnalyticsServiceException("Error occurred when process json string.", e));
        }
    }

    /**
     * Get the marathon match result from platform api.
     *
     * @param roundId the round id of this marathon match contest.
     * @param pageSize the page size of result.
     * @param pageNumber the page number of result.
     * @param sortingOrder the sorting order of result. Can be null. It can only be 'desc' and 'asc'.
     * @param sortingField the sorting field of result. Can be null. It can only be 'rank', 'handleName', 'coderId',
     *                     'coderRating', 'finalScore', 'provisionalScore'.
     * @param accessToken the access token.
     * @return the result
     * @throws MarathonMatchAnalyticsServiceException if any error occurred.
     * @since 1.2
     */
    public SearchResult<MatchResultResource> getMatchResults(long roundId, int pageSize, int pageNumber,
            String sortingOrder, String sortingField, String accessToken)
            throws MarathonMatchAnalyticsServiceException {
        final String signature = CLASS_NAME + "#getMatchResults(roundId, pageSize, pageNumber, sortingOrder, " +
                "sortingField, accessToken)";
        try {
            LoggingWrapperUtility.logEntrance(logger, signature,
                    new String[] {"roundId", "pageSize", "pageNumber", "sortingOrder", "sortingField", "accessToken"},
                    new Object[] {roundId, pageSize, pageNumber, sortingOrder, sortingField, accessToken});
            ValidationUtility.checkPositive(roundId, "roundId", IllegalArgumentException.class);
            ValidationUtility.checkPositive(pageSize, "pageSize", IllegalArgumentException.class);
            ValidationUtility.checkPositive(pageNumber, "pageNumber", IllegalArgumentException.class);
            ValidationUtility.checkNotNullNorEmptyAfterTrimming(accessToken, "accessToken",
                    IllegalArgumentException.class);

            String jsonResult = callRestService(getMatchResultsEndPointUrl,
                    createParameterMap(new String[] {"pageSize", "pageNumber", "sortingOrder", "sortingField"},
                            new String[] {String.valueOf(pageSize), String.valueOf(pageNumber), sortingOrder,
                                    sortingField}),
                    createParameterMap(new String[] {ROUND_ID}, new String[] {String.valueOf(roundId)}),
                    accessToken);

            ObjectMapper mapper = createObjectMapper();

            SearchResult<MatchResultResource> result =
                    mapper.readValue(jsonResult.getBytes(), new TypeReference<SearchResult<MatchResultResource>>() {});

            LoggingWrapperUtility.logExit(logger, signature, new Object[] {result});

            return result;
        } catch (IllegalArgumentException iae) {
            throw LoggingWrapperUtility.logException(logger, signature,
                    new MarathonMatchAnalyticsServiceException("The parameter is invalid.", iae));
        } catch (MarathonMatchAnalyticsServiceException e) {
            LoggingWrapperUtility.logException(logger, signature, e);
            throw e;
        } catch (IOException e) {
            throw LoggingWrapperUtility.logException(logger, signature,
                    new MarathonMatchAnalyticsServiceException("Error occurred when process json string.", e));
        }
    }

    /**
     * Get system tests results of specified marathon match.
     *
     * @param roundId the round id of this marathon match contest.
     * @param coderId the coder id of a specified coder.
     * @param coderStartNumber the coder start number.
     * @param testCaseStartNumber the test case start number.
     * @param sortingOrder the sort order. 'asc', 'desc' or null only.
     * @param sortingField the sort field. could be null.
     * @param accessToken the access token.
     * @return the search result of system test resource wrapper.
     * @since 1.3
     */
    public SystemTestResourceWrapper getSystemTests(long roundId, Long coderId, int coderStartNumber,
            int testCaseStartNumber, String sortingField, String sortingOrder, String accessToken)
            throws MarathonMatchAnalyticsServiceException {
        final String signature = CLASS_NAME + "#getSystemTests(long roundId, long coderId, int coderStartNumber, " +
                "int testCaseStartNumber, String sortingOrder, String sortingField, String accessToken)";
        try {
            LoggingWrapperUtility.logEntrance(logger, signature,
                    new String[] {"roundId", "coderId", "coderStartNumber", "testCaseStartNumber", "sortingOrder",
                            "sortingField", "accessToken"},
                    new Object[] {roundId, coderId, coderStartNumber, testCaseStartNumber, sortingOrder, sortingField,
                            accessToken});
            ValidationUtility.checkPositive(roundId, "roundId", IllegalArgumentException.class);
            if(coderId != null) {
                ValidationUtility.checkPositive(coderId, "coderId", IllegalArgumentException.class);
            }
            ValidationUtility.checkPositive(coderStartNumber, "coderStartNumber", IllegalArgumentException.class);
            ValidationUtility.checkPositive(testCaseStartNumber, "testCaseStartNumber", IllegalArgumentException.class);
            ValidationUtility.checkNotNullNorEmptyAfterTrimming(accessToken, "accessToken",
                    IllegalArgumentException.class);

            String jsonResult = callRestService(getSystemTestsResultsEndPointUrl,
                    createParameterMap(new String[] {"coderId", "coderStartNumber", "testCaseStartNumber",
                            "sortingOrder", "sortingField"},
                            new String[] {coderId == null ? null : coderId.toString(), String.valueOf(coderStartNumber),
                                    String.valueOf(testCaseStartNumber), sortingOrder, sortingField}),
                    createParameterMap(new String[] {ROUND_ID}, new String[] {String.valueOf(roundId)}),
                    accessToken);
            ObjectMapper mapper = createObjectMapper();

            SystemTestResourceWrapper result =
                    mapper.readValue(jsonResult.getBytes(), new TypeReference<SystemTestResourceWrapper>() {});

            LoggingWrapperUtility.logExit(logger, signature, new Object[] {result});

            return result;
        } catch (IllegalArgumentException iae) {
            throw LoggingWrapperUtility.logException(logger, signature,
                    new MarathonMatchAnalyticsServiceException("The parameter is invalid.", iae));
        } catch (IOException e) {
            throw LoggingWrapperUtility.logException(logger, signature,
                    new MarathonMatchAnalyticsServiceException("Error occurred when process json string.", e));
        }
    }

    /**
     * Build a json node based on an existing response.
     *
     * @param url the url.
     * @param queryParams the map of query parameters.
     * @param pathParams the map of path parameters.
     * @param accessToken the access token.
     * @return the json node of the response.
     * @throws IOException if any error occurs when reading data.
     * @throws MarathonMatchAnalyticsServiceException if the rest call is failed.
     */
    private String callRestService(String url, Map<String, String> queryParams, Map<String, String> pathParams,
            String accessToken) throws IOException, MarathonMatchAnalyticsServiceException {
        WebClient client = getWebClient();

        if(queryParams != null) {
            for(String key : queryParams.keySet()) {
                if(queryParams.get(key) != null) {
                    client.query(key, queryParams.get(key));
                }
            }
        }

        // Set the api key for the rest call.
        client.query("user_key", apiKey);

        if(pathParams != null) {
            for(String key : pathParams.keySet()) {
                url = url.replace(key, pathParams.get(key));
            }
        }

        OAuthClientUtils.Consumer consumer = new OAuthClientUtils.Consumer(clientId, clientSecret);
        ClientAccessToken clientAccessToken = new ClientAccessToken(OAuthConstants.BEARER_TOKEN_TYPE, accessToken);
        String authHeader = OAuthClientUtils.createAuthorizationHeader(consumer, clientAccessToken);
        client.replaceHeader("Authorization", authHeader);

        Response response = client.path(url).get();

        if (response.getStatus() != 200) {
            throw new MarathonMatchAnalyticsServiceException("The rest call is failed :" + response.getStatus());
        } else {
            return IOUtils.toString((InputStream) response.getEntity(), "UTF-8");
        }
    }

    /**
     * Create the map by given input keys and values.
     *
     * @param key the keys
     * @param value the values.
     * @return the map.
     */
    private Map<String, String> createParameterMap(String[] key, String[] value) {
        // Create a null object.
        if(key == null && value == null) {
            return null;
        }
        Map<String, String> map = new HashMap<String, String>();
        for(int i = 0; i < key.length; i++) {
            map.put(key[i], value[i]);
        }
        return map;
    }

    /**
     * Gets the <code>WebClient</code> instance to make call to remote service.
     *
     * @return the <code>WebClient</code> instance
     */
    private WebClient getWebClient() {
        return WebClient.create(getRemoteServiceBaseUrl());
    }

    /**
     * Create the object mapper.
     *
     * @return the object mapper instance.
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setDateFormat(dateFormat);
        return mapper;
    }

    /**
     * Gets remote service base url.
     *
     * @return the remote service base url
     */
    public String getRemoteServiceBaseUrl() {
        return remoteServiceBaseUrl;
    }

    /**
     * Sets remote service base url.
     *
     * @param remoteServiceBaseUrl the remote service base url
     */
    public void setRemoteServiceBaseUrl(String remoteServiceBaseUrl) {
        this.remoteServiceBaseUrl = remoteServiceBaseUrl;
    }

    /**
     * Gets get mM basic info end point url.
     *
     * @return the get mM basic info end point url
     */
    public String getGetMMBasicInfoEndPointUrl() {
        return getMMBasicInfoEndPointUrl;
    }

    /**
     * Sets get mM basic info end point url.
     *
     * @param getMMBasicInfoEndPointUrl the get mM basic info end point url
     */
    public void setGetMMBasicInfoEndPointUrl(String getMMBasicInfoEndPointUrl) {
        this.getMMBasicInfoEndPointUrl = getMMBasicInfoEndPointUrl;
    }

    /**
     * Gets get mM registrants end point url.
     *
     * @return the get mM registrants end point url
     */
    public String getGetMMRegistrantsEndPointUrl() {
        return getMMRegistrantsEndPointUrl;
    }

    /**
     * Sets get mM registrants end point url.
     *
     * @param getMMRegistrantsEndPointUrl the get mM registrants end point url
     */
    public void setGetMMRegistrantsEndPointUrl(String getMMRegistrantsEndPointUrl) {
        this.getMMRegistrantsEndPointUrl = getMMRegistrantsEndPointUrl;
    }

    /**
     * Gets get all mM submissions end point url.
     *
     * @return the get all mM submissions end point url
     */
    public String getGetAllMMSubmissionsEndPointUrl() {
        return getAllMMSubmissionsEndPointUrl;
    }

    /**
     * Sets get all mM submissions end point url.
     *
     * @param getAllMMSubmissionsEndPointUrl the get all mM submissions end point url
     */
    public void setGetAllMMSubmissionsEndPointUrl(String getAllMMSubmissionsEndPointUrl) {
        this.getAllMMSubmissionsEndPointUrl = getAllMMSubmissionsEndPointUrl;
    }

    /**
     * Gets get match results end point url.
     *
     * @return the get match results end point url
     */
    public String getGetMatchResultsEndPointUrl() {
        return getMatchResultsEndPointUrl;
    }

    /**
     * Sets get match results end point url.
     *
     * @param getMatchResultsEndPointUrl the get match results end point url
     */
    public void setGetMatchResultsEndPointUrl(String getMatchResultsEndPointUrl) {
        this.getMatchResultsEndPointUrl = getMatchResultsEndPointUrl;
    }

    /**
     * Gets get competitor details end point url.
     *
     * @return the get competitor details end point url
     */
    public String getGetCompetitorDetailsEndPointUrl() {
        return getCompetitorDetailsEndPointUrl;
    }

    /**
     * Sets get competitor details end point url.
     *
     * @param getCompetitorDetailsEndPointUrl the get competitor details end point url
     */
    public void setGetCompetitorDetailsEndPointUrl(String getCompetitorDetailsEndPointUrl) {
        this.getCompetitorDetailsEndPointUrl = getCompetitorDetailsEndPointUrl;
    }

    /**
     * Gets get marathon match listings end point url.
     *
     * @return the get marathon match listings end point url
     */
    public String getGetMarathonMatchListingsEndPointUrl() {
        return getMarathonMatchListingsEndPointUrl;
    }

    /**
     * Sets get marathon match listings end point url.
     *
     * @param getMarathonMatchListingsEndPointUrl the get marathon match listings end point url
     */
    public void setGetMarathonMatchListingsEndPointUrl(String getMarathonMatchListingsEndPointUrl) {
        this.getMarathonMatchListingsEndPointUrl = getMarathonMatchListingsEndPointUrl;
    }

    /**
     * Sets client id.
     *
     * @param clientId the client id
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Sets client secret.
     *
     * @param clientSecret the client secret
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     * Sets api key.
     *
     * @param apiKey the api key
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Gets get system tests results end point url.
     *
     * @return the get system tests results end point url
     * @since 1.3
     */
    public String getGetSystemTestsResultsEndPointUrl() {
        return getSystemTestsResultsEndPointUrl;
    }

    /**
     * Sets get system tests results end point url.
     *
     * @param getSystemTestsResultsEndPointUrl the get system tests results end point url
     * @since 1.3
     */
    public void setGetSystemTestsResultsEndPointUrl(String getSystemTestsResultsEndPointUrl) {
        this.getSystemTestsResultsEndPointUrl = getSystemTestsResultsEndPointUrl;
    }
}

