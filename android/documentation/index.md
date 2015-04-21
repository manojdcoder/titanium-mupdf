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

var pdfReader = READER_MODULE.createPDFReader({
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
				message : "Search for the keyword 'for' in the entire pdf",
				duration : Ti.UI.NOTIFICATION_DURATION_LONG
			});
			toast.show();
			count = 0;
			pdfReader.onSearch(searchResult);
			pdfReader.search("for", 0);
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
			pdfReader.search("for", -1);
		});
		var searchNextItem = e.menu.add({
			title : "Search Next",
			showAsAction : Ti.Android.SHOW_AS_ACTION_IF_ROOM
		});
		searchNextItem.addEventListener("click", function(e) {
			pdfReader.onSearch(logSearch);
			pdfReader.search("for", 1);
		});
		var toggleHightLight = e.menu.add({
			title : "Toggle highlight",
			showAsAction : Ti.Android.SHOW_AS_ACTION_IF_ROOM
		});
		toggleHightLight.addEventListener("click", function(e) {
			enabled = !enabled;
			pdfReader.setHighlightColor( enabled ? "#0000FF" : "transparent");
			pdfReader.onSearch(logSearch);
			pdfReader.search("for", 0);
		});
	};
	activity.invalidateOptionsMenu();
});

function searchResult(result) {
	console.log(result);
	if (count == 0 && result.error) {
		if (result.code == READER_MODULE.ERROR_TEXT_NOT_FOUND) {
			alert("No matches found");
		} else if (result.code == READER_MODULE.ERROR_NO_FURTHER_OCCURRENCES_FOUND) {
			alert("No more occurrences on the given direction");
		}
		return;
	}
	count += result.count;
	if (result.success && result.currentPage < pdfReader.getPageCount()) {
		pdfReader.search("for", 1);
	} else {
		pdfReader.setCurrentPage(1);
		alert("Total occurence : " + count);
	}
}

function logSearch(evt) {
	console.log(evt);
}

Ti.Gesture.addEventListener("orientationchange", function() {
	pdfReader.setCurrentPage(pdfReader.getCurrentPage());
});

win.open();
```