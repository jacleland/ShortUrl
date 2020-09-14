/**
 *  This file belongs to the ShortUrl project, the latest version of which
 *  can be found at https://github.com/jacleland/ShortUrl.
 *
 *  Copyright (c) 2020, James A. Cleland <jcleland at jamescleland dot com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Application setup on load complete
 */
window.onload=function() {
	//Get page location URL for web services
	const pageUrl = window.location.href;
	
	//Hide the short URL text output initially
	showShortUrl(false);
	
	//Get controls
	var urlInput = document.getElementById("longUrl");
	var genButton = document.getElementById("generateButton");
	
	//Add event listener for URL field on paste
	urlInput.addEventListener('paste', (event) => {
		//Get pasted data from clipboard
	  let paste = (event.clipboardData || window.clipboardData).getData('text');

		//Hide results on change
		showShortUrl(false);
		
		//Validate pasted text
		enableGenerate(validateUrl(paste));
	});
	
	//Add listener for key up events
	urlInput.addEventListener('keyup', (event) => {
		event;
		//Hide results on change
		showShortUrl(false);

		//Validate URL text		
		enableGenerate(validateUrl(urlInput.value));
	});
	
	//Click listener for generate button
	genButton.addEventListener('click', (event) => {
		event;
		var end = pageUrl.lastIndexOf('/');
		var wsUrl = pageUrl.substr(0, end) + '/create';

		//Get long URL from text input
		var longUrl = document.getElementById("longUrl").value;
		
		var xmlHttp = new XMLHttpRequest();
		xmlHttp.open('PUT', wsUrl);
		xmlHttp.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
	
		xmlHttp.onload = function() {
			handleResponse(xmlHttp.responseText);
		}	
		
		xmlHttp.send('{"url": "'+longUrl+'"}');
	});
}

/**
 * Handle JSON response to /create 
 */
function handleResponse(response) {
	JSON.parse(response, (key, value) => {
		if(key == 'shortUrl')
			document.getElementById("shortUrl").value = value;
			showShortUrl(true);
	});	
}

/**
 * Simple regex for verifying that the provided URL kinda LOOKS valid at least
 */
function validateUrl(url) {
	var regex = new RegExp(/^(ftp|http|https):\/\/[^ "]+\/[^ ]*$/);
	return regex.test(url);
}

/**
 * Enables or disables the generate button, depending on whether or not the
 * URL value is valid.
 */
function enableGenerate(val) {
	var container = document.getElementById("generateButton");
	container.disabled = !val;
}

/**
 * Show or hide the short URL results container
 */
function showShortUrl(val) {
	var container = document.getElementById("shortUrlContainer");
	container.style.display = (val) ? "block" : "none";
}

function copyToClipboard(id) {
	id;
	var textControl = document.getElementById("shortUrl");
	//var shortUrl = textControl.value;
	
	//Select the text control contents
	textControl.select();
	document.execCommand('copy');
}
