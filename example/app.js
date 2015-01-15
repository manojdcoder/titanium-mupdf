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

pdfReader.addEventListener("pagechanged", function(evt) {
	/*
	 *
	 * properties of evt
	 * currentPage - being viewed
	 * pageCount - number of pages in pdf
	 *
	 */
	console.log("Viewing " + evt.currentPage + " / " + evt.pageCount);
});

pdfReader.addEventListener("click", function(evt) {
	console.log("you just clicked on pdf reader");
});

var enabled = true;
win.addEventListener("open", function(e) {
	var activity = win.getActivity();
	activity.onCreateOptionsMenu = function(e) {
		/*var previousItem = e.menu.add({
		 title : "Previous",
		 showAsAction : Ti.Android.SHOW_AS_ACTION_ALWAYS
		 });
		 previousItem.addEventListener("click", function(e) {
		 pdfReader.moveToPrevious();
		 });
		 var nextItem = e.menu.add({
		 title : "Next",
		 showAsAction : Ti.Android.SHOW_AS_ACTION_ALWAYS
		 });
		 nextItem.addEventListener("click", function(e) {
		 pdfReader.moveToNext();
		 });*/
		var toggleHightLight = e.menu.add({
			title : "Toggle highlight",
			showAsAction : Ti.Android.SHOW_AS_ACTION_ALWAYS
		});
		toggleHightLight.addEventListener("click", function(e) {
			enabled = !enabled;
			pdfReader.enableHighlight(enabled);
			pdfReader.search("java", 0);
		});
		var searchPreviousItem = e.menu.add({
			title : "Search P",
			showAsAction : Ti.Android.SHOW_AS_ACTION_ALWAYS
		});
		searchPreviousItem.addEventListener("click", function(e) {
			pdfReader.search("java", -1);
		});
		var searchNextItem = e.menu.add({
			title : "Search N",
			showAsAction : Ti.Android.SHOW_AS_ACTION_ALWAYS
		});
		searchNextItem.addEventListener("click", function(e) {
			pdfReader.search("java", 1);
		});
	};
	activity.invalidateOptionsMenu();
});

pdfReader.onSearch(searchResult);

function searchResult(result) {
	console.log(pdfReader.ERROR_NO_FURTHER_OCCURRENCES_FOUND);
	if (result.error) {
		if (result.code == READER_MODULE.ERROR_TEXT_NOT_FOUND) {
			alert("No matches found");
		} else if (result.code == READER_MODULE.ERROR_NO_FURTHER_OCCURRENCES_FOUND) {
			alert("No more occurrences on the given direction");
		}
	}
	console.log(result);
}

Ti.Gesture.addEventListener("orientationchange", function() {
	pdfReader.setCurrentPage(pdfReader.getCurrentPage());
});

win.open();
