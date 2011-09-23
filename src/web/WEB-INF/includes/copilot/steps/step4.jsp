<%--
  - Author: TCSASSEMBLER
  - Version: 1.0
  - Copyright (C) 2011 TopCoder Inc., All Rights Reserved.
  -
  - Description: The step 4 of post a copilot.
  - Since: TC Cockpit Post a Copilot Assembly 1
  - Version 1.0 (TC Cockpit Post a Copilot Assembly 1).
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/includes/taglibs.jsp" %>
<div class="postCopilotStep4 stepDiv">
    <!-- form -->
    <div class="form">
        
        
        <div class="noteContainer">
            <p>
            In this step you will be directed to set schedule for your project. Please fill out the form below:
           </p>
        </div>
        
        <!-- row -->
        <div class="row">
            <span class="intro">All fields marked with <span class="red">*</span> are mandatory</span>
            <div class="clear"></div>
            
            <div class="title"><!-- columns title -->
                <div class="titleLeft"><div class="titleRight">
                    <h2>Project Schedule</h2>
                </div></div>
            </div><!-- End .title -->
            
            
            <div class="rowItem">
                <div class="lineItem">
                    <label>Do you want to post this opportunity to the Copilots now?</label>
                    <a href="javascript:;" class="red-button-big"><span class="right"><span class="middle">YES, POST NOW</span></span></a>
                    <div class="clear"></div>
                </div>
                <div class="lineItem amountItem inputItem">
                    <label><strong>OR</strong></label>
                    
                    <div class="postFrame hide">
                        <span class="grayArrow"></span>
                        
                        <p>Most copilot posting are listed for $<span class="defaultFirstPlaceCost"></span>. Would you like to proceed with <br />this amount or set your own amount?</p>
                        
                        <ul>
                            <li>
                                <input type="radio" class="radio2 proceedRadio" name="amount"/><label for="proceed">Proceed</label>
                            </li>
                            <li>
                                <input type="radio"  class="radio2 useAmountRadio" name="amount"/><label for="useAmount">Use this amount</label> <input type="text" class="text amountText"  disabled="disabled"/>
                            </li>
                        </ul>
                    </div>
                    
                    <div class="clear"></div>
                </div>
                <div class="lineItem">
                    <label>Would you like to schedule it for a later date?</label>
                    <a href="javascript:;" class="red-button-big"><span class="right"><span class="middle">YES, DO IT LATER</span></span></a>
                    <div class="clear"></div>
                </div>               
            
                <div class="lineItem dateItem inputItem">
                    <label>&nbsp;</label>
                    
                    <div class="postFrame hide">
                        <span class="grayArrow"></span>
                        
                        <p>Most copilot posting are listed for $<span class="defaultFirstPlaceCost"></span>. Would you like to proceed with <br />this amount or set your own amount?</p>
                        
                        <ul>
                            <li>
                                <input type="radio" class="radio2 proceedRadio" name="amount"/><label for="proceed">Proceed</label>
                            </li>
                            <li>
                                <input type="radio"  class="radio2 useAmountRadio" name="amount"/><label for="useAmount">Use this amount</label> <input type="text" class="text amountText"  disabled="disabled"/>
                            </li>
                        </ul>                            
                        
                        <p>&nbsp;</p>
                        
                        <p>Start Date</p>
                        <div id="launchContestOut" class="noBorder">
                            <div class="row schedule">
                                <jsp:useBean id="currentDate" class="java.util.Date" />
                                <input id="currentCopilotDate" type="hidden" value='<fmt:formatDate value="${currentDate}" type="date" pattern="MM/dd/yyyy" />' />
                                
                                <input id="startDate" name="startDate" type="text"  class="text date-pick" readonly="true"/>
                                <div class="startEtSelect">
                                    <select id="startTime" name="startTime" ><jsp:include page="../../common/timeOptions.jsp"/></select>
                                </div>
                                <span>ET (UTC-05)</span>
                            </div>
                        </div>                          
                        
                    </div>
                    
                    <div class="clear"></div>
                </div>                
                            
            </div>
            <!--End .rowItem-->
            
        </div>
        <!-- End .row -->
        
    </div>
    <!-- End .form --> 
</div>