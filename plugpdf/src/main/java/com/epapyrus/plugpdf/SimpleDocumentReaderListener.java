/****************************************************************************
 **
 ** Copyright (C) 2013 ePapyrus, Inc.
 ** All rights reserved.
 **
 ** This file is part of PlugPDF for Android project.
 **
 ****************************************************************************/

package com.epapyrus.plugpdf;

import android.content.Context;

import com.epapyrus.plugpdf.core.viewer.DocumentState;

/**
 * The listener to be implemented to receive event notifications on completion
 * of PDF document loading on a {@link SimpleDocumentReader}.
 * 
 */
public interface SimpleDocumentReaderListener {
	/**
	 * Notifies that a PDF document has finished loading.
	 * 
	 * @param state
	 *            {@link DocumentState.OPEN}
	 */
	 void onLoadFinish(DocumentState.OPEN state);
}