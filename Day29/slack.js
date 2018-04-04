/**
 * 
 */
// Holds the Slack token
var token;

var listPublicUrl = "https://slack.com/api/channels.list";
var listPrivateUrl = "https://slack.com/api/groups.list";
var postUrl = "https://slack.com/api/chat.postMessage";

var addchannel_inputButton="<div class=\"row\">" +
		"<div class=\"col-sm-1\"><input name=\"channel\" type=\"channel_input\" /></div>" +
		"</div>";

var chosen_channel;
var list_of_channels = [];
var message;

var input_on = false;
var text_typed = false;
var show_private = false;

// HTML elements
var textarea;
var post_button;
var user_box;
var token_area;
var private_channels;

$(document).ready(function() {	
	textarea = $("#msg");
	post_button = $("#post_msg");
	user_box = $("#user_name");
	private_channels = $("#private_channel_list");
	
	// Grab the token from the other JavaScript file
	token = getSlackToken();
		
	// Disable button by default
	post_button.attr("disabled", "disabled");	
	
	// Hide the private channels by default
	private_channels.hide();
	
	// List the public and then private channels
	// in that order
	listChannelRows();
	
	textarea.on("keyup", function() {
		if(textarea.val().length > 0) {
			text_typed = true;
		} else {
			text_typed = false;
		}
		
		buttonStatus();
	})
	
	post_button.click(function(){
		submit();
	})
	
})

// Return a formatted row consisting of a channel_input button and the channel name
function addRow(object, channel_name) { 
	var channel_input = $("<div class=\"col-sm-1\"><input name=\"channel\" type=\"checkbox\" value=\"" + channel_name + "\" /></div>");
	var channel = $("<div class=\"col-sm-11\">");
	channel.append(channel_name);
	channel.append("</div>");
	
	var row = $("<div class=\"row\">");
	row.append(channel_input);
	row.append(channel);
	row.append($("</div>"));
	
	object.append(row);
}

// List first the public and then the private channels
function listChannelRows() {
	listPublicChannels();
}

// Get the list of available public Slack channels.
// Then list the private ones
function listPublicChannels() {
	var public_channels = $("#public_channel_list");
	
	// Get the list of available public Slack channels
	listPublicUrl += "?token=" + token;
	$.ajax(listPublicUrl, {
		"method": "GET"
	}).then(function(result) {
		listChannels(result.channels, public_channels);		
		
		// Embed here to always list the public channels first
		// followed by the private ones
		listPrivateChannels();
	})
}

// Get the list of available private Slack channels
function listPrivateChannels() {
	listPrivateUrl += "?token=" + token;
	$.ajax(listPrivateUrl, {
		"method": "GET"
	}).then(function(result) {
		listChannels(result.groups, private_channels);
		
		$("input:checkbox").click(function() {
						
			console.log("Checked a box!");
			chosen_channel = $(this).val();
			
			// If indexOf > -1, the channel is already included so take it off the list
			var index = list_of_channels.indexOf(chosen_channel);
			if(index > -1) {
				console.log("Removing from the list", chosen_channel);
				list_of_channels.splice(index, 1);
			} else {
				console.log("Adding to the list", chosen_channel);
				list_of_channels.push(chosen_channel);
			}
			
			console.log("Chose", chosen_channel);
			
			input_on = true;
			
			buttonStatus();
		});
		
		$("#toggle_link").click(function() {
			if(show_private == false) {
				private_channels.show();
				show_private = true;
			} else {
				private_channels.hide();
				show_private = false;
			}
		})
	})
}

// Iterate through the result object for both the
// public and private channels and list them
function listChannels(result, object) {
	result.sort();
	for(var idx = 0; idx < result.length; idx++) {
		addRow(object, result[idx].name);
	}
}

// Check whether the button should be displayed.
// The button should only be displayed if a channel
// is chosen and text is present in the textarea
function buttonStatus() {
	console.log(list_of_channels.length);
	if((text_typed == true) && (list_of_channels.length > 0)) {
		post_button.removeAttr("disabled");
		
		textarea.keypress(function(event){
			var keycode = (event.keyCode ? event.keyCode : event.which);
			if(keycode == '13'){
				submit();	
				return false;
			}
		});		
	} else {
		post_button.attr("disabled", "disabled");
	}
}

// Do the submission work
function submit() {
	// Message collected from the textarea
	message = textarea.val();
	var user_name = user_box.val();
		
	postUrl += "?token=" + token + "&text=" + message + "&username=" + user_name;
	
	console.log("Clicked the post button with message", message);
	console.log("Calling with url", postUrl);
	
	if(user_name.length == 0) {
		user_box.val("Slack API Tester");
	}
	
	console.log("Channels contained are:");
	for(var idx = 0; idx < list_of_channels.length; idx++) {
		console.log(list_of_channels[idx]);
		$.ajax(postUrl + "&channel=" + list_of_channels[idx], {
			"method": "POST"
		})
	}
	
	// Clear the textarea after posting
	textarea.val("");
	
	// Textarea now has length 0 so disable the button
	text_typed = false;
	buttonStatus();
}