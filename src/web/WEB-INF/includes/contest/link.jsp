<%@ page import="com.topcoder.direct.services.configs.ServerConfiguration" %>
<%--
  - Author: Ghost_141, GreatKevin
  - Version: 1.4
  - Copyright (C) 2011 - 2014 TopCoder Inc., All Rights Reserved.
  -
  - Description: Contest links area for Contest Details page
  -
  - Version 1.1 (TC Direct "Contest Links and Button" Update 24Hr Assembly) changes:
  - 1. Change the  style for the links. 
  - 2. Add the 'View Contest', 'Repost Contest', 'New Version' links.
  -
  - Version 1.2 (Release Assembly - TopCoder Cockpit - Tracking Marathon Matches Progress - Results Tab 2) changes:
  - 1. Update 'Preview Contest' and 'Registration Page' link when the contest is marathon match contest.
  -
  - Version 1.3 (BUGR - 9796) changes:
  - 1. Add link for update/set round id.
  -
   - Version 1.4 (Release Assembly - Port Design Challenge Forum to use Dev Forum)
  - - Update forum link for studio contest
--%>
<%@ include file="/WEB-INF/includes/taglibs.jsp" %>
<div class="contestLinks">

    <ul class="links">
		<li>
        <a href="javascript:;" id="repostButton" class="contestLinkIcon linkIconRepostContest" style="text-shadow: 0px 1px 1px rgb(255, 255, 255); display:none;">Repost</a>
        </li>
        <li class="splitter" id="repostButtonSplitter" style="display:none;"></li>

        <s:if test="viewData.contestStats.softwareContestCompleted">
            <li>
                <a href="javascript:;" id="newVersionButton" class="contestLinkIcon linkIconNewVersion"
                   style="text-shadow: 0px 1px 1px rgb(255, 255, 255)">New Version</a>
            </li>
            <li class="splitter" id="newVersionButtonSplitter"></li>
        </s:if>

        

        <s:if test="viewData.contestStats.isStudio">
            <s:set name="contestLink" value="'http://' + #application['SERVER_CONFIG_NEW_SERVER_NAME'] + '/challenge-details/' + viewData.contestStats.contest.id + '/?type=design&noncache=true'"/>
        </s:if>
        <s:elseif test="marathon">
            <s:if test="viewData.active">
                <s:set name="contestLink" value="'http://' + #application['SERVER_CONFIG_SERVER_NAME'] + '/tc?module=MatchDetails&rd=' + viewData.roundId"/>
            </s:if>
            <s:else>
                <s:set name="contestLink" value="'http://' + #application['SERVER_CONFIG_SERVER_NAME'] + '/tc?module=MatchDetails&rd=' + viewData.roundId"/>
            </s:else>
        </s:elseif>
        <s:else>
           <s:set name="contestLink" value="'http://' + #application['SERVER_CONFIG_NEW_SERVER_NAME'] + '/challenge-details/' + viewData.contestStats.contest.id + '/?type=develop&noncache=true'"/>
        </s:else>

        <s:if test="marathon">
            <s:if test="viewData.active">
                <li id="previewContestButton">
                    <a href="<s:property value='#contestLink'/>"  target="_blank" class="contestLinkIcon linkIconPreviewContest"
                       style="text-shadow: 0px 1px 1px rgb(255, 255, 255);">Preview Challenge</a>
                </li>
            </s:if>
            <s:else>
                <li id="viewContestButton">
                    <a href="<s:property value='#contestLink'/>" target="_blank" class="contestLinkIcon linkIconViewContest"
                       style="text-shadow: 0px 1px 1px rgb(255, 255, 255);">View Challenge</a>
                </li>
            </s:else>
        </s:if>
        <s:else>
            <s:if test="viewData.contestStats.draft">

                <li id="previewContestButton">
                    <a href="<s:property value='#contestLink'/>"  target="_blank" class="contestLinkIcon linkIconPreviewContest"
                       style="text-shadow: 0px 1px 1px rgb(255, 255, 255);">Preview Challenge</a>
                </li>

            </s:if>
            <s:else>

                <li id="viewContestButton">
                    <a href="<s:property value='#contestLink'/>" target="_blank" class="contestLinkIcon linkIconViewContest"
                       style="text-shadow: 0px 1px 1px rgb(255, 255, 255);">View Challenge</a>
                </li>
            </s:else>
        </s:else>

        <li class="splitter"></li>

        <li>
            <link:onlineReviewProjectDetails projectId="${param.projectId}" target="_blank"
                                             styleClass="contestLinkIcon linkIconOnlineReview">Online Review</link:onlineReviewProjectDetails>
        </li>
        <li class="splitter"></li>
        <li><a href="<s:property value='#contestLink'/>" target="_blank"
               class="contestLinkIcon linkIconRegistrationPage">Registration
            Page</a></li>
        <li class="splitter"></li>
        <li>
        <s:if test="!viewData.contestStats.isStudio">
        <a href="https://<%=ServerConfiguration.FORUMS_SERVER_NAME%>?module=Category&categoryID=${viewData.contestStats.forumId}" target="_blank"  class="contestLinkIcon linkIconForum" >Forum</a>
        </s:if>
        <s:elseif test="marathon">
            <a href="http://<%=ServerConfiguration.FORUMS_SERVER_NAME%>?module=ThreadList&forumID=${viewData.roundId}" target="_blank" class="contestLinkIcon linkIconForum">Forum</a>
        </s:elseif>
        <s:else>
            <s:if test="viewData.contestStats.newForum">
                <a href="https://<%=ServerConfiguration.FORUMS_SERVER_NAME%>?module=ThreadList&forumID=${viewData.contestStats.forumId}" target="_blank" class="contestLinkIcon linkIconForum">Forum</a>
            </s:if>
            <s:else>
                <a href="https://<%=ServerConfiguration.STUDIO_FORUMS_SERVER_NAME%>?module=ThreadList&forumID=${viewData.contestStats.forumId}" target="_blank" class="contestLinkIcon linkIconForum">Forum</a>
            </s:else>

        </s:else>
        </li>
		<li class="splitter"></li>
        <li>
        <link:jira projectId="${param.projectId}" target="_blank" styleClass="contestLinkIcon linkIconJIRA" >JIRA</link:jira>
        </li>

        <c:if test="${!viewData.contestStats.isStudio && ! empty viewData.contestStats.svn}">
		<li class="splitter"></li>
        <li><a href="${viewData.contestStats.svn}" target="_blank"  class="contestLinkIcon linkIconSVN">SVN</a></li>
        </c:if>

        <s:if test="marathon">
            <input id="mmProjectId" type="hidden" value="${viewData.contestStats.contest.id}"/>
            <input id="mmRoundId" type="hidden" value="${viewData.roundId}"/>
            <s:if test="hasRoundId">
                <li class="splitter"></li>
                <li><a href="javascript: roundIdModalLoad('update');">Update Round ID</a></li>
            </s:if>
            <s:else>
                <li class="splitter"></li>
                <li><a href="javascript: roundIdModalLoad('set');">Set Round ID</a> </li>
            </s:else>
        </s:if>
    </ul>
    <div class="clear"></div>
</div>
