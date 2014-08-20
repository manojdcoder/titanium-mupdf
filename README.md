#PDF Reader

## Description

This module extends the android project here - https://github.com/joniks/Android-MuPDF/ 

## Usage

```javascript
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

win.addEventListener("open", function(e) {
	var activity = win.getActivity();
	activity.onCreateOptionsMenu = function(e) {
		var previousItem = e.menu.add({
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
		});
	};
	activity.invalidateOptionsMenu();
});

Ti.Gesture.addEventListener("orientationchange", function(){
	pdfReader.setCurrentPage(pdfReader.getCurrentPage());
});

win.open();

```

## Changelog

* v1.1
  * added x86 support
