/****************************************************************************
 **
 ** Copyright (C) 2013 ePapyrus, Inc.
 ** All rights reserved.
 **
 ** This file is part of PlugPDF for Android project.
 **
 ****************************************************************************/
package com.epapyrus.plugpdf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epapyrus.plugpdf.core.CoordConverter;
import com.epapyrus.plugpdf.core.PDFDocument;
import com.epapyrus.plugpdf.core.PropertyManager;
import com.epapyrus.plugpdf.core.annotation.AnnotEventListener;
import com.epapyrus.plugpdf.core.viewer.BasePlugPDFDisplay.PageDisplayMode;
import com.epapyrus.plugpdf.core.viewer.DocumentState;
import com.epapyrus.plugpdf.core.viewer.DocumentState.OPEN;
import com.epapyrus.plugpdf.core.viewer.PageViewListener;
import com.epapyrus.plugpdf.core.viewer.ReaderListener;
import com.epapyrus.plugpdf.core.viewer.ReaderView;

/**
 * You can create a custom PDF viewer class by using PDFDocument class directly
 * and not using SimpleDocumentViewer at all.
 * 
 * @brief Provides the fundamental PDF viewer model for all Android apps.
 */
public class SimpleDocumentReader implements ReaderListener {

	private ReaderView mReaderView;
	private SimpleReaderControlView mControlView;
	private SimpleDocumentReaderListener mListener;
	private Activity mAct;
	private String	mFilePath = null;
	private byte[] mFileData = null;



	public SimpleDocumentReader(Activity act) {
		mAct = act;
		mReaderView = new ReaderView(mAct);
		mReaderView.setReaderListener(this);

		mControlView = (SimpleReaderControlView) SimpleReaderControlView
				.inflate(mAct, R.layout.simple_reader_control, null);
		mControlView.createUILayout(mReaderView);
	}

	/**
	 * Register a callback to be invoked when the result of document open.
	 * 
	 * @param listener
	 *            An implementation of SimpleDocumentViewerListener
	 */
	public void setListener(SimpleDocumentReaderListener listener) {
		mListener = listener;
	}

	/**
	 * Register a callback to be invoked when the page loading is complete.
	 * 
	 * @param listener
	 *            An implementation of PageViewListener
	 */
	public void setPageViewListener(PageViewListener listener) {
		mReaderView.setPageViewListener(listener);
	}

	/**
	 * Register a callback to be invoked when an annotation event occurs.
	 * 
	 * @param listener
	 *            An implementation of PlugPDFAnnotEventListener
	 */
	public void setAnnotEventLisener(AnnotEventListener listener) {
		mReaderView.setAnnotEventListener(listener);
	}

	public ReaderView getReaderView() {
		return mReaderView;
	}

	/**
	 * Open a SimpleDocumentViewer object with the contents at the specified
	 * path.
	 * 
	 * @param filePath
	 *            The path and name of the accessible PDF
	 * @param password
	 *            The password to unlock an encrypted document.
	 */
	public void openFile(String filePath, String password) {
		mFilePath = filePath;
		mReaderView.openFile(filePath, password);
	}

	/**
	 * Open a PDF document with the passed-in data.
	 * 
	 * @param data
	 *            The byte array from PDF data
	 * @param len
	 *            The length of the data passed-in
	 * @param password
	 *            The password to unlock an encrypted document.
	 */
	public void openData(byte[] data, int len, String password) {
		mFileData = data;
		mReaderView.openData(data, len, password);
	}

	/**
	 * Open a PDF document from JetStream Server.
	 * 
	 * @param url
	 *            The url data of JetStream server encapsulated in an String
	 *            object.
	 * @param port
	 *            The port number of JetStream server connection information.
	 * @param filename
	 *            The PDF file name to open on JetStream server.
	 * @param password
	 *            The password to unlock an encrypted document.
	 */
	public void openJetStreamUrl(String url, int port, String filename,
			String password) {
		mReaderView.openJetStreamUrl(url, port, filename, password);
	}
	
	/**
	 * Open a PDF document with URL.
	 * 
	 * @param url
	 *            The url data of Web Address an String object.
	 * @param password
	 *            The password to unlock an encrypted document.
	 */
	public void openUrl(String url, String password) {
		mReaderView.openUrl(url, password);
	}

	/**
	 * Restores the state of the PDF info the object passed.
	 * 
	 * @param object
	 *            The state of the PDF info which to restore
	 */
	public void restoreReaderState(Object object) {
		mReaderView.restoreSavedState(object);
	}

	/**
	 * Returns the current state value that is PDF info.
	 * 
	 * @return state of the current PDF info
	 */
	public Object getReaderState() {
		return mReaderView.getState();
	}

	/**
	 * Saves the bundle object of the controller state.
	 * 
	 * @param state
	 *            The bundle object
	 */
	public void saveControlState(Bundle state) {
		mControlView.saveState(state);
	}

	/**
	 * Restores the state of the controller as the bundle object passed.
	 * 
	 * @param state
	 *            The bundle object that contains controller state info
	 */
	public void restoreControlState(Bundle state) {
		mControlView.restoreState(state);
	}

	public void setTitle(String title) {
		mControlView.setTitle(title);
	}

	public PDFDocument getDocument() {
		return mReaderView.getDocument();
	}

	/**
	 * Sets the page of given index to current page
	 * 
	 * @param pageIdx
	 *            The index of the page you want to display. The index is
	 *            zero-based.
	 */
	public void goToPage(int pageIdx) {
		mReaderView.goToPage(pageIdx);
	}

	/**
	 * Returns The index of current display page. The index is zero-based.
	 */
	public int getPageIdx() {
		return mReaderView.getPageIdx();
	}

	/**
	 * Searches string and displays the hit view on the page if there is a
	 * matching element.
	 * 
	 * @param keyword
	 *            The string you want to find
	 * @param direction
	 *            The direction of next page (1 : front , -1 : reverse)
	 */
	public void search(String keyword, int direction) {
		mReaderView.search(keyword, direction);
	}

	/**
	 * Stops the current searching and deletes the hit views on any page view.
	 */
	public void stopSearch() {
		mReaderView.stopSearch();
	}

	/**
	 * Refreshes the current layout.
	 */
	public void refreshLayout() {
		if (mReaderView != null) {
			mReaderView.refreshLayout();
		}

		if (mControlView != null) {
			mControlView.refreshLayout();
		}
	}

	/**
	 * Enable / Disable Annotation Menu From Control Bar.
	 * 
	 * @param enable
	 *            true is show the Annotation Menu, false is hide the Annotation
	 *            Menu.
	 */
	public void enableAnnotationMenu(boolean enable) {
		mControlView.showEditButton(enable);
	}

	/**
	 * enable annotation feature of specified type.
	 * 
	 * @param type
	 *            Type of annotation in string form. Eg. "INK:NOTE:LINK"
	 * @param enable
	 *            true is enable the annotation, false is disable the annotation
	 */
	public void enableAnnotationFeature(String types, boolean enable) {
		mControlView.enableAnnotFeature(types, enable);
		mReaderView.enableAnnotFeature(types, enable);
	}

	/**
	 * Sets the display mode of the list and changes the screen. (BILATERAL and
	 * THUMBNAIL mode not support yet)
	 * 
	 * @param mode
	 *            The mode value is one of the values available in the
	 *            "PageDisplayMode" enumeration.
	 */
	public void setPageDisplayMode(PageDisplayMode mode) {
		
		switch (mode) {
		case HORIZONTAL:
			mControlView.setHorizontalMode();
			break;
		case VERTICAL:
			mControlView.setVerticalMode();
			break;
		case CONTINUOS:
			mControlView.setContinuosMode();
			break;
		case BILATERAL_VERTICAL:
			mControlView.setBilateralVerticalMode();
			break;
		case BILATERAL_HORIZONTAL:
			mControlView.setBilateralHorizontalMode();
			break;
		case BILATERAL_REALISTIC:
			mControlView.setBilateralRealisticMode();
			break;
		case THUMBNAIL:
			mControlView.setThumbnailMode();
			break;
		case REALISTIC:
			mControlView.setRealisticMode();
			break;
		}
	}

	/**
	 * Sets the zoom level of double tab. 1 means 100 %
	 * 
	 * @param scale
	 */
	public void setDoubleTapZoomLevel(double scale) {
		PropertyManager.setDoubleTapZoomLevel(scale);
	}

	public void flatten() {
		mReaderView.flatten();
	}

	public void save() {
		mReaderView.save();
	}

	public String saveAsFile(String filePath) {
		return mReaderView.saveAsFile(filePath);
	}

	// / @cond DOXYGEN_HIDE

	/**
	 * Is called when a PDF document load finish.
	 * 
	 * @param state
	 *            {@link OPEN_STATE} The state value is PDF document load
	 *            result.
	 */
	@SuppressLint("InflateParams")
	@Override
	public void onLoadFinish(DocumentState.OPEN state) {
		if (state == OPEN.SUCCESS) {
			mControlView.init(mAct);

			RelativeLayout layout = new RelativeLayout(mAct);
			layout.addView(mReaderView);
			layout.addView(mControlView);

			mAct.setContentView(layout);

			CoordConverter.initCoordConverter(mAct, mReaderView);
            SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(mAct);
            String klasse = pref.getString("example_list", "");
			String lehrer = pref.getString("lehrer_liste", "");
			Boolean lehrertrue = pref.getBoolean("LehrerPref", false);
			if (lehrertrue) {
				goToPage(Integer.parseInt(lehrer));
			}
			if (!lehrertrue) {
				goToPage(Integer.parseInt(klasse));
			}
		} else if (state == OPEN.WRONG_PASSWD) {
			LayoutInflater layoutInflater = LayoutInflater.from(mAct);
            View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mAct);

            alertDialogBuilder.setView(promptView);
            final TextView tag = (TextView) promptView.findViewById(R.id.input_dialog_tag);
            tag.setText("PASSWORD:");
            final EditText input = (EditText) promptView.findViewById(R.id.input_dialog_text_field);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            input.setTransformationMethod(PasswordTransformationMethod.getInstance());
            final InputMethodManager imm = (InputMethodManager) mAct.getSystemService(Service.INPUT_METHOD_SERVICE);
            imm.showSoftInput(input, 0);
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		                        public void onClick(DialogInterface dialog, int id) {
		                        	imm.hideSoftInputFromInputMethod(input.getWindowToken(), 0);
		                        	if (null != mFilePath) {
		                        		mReaderView.openFile(mFilePath, input.getText().toString().trim());
		                        	} else if (null != mFileData) {
		                        		mReaderView.openData(mFileData, mFileData.length, input.getText().toString().trim());
		                        	}
		                        }
		                   	})
		            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int id) {
		                    dialog.cancel();
		                    imm.hideSoftInputFromInputMethod(input.getWindowToken(), 0);
		                }
		            });

            AlertDialog alertD = alertDialogBuilder.create();
            alertD.show();
		}

		if (mListener != null) {
			mListener.onLoadFinish(state);
		}
	}

	/**
	 * It is called when the PDF document search finish.
	 * 
	 * @param success
	 *            returns true if the search is successful. otherwise return
	 *            false
	 */
	@Override
	public void onSearchFinish(boolean success) {
		if (success) {
			if (mReaderView.getPageDisplayMode() == PageDisplayMode.THUMBNAIL) {
				onChangeDisplayMode(PageDisplayMode.HORIZONTAL);
			}
		}
	}

	/**
	 * It is called when the page move the event occurred.
	 * 
	 * @param pageIdx
	 *            target page index
	 * @param pageCount
	 *            total page count
	 */
	@Override
	public void onGoToPage(int pageIdx, int pageCount) {
		mControlView.updatePageNumber(pageIdx, pageCount);
	}

	/**
	 * It is called when the screen touch the event occurred.
	 * 
	 * @param e
	 *            event info object
	 */
	@Override
	public void onSingleTapUp(MotionEvent e) {
		mControlView.toggleControlTabBar();
	}

	/**
	 * It is called when the screen double touch the event occurred.
	 * 
	 * @param e
	 *            event info object
	 */
	@Override
	public void onDoubleTapUp(MotionEvent e) {

	}

	/**
	 * It is called when the scroll the event occurred.
	 */
	@Override
	public void onScroll() {
		mControlView.hideTopMenu();
	}
	
	/**
	 * It is change the page display mode.
	 * 
	 * @param mode
	 *            {@link PageDisplayMode}
	 */
	@Override
	public void onChangeDisplayMode(PageDisplayMode mode) {
		if (mode == PageDisplayMode.HORIZONTAL) {
			mControlView.setHorizontalMode();
		} else if (mode == PageDisplayMode.VERTICAL) {
			mControlView.setVerticalMode();
		} else if (mode == PageDisplayMode.CONTINUOS) {
			mControlView.setContinuosMode();
		} else if (mode == PageDisplayMode.BILATERAL_VERTICAL) {
			mControlView.setBilateralVerticalMode();
		} else if (mode == PageDisplayMode.BILATERAL_HORIZONTAL) {
			mControlView.setBilateralHorizontalMode();
		} else if (mode == PageDisplayMode.BILATERAL_REALISTIC) {
			mControlView.setBilateralRealisticMode();
		} else if (mode == PageDisplayMode.THUMBNAIL) {
			mControlView.setThumbnailMode();
		}
	}

	/**
	 * It is called when the change the zoom level.
	 * 
	 * @param zoomLevel
	 * 			  changed level value
	 */
	@Override
	public void onChangeZoom(double zoomLevel) {
		
	}
	
	public boolean setEncrypt(String userpass, String ownerpass, int perm)
	{
		return mReaderView.setEncrypt(userpass, ownerpass, perm);
	}

	public String getPageText(int pageIdx)
	{
		return mReaderView.getPageText(pageIdx);
	}

	public void clear() {
		mReaderView.clear();
		mFileData = null;
		mFilePath = null;
	}

	// / @endcond
}
