// JavaScript Document
$(document).ready(function(){
						   
	
	
	//------------------------------------------------- Contests List
	
	/* sort contests */
	$("#contestsTable").tablesorter(); 
	
	/* sort contest by title */
	sortTitle = function(){
		 var sorting = [[1,0]];
        $("#contestsTable").trigger("sorton",[sorting]); 
	}
	
	/* sort contest by status */
	sortStatus = function(){
		var sorting = [[0,0]];
        $("#contestsTable").trigger("sorton",[sorting]); 
	}
	
	/* sort contest by type */
	sortType = function(){
		var sorting = [[2,0]];
        $("#contestsTable").trigger("sorton",[sorting]); 
	}
	
	/* get the selected index and sort the contests table */
	sortTable = function(mySelect){
		var selected = mySelect.options[mySelect.selectedIndex].value;
		
		if( selected == "title" )
			sortTitle();
		else if(selected == "status")
			sortStatus();
		else 
			sortType();
	}
	
	/*---------------------- Show the scrollbar when the number of contests is more than 10----*/
	
	var rows_height = 0;
	var contests_nbre = 0;
	
	/* get the height of the 10 first rows ( one contest per row)*/
	$("#contestsTable TBODY").children().each(function(){
			if( contests_nbre < 10 )
				rows_height += $(this).height();
			contests_nbre++;
	});
	
	if( contests_nbre > 10 ){ // if the number of contests > 0 we will set the height to show the scrollbar 
		$(".contestsContent").height(rows_height);
		$(".contestsContent TABLE").css("width","232px");
	}
	
	
	
	/* Stylished scrollbar*/
	$('.contestsContent').jScrollPane({
		scrollbarWidth: 17,
		showArrows: true
	
	});
	
	/* if a user click a contest cell he will be taken to that Contest description page*/
	$(".contestsContent TD").click(function(){
		document.location.href='contest-details.html';
	});
	
	
	
	/* if a user click a contest cell he will be taken to that Contest description page*/
	$(".contestsContent1 TD").click(function(){
		document.location.href='contest-details.html';
	});
	
	/*-------------------------- Show/hide the dropdown list --*/
	
	showHideList = function(){
		$("#dropDown1").slideToggle(100);
		$("#sortTableBy").toggle();
	}
	
	/*------------------------- hover state of the dropdown list  --*/
	
	$(".contestsDropDown UL LI").mouseover(function(){
			$(this).addClass("hover");
	});
	
	$(".contestsDropDown UL LI").mouseout(function(){
			$(this).removeClass("hover");
	});
	
	/*------------------------- hover state of the contests table --*/
	$("table#contestsTable tr").mouseover(function(){
		$(this).addClass("highlight");
	});
	
	$("table#contestsTable tr").mouseout(function(){
		$(this).removeClass("highlight");
	});
	
	
	/*------------------------- show or hide rows functionnality in dashboard.html --*/
	// we will show just the first rows_nbre rows 
	$("TABLE.rowsToHide").each(function(){
		
		var table_id = $(this).attr("id");
		var hide_row = false;
		
		$("#"+table_id+" TBODY TR").each(function(){
												  
				if( this.className.search("hideStart") != -1 ){
						hide_row = true;
				}
					
				if( hide_row )
						$(this).addClass("hide");								
		})
	});
	
	
	showHideRows = function(myLink, tableId){
		
		if( $(myLink).html() == "View More" ){ //when the user click the view more link we will show the hidden rows
			$("#"+tableId+" TBODY TR").each(function(){
				$(this).removeClass("hide");
			});
			
			 $(myLink).html("Hide Rows");
			 $(myLink).addClass("less");
			 
		}else{ //when the user click the hide rows link we will hide some rows
			var hide_row = false;
			$("#"+tableId +" TBODY TR").each(function(){
					
					if( this.className.search("hideStart") != -1 ){
						hide_row = true;
					}
					
					if( hide_row )
						$(this).addClass("hide");
			});
			
			$(myLink).html("View More");
			$(myLink).removeClass("less");
		}
		
	}
	
	/*----------------- projects table hover --*/
	$("table.project TR").mouseover(function(){
			$(this).addClass("hover");
	});
	
	$("table.project TR").mouseout(function(){
			$(this).removeClass("hover");
	});
	
	/*------------------------------------------------------------ Calendar --*/
	

	
		var date = new Date();
		var d = date.getDate();
		var m = date.getMonth();
		var y = date.getFullYear();
		
		$('#calendar').fullCalendar({
			header: {
				left: 'prev',
				center: 'title',
				right: 'next'
			},
			editable: false,
			dayNamesShort: ['Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday'],
			events: [
				{
					title: '<a href=# class=launch onmouseover=showPopup(this,\'contestLaunch1\')>Contest Launch</a>',
					start: new Date(y, m, 8)
				},
				{
					title: '<a href=# class=launch onmouseover=showPopup(this,\'contestLaunch4\')>Spec Post</a>',
					start: new Date(y, m, 8)
				},
				{
					title: '<a href=# class=task onmouseover=showPopup(this,\'contestLaunch5\')>Task</a>',
					start: new Date(y, m, 8)
				},
				{
					title: '<a href=# class=launch onmouseover=showPopup(this,\'contestLaunch2\')>Review Complete</a>',
					start: new Date(y, m, 11)
				},
				{
					title: '<a href=# class=forum onmouseover=showPopup(this,\'contestLaunch6\')>Forum Post</a>',
					start: new Date(y, m, 11)
				},
				{
					title: '<a href=# class=forum onmouseover=showPopup(this,\'contestLaunch7\')>Forum Post</a>',
					start: new Date(y, m, 11)
				},
				{
					title: '<a href=# class=task onmouseover=showPopup(this,\'contestLaunch8\')>Task</a>',
					start: new Date(y, m, 11)
				},
				{
					title: '<a href=# class=launch onmouseover=showPopup(this,\'contestLaunch3\')>Review Pending</a>',
					start: new Date(y, m, 13)
				},
				{
					title: '<a href=# class=forum onmouseover=showPopup(this,\'contestLaunch9\')>Forum Post</a>',
					start: new Date(y, m, 13)
				},
				{
					title: '<a href=# class=task onmouseover=showPopup(this,\'contestLaunch10\')>Task</a>',
					start: new Date(y, m, 13)
				},
				{
					title: '<a href=# class=milestone onmouseover=showPopup(this,\'contestLaunch11\')>Milestone Feed back</a>',
					start: new Date(y, m, 17)
				},
				{
					title: '<a href=# class=task onmouseover=showPopup(this,\'contestLaunch12\')>Task 1</a>',
					start: new Date(y, m, 17)
				},
				{
					title: '<a href=# class=task onmouseover=showPopup(this,\'contestLaunch13\')>Task 2</a>',
					start: new Date(y, m, 17)
				},
				{
					title: '<a href=# class=winner onmouseover=showPopup(this,\'contestLaunch14\')>Winner Announcement</a>',
					start: new Date(y, m, 20)
				}
			]
		});
		

	
	/*----------------- this function is for demonstration purpose, it will show some contests on the contests list --*/
	showContestsDemo = function(){
			var curr = 0;
			$("TABLE#contestsTable TBODY TR").each(function(){
					if( curr > 2 )
						$(this).addClass("hide");
						
					curr++;
			});
	}
	
	
	/*-------------------------------------------------------------- Popup -----------------*/
	
	var prevPopup = null;
	showPopup = function(myLink,myPopupId){
		var myLinkLeft = myLinkTop  = 0;
		
		/* hide the previous popup */
		if( prevPopup )
			$(prevPopup).css("display","none");
			
		prevPopup = $('#'+myPopupId);
		
		/* get the position of the current link */
		do{
			myLinkLeft += myLink.offsetLeft;
			myLinkTop += myLink.offsetTop;
		}while( myLink = myLink.offsetParent );
		
		/* set the position of the popup */
		var popUpHeight2 = $('#'+myPopupId).height()/2;
		
		myLinkTop -= popUpHeight2;
	
		$('#'+myPopupId).css("top",myLinkTop+'px');
		$('#'+myPopupId).css("left",( myLinkLeft + 50 )+'px');
		
		/* set the positio of the arrow inside the popup */
		$(".tooltipContainer SPAN.arrow").css("top",popUpHeight2+'px');
		
		/* show the popup */
		$('#'+myPopupId).css("display","block");
		
	}
	
	/* if the user click the next of prev link of the calendar we will hide the popups */
	$(".fc-button-prev a").click(function(){
		$(prevPopup).css('display','none');
	});
	
	$(".fc-button-next a").click(function(){
		$(prevPopup).css('display','none');
	});
	
	/*------------------------------------------------------------------------------------------*/
	
	/*-----------------  tabs4 navigation   -*/
	showHideTabs = function(myLink, myContainerId, myTabsIndex){
		/* myLink: the clicked link
		   myContainerID: the id of the tabs container
		   myTabsIndex: the index num of the tab */
		
		// get the first "ul" parent
		// the html structure is as fellow: <ul><li><a href=""></a></li></ul>
		var ULparent = $(myLink).parents()[1];
		var curr_link = 0;
		
		$($(ULparent).children()).each(function(){ //add the "on" class to the parent (li) of the clicked link
				
				if( myTabsIndex == curr_link ){
					$(this).addClass("on");
				}else{
					$(this).removeClass("on");
				}
				
				curr_link++;
				   
		});
		   
		var current_tab = 0;
		$($("#"+myContainerId).children()).each(function(){ // show the tab with the index myTabsIndex and hide the others
				
				if( current_tab == myTabsIndex ){
					$(this).css('display','block');
				}else{
					$(this).css('display','none');
				}
				
				current_tab++;
				
		})
	}
	
	/*--------------------------------------- Show/Hide group users (the table on the permissions tab ) --*/
	showHideGroup = function(myLink, rowClass){
		/*  myLink: the clicked link
			rowClass: the class name of the group */
		
		/* change the link ico */
		if( myLink.className.search("expand") != -1 ){
			$(myLink).removeClass("expand");
			$(myLink).addClass("collapse");
		}else{
			$(myLink).removeClass("collapse");
			$(myLink).addClass("expand");
		}
		
		
		$("."+rowClass).toggle();
	}
	
	/*--------------------------------------- Show/Hide group users (the table on the permissions tab ) --*/
	showHideGroup2 = function(myLink, rowClass){
		/*  myLink: the clicked link
			rowClass: the class name of the group */
		
		/* change the link ico */
		var $kid = $(myLink).children();
		if( $kid.hasClass("expand")){
			$kid.removeClass("expand");
			$kid.addClass("collapse");
		}else{
			$kid.removeClass("collapse");
			$kid.addClass("expand");
		}
				
		$("."+rowClass).toggle();
	}
	
	/*--------------------------------------- Hide group users (the table on the permissions tab ) --*/
	hideGroup = function(group, rowClass){
		/*  myLink: the clicked link
			rowClass: the class name of the group */
		
		/* change the link ico */
		$("."+group).removeClass("collapse");
		$("."+group).addClass("expand");
		

		$("."+rowClass).hide();
	}
	/*--------------------------------------- Show group users (the table on the permissions tab ) --*/
	showGroup = function(group, rowClass){
		/*  myLink: the clicked link
			rowClass: the class name of the group */
		
		/* change the link ico */
		
		$("."+group).removeClass("expand");
		$("."+group).addClass("collapse");
		
		$("."+rowClass).show();
	}
	
	/*--------------------------------------- Show/Hide spec review question (on the spec review tab ) --*/
	showHideDiv = function(div_to_show, div_to_hide){
		$('#'+div_to_show).show();
		$('#'+div_to_hide).hide();
	}

	/*--------------------------------------- Change the comment according to the input text --*/
	displayComment = function(commentId, inputId){
		$('#'+commentId).html($('#'+inputId+' textarea').val());
	}
	
	$('.textarea1 textarea').click(function(){
		if ($(this).val() == 'Input your comment...') {
			$(this).html('');
		}
	}).blur(function() {
		if ($(this).val() == '') {
			$(this).html('Input your comment...');
		}
	});
	
	/*--------------------------------------- Image button hover effect --*/
	$("div.comment_specreview a").hover(function(){
		$("img.sr_editcomment", this).attr("src", "images/edit_red.png");
	}, function() {
		$("img.sr_editcomment", this).attr("src", "images/edit.png");
	});
	
	$("div.to_add_your_comment a").hover(function(){
		$("img.sr_addcomment", this).attr("src", "images/add_your_comment_hover.png");
	}, function() {
		$("img.sr_addcomment", this).attr("src", "images/add_your_comment.png");
	});
	
	/*-------------------------------------- Check/uncheck all checkboxes functionnality --*/
	checkAll = function(myCheckbox, myContainerId){
		/* myCheckbox: the "select all" check box
		   myContainerId: the id of the container of the checkboxes */
	
		$("#"+myContainerId+" input.selectUser").each(function(){
				if( myCheckbox.checked )
					this.checked = true;
				else
					this.checked = false;
		});
	}
	
	/*------------------------------ w/search ---*/
	var prev_result = null;
	showResults = function(mySelect, containerId){
	
		$("#"+containerId).css('display','block');
		
		if( prev_result )
			$(prev_result).css('display','none');
		
		var result_container_id =  mySelect[0].options[mySelect[0].selectedIndex].value;
		$("#"+result_container_id).css('display','block');
		
		prev_result = $("#"+result_container_id);
		
	}
	
	/*-------------------------------------------------------------------------*/
	/* add zebra stripping for projectStats tables */
	$('table.contests tbody tr:odd').addClass('even');
	
	$('a.clearDataFind').live('click', function(){
		resetForm('find');
	});
	
	showHide = function(rowClass){
		$("."+rowClass).toggle();
	}
	
	$('a.button6.apply').live('click', function(){
		
		/* change the link ico */		
		$('.collapse').addClass("expand").removeClass("collapse");
		
		$('.roundedBox').toggle();
		showHide('apply');
	});

	/*help center widget tab function*/

    $("#helpCenterWidget .tabList li a.tab").click(function() {
        $("#helpCenterWidget .tabContent").hide();
        $(this).addClass("actived");
        $(this).parent("li").siblings("li").children("a.tab").removeClass("actived");
        switch ($(this).attr("id")) {
            case "FAQTab":
                $("#FAQTabContent").show();
                break;
            case "videoTab":
                $("#videoTabContent").show();
                break;
            case "tutorialTab":
                $("#tutorialTabContent").show();
                break;
            case "exampleTab":
                $("#exampleTabContent").show();
                break;
            case "moreTab":
                $(".tab").hide();
                $(".tabMore,#exampleTab").css("display", "inline-block");
                $("#exampleTabContent").show();
                $("#exampleTab").addClass("actived");
                break;
            default:
                break;
        }
    });
    $("#helpCenterWidget .tabList li a#lessTab").click(function() {
        $("#moreTab").removeClass("actived");
        $(".tab").show();
        $("#exampleTabContent").hide();
        $("#exampleTab").removeClass("actived");
        $(".tabMore,#exampleTab").css("display", "none");
        $("#FAQTabContent").show();
        $("#FAQTab").addClass("actived");

    });	
});

function resetForm(className) {
	$('.'+className).each(function(){
	    this.reset();
	});
}