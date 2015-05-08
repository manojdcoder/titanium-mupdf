#PDF Reader

## Description

This module extends the android project here - https://github.com/joniks/Android-MuPDF/ 

## Usage

```javascript
var enabled = true;
var count = 0;

var win = Ti.UI.createWindow({
	backgroundColor : 'white',
	exitOnClose : true
});

var READER_MODULE = require("com.mykingdom.mupdf");

//Make sure the file exists
var file = Ti.Filesystem.getFile(Ti.Filesystem.externalStorageDirectory, "sample.pdf");

if (!file.exists()) {
	var source = Ti.Filesystem.getFile(Ti.Filesystem.resourcesDirectory, "sample.pdf");
	file.write(source.read());
}

console.log(">>EXISTS>>>" + file.exists());

var processDialog = Ti.UI.Android.createProgressIndicator({
	message : 'Searching...',
	location : Ti.UI.Android.PROGRESS_INDICATOR_DIALOG,
	type : Ti.UI.Android.PROGRESS_INDICATOR_INDETERMINANT,
	cancelable : false
});

var pdfReader = READER_MODULE.createView({
	file : file
});

//pdfReader.loadPDFFromFile(file);

win.add(pdfReader);

/*
 *
 * Available methods:
 * ------------------
 * pdfReader.getCurrentPage() - returns current page
 * pdfReader.setCurrentPage(pageNum) - set current page
 * pdfReader.getPageCount() - returns total number of pages
 *
 */

pdfReader.addEventListener("change", function(evt) {
	/*
	 *
	 * properties of evt
	 * currentPage - being viewed
	 * count - number of pages in pdf
	 *
	 */
	console.log("Viewing " + evt.currentPage + " / " + evt.count);
});

pdfReader.addEventListener("click", function(evt) {
	console.log("you just clicked on pdf reader");
});

win.addEventListener("open", function(e) {
	var activity = win.getActivity();
	activity.onCreateOptionsMenu = function(e) {
		var searchItem = e.menu.add({
			title : "Search",
			showAsAction : Ti.Android.SHOW_AS_ACTION_ALWAYS
		});
		searchItem.addEventListener("click", function(e) {
			var toast = Ti.UI.createNotification({
				message : "Search for the total occurences of keyword 'for' in the entire pdf. Note : Touch events will be disabled during search",
				duration : Ti.UI.NOTIFICATION_DURATION_LONG
			});
			toast.show();
			count = 0;
			processDialog.show();
			pdfReader.touchEnabled = false;
			pdfReader.onSearch(searchResult);
			//start search from page no. 1.
			//third parameter is optional, defaults to false. Disable the rendering of the search. If true the page will be rendered with results highlighted
			pdfReader.search("for", 1, false);
		});
		var previousItem = e.menu.add({
			title : "Previous",
			showAsAction : Ti.Android.SHOW_AS_ACTION_IF_ROOM
		});
		previousItem.addEventListener("click", function(e) {
			pdfReader.moveToPrevious();
		});
		var nextItem = e.menu.add({
			title : "Next",
			showAsAction : Ti.Android.SHOW_AS_ACTION_IF_ROOM
		});
		nextItem.addEventListener("click", function(e) {
			pdfReader.moveToNext();
		});
		var searchPreviousItem = e.menu.add({
			title : "Search Previous",
			showAsAction : Ti.Android.SHOW_AS_ACTION_IF_ROOM
		});
		searchPreviousItem.addEventListener("click", function(e) {
			pdfReader.onSearch(logSearch);
			pdfReader.search("for", pdfReader.getCurrentPage() - 1, true);
		});
		var searchNextItem = e.menu.add({
			title : "Search Next",
			showAsAction : Ti.Android.SHOW_AS_ACTION_IF_ROOM
		});
		searchNextItem.addEventListener("click", function(e) {
			pdfReader.onSearch(logSearch);
			pdfReader.search("for", pdfReader.getCurrentPage() + 1, true);
		});
		var toggleHightLight = e.menu.add({
			title : "Toggle highlight",
			showAsAction : Ti.Android.SHOW_AS_ACTION_IF_ROOM
		});
		toggleHightLight.addEventListener("click", function(e) {
			pdfReader.setHighlightColor( enabled ? "#500000FF" : "transparent");
			pdfReader.onSearch(logSearch);
			//search and render results for the current page
			pdfReader.search("for", pdfReader.getCurrentPage(), true);
			enabled = !enabled;
		});
	};
	activity.invalidateOptionsMenu();
});

function logSearch(evt) {
	console.log(evt);
}

function searchResult(result) {
	console.log(result);
	count += result.count;
	if (result.success && result.currentPage < pdfReader.getPageCount()) {
		// search for next page until end of the pdf
		pdfReader.search("for", result.currentPage + 1);
	} else {
		processDialog.hide();
		pdfReader.touchEnabled = true;
		if (count == 0) {
			alert("No matches found");
		} else {
			alert("Total occurence : " + count);
		}
	}
}

Ti.Gesture.addEventListener("orientationchange", function() {
	pdfReader.setCurrentPage(pdfReader.getCurrentPage());
});

win.open();
```

## Changelog
* v1.6
	* crash issues fixed
	
* v1.5
	* replaced createPDFReader with createView
	* updated mupdf library to 1.7
	* enchancements on search method 

* v1.4
	* added methods setHighlightColor
	* added events click and change
