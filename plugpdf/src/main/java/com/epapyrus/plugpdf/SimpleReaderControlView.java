/****************************************************************************
 **
 ** Copyright (C) 2013 ePapyrus, Inc.
 ** All rights reserved.
 **
 ** This file is part of PlugPDF for Android project.
 **
 ****************************************************************************/

package com.epapyrus.plugpdf;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.epapyrus.plugpdf.SimpleReaderControlPanel.PanelType;
import com.epapyrus.plugpdf.core.BaseReaderControl;
import com.epapyrus.plugpdf.core.PlugPDF;
import com.epapyrus.plugpdf.core.PlugPDFUtility;
import com.epapyrus.plugpdf.core.annotation.tool.BaseAnnotTool.AnnotToolType;
import com.epapyrus.plugpdf.core.gesture.BaseGestureProcessor.GestureType;
import com.epapyrus.plugpdf.core.viewer.BasePlugPDFDisplay.PageDisplayMode;

/*
 * Menu control built-in class
 * 
 * This is the UI processing class with operations to be displayed to the user.
 */
public class SimpleReaderControlView extends RelativeLayout {

	private BaseReaderControl mController;
	private SimpleReaderControlPanel mControlPanel;
	private AnnotSettingMenu mAnnotSettingMenu;
	Activity mAct;

	private boolean mButtonsVisible;
	private boolean mTopBarIsSearch;

	private ViewFlipper mTopBarSwitcher;
	private TextView mPageNumberView;
	private ImageView mPageThumbnail;
	private SeekBar mPageSlider;
	private Button mSearchButton;
	private Button mOutlineButton;
	private Button mEditButton;

	// search
	private Button mSearchCancelButton;
	private EditText mSearchText;
	private Button mSearchBack;
	private Button mSearchFwd;

	// edit
	private Button mEditCancelButton;
	private Button mEditNoteButton;
	private Button mEditInkButton;
	private Button mEditEraserButton;
	private Button mEditHighlightButton;
	private Button mEditUnderlineButton;
	private Button mEditStrikeoutButton;

	private Button mRotateButton;
	private Button mPageDisplayModeButton;
	private Button mBrightnessButton;
	private TextView mTitle;
	
	private int mPageIdx;
	private Bitmap mBitmap = null;

	public SimpleReaderControlView(Context context) {
		super(context);
	}

	public SimpleReaderControlView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void createUILayout(BaseReaderControl controller) {
		
		mController = controller;
		
		mTitle = (TextView) findViewById(R.id.rc_title);
		mTopBarSwitcher = (ViewFlipper) findViewById(R.id.flipper);
		mPageNumberView = (TextView) findViewById(R.id.rc_page_number);
		mPageThumbnail = (ImageView) findViewById(R.id.rc_page_thumbnail);
		mPageSlider = (SeekBar) findViewById(R.id.rc_page_slider);
		mSearchButton = (Button) findViewById(R.id.rc_search);
		mEditButton = (Button) findViewById(R.id.rc_edit);
		mSearchCancelButton = (Button) findViewById(R.id.rc_search_cancel);
		mSearchText = (EditText) findViewById(R.id.rc_search_text);
		mSearchBack = (Button) findViewById(R.id.rc_search_back);
		mSearchFwd = (Button) findViewById(R.id.rc_search_forward);
		mEditCancelButton = (Button) findViewById(R.id.rc_edit_cancel);
		mEditNoteButton = (Button) findViewById(R.id.rc_edit_note);
		mEditInkButton = (Button) findViewById(R.id.rc_edit_ink);
		mEditEraserButton = (Button) findViewById(R.id.rc_edit_eraser);
		mEditHighlightButton = (Button) findViewById(R.id.rc_edit_tm_highlight);
		mEditUnderlineButton = (Button) findViewById(R.id.rc_edit_tm_underline);
		mEditStrikeoutButton = (Button) findViewById(R.id.rc_edit_tm_strikeout);
		mOutlineButton = (Button) findViewById(R.id.rc_outline);
		mRotateButton = (Button) findViewById(R.id.rc_rotate);
		mPageDisplayModeButton = (Button) findViewById(R.id.rc_page_display_mode);
		mBrightnessButton = (Button) findViewById(R.id.rc_brightness);
		
	}

	public void init(Activity act) {
        mEditButton.setVisibility(View.GONE);
        mRotateButton.setVisibility(View.GONE);
        mSearchButton.setVisibility(View.GONE);
        mOutlineButton.setVisibility(View.GONE);
		mBrightnessButton.setVisibility(View.GONE);
        mPageDisplayModeButton.setVisibility(View.GONE);
		mAct = act;

		mAnnotSettingMenu = new AnnotSettingMenu(getContext());
		
		mControlPanel = new SimpleReaderControlPanel(getContext(), this,
				mController);
		showOutlineButton(true);
		
		mTopBarSwitcher.setVisibility(View.INVISIBLE);
		mPageThumbnail.setVisibility(View.INVISIBLE);
		mPageNumberView.setVisibility(View.INVISIBLE);
		mPageSlider.setVisibility(View.INVISIBLE);


		
		mPageSlider
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					public void onStopTrackingTouch(SeekBar seekBar) {
						mController.goToPage(seekBar.getProgress());
					}

					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						updatePageNumber(seekBar.getProgress() + 1,
								seekBar.getMax() + 1);
					}
				});

		mSearchButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				searchModeOn();
			}
		});
		
		if (mController.canModifyAnnot()) {
			mEditButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					editModeOn();
				}
			});
		} else {
			mEditButton.setVisibility(GONE);
		}
		

		mSearchCancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				searchModeOff();
			}
		});

		mSearchBack.setEnabled(false);
		mSearchFwd.setEnabled(false);

		mSearchText.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				boolean haveText = s.toString().length() > 0;
				mSearchBack.setEnabled(haveText);
				mSearchFwd.setEnabled(haveText);

				mController.resetSearchInfo();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});

		mSearchText
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_NEXT
								|| actionId == EditorInfo.IME_ACTION_DONE) {
							mController.search(
									mSearchText.getText().toString(), 1);
						}
						return false;
					}
				});

		mSearchBack.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mController.search(mSearchText.getText().toString(), -1);
			}
		});
		mSearchFwd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mController.search(mSearchText.getText().toString(), 1);
			}
		});

		mEditCancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editModeOff();
			}
		});

		mEditNoteButton.setOnClickListener(mEditButtonClickHandler);
		mEditInkButton.setOnClickListener(mEditButtonClickHandler);
		mEditInkButton.setOnLongClickListener(mEditButtonClickHandler);
		mEditEraserButton.setOnClickListener(mEditButtonClickHandler);
		mEditHighlightButton.setOnClickListener(mEditButtonClickHandler);
		mEditHighlightButton.setOnLongClickListener(mEditButtonClickHandler);
		mEditUnderlineButton.setOnClickListener(mEditButtonClickHandler);
		mEditUnderlineButton.setOnLongClickListener(mEditButtonClickHandler);
		mEditStrikeoutButton.setOnClickListener(mEditButtonClickHandler);
		mEditStrikeoutButton.setOnLongClickListener(mEditButtonClickHandler);

		mRotateButton.setTag(0);
		mRotateButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mRotateButton.getTag().equals(0)) {
					mRotateButton.setTag(1);
					mRotateButton
							.setBackgroundResource(R.drawable.st_btn_rotate_lock);
					PlugPDFUtility.setRotationLock(mAct, true);
				} else {
					mRotateButton.setTag(0);
					mRotateButton
							.setBackgroundResource(R.drawable.st_btn_rotate);
					PlugPDFUtility.setRotationLock(mAct, false);
				}
			}
		});

		mPageDisplayModeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				 mControlPanel.show(PanelType.DISPLAYMODE, v);
			}
		});

		mBrightnessButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mControlPanel.show(PanelType.BRIGHTNESS, v);
			}
		});

	}

	public void setTitle(String title) {
		mTitle.setText(title);
	}

	// The top menu show / hide
	public void toggleControlTabBar() {
		if (!mButtonsVisible)
			show();
		else
			hideTopMenu();
	}

	// refresh layout.
	public void refreshLayout() {
		mControlPanel.refreshLayout();
	}

	// The top menu show.
	public void show() {
		if (mButtonsVisible)
			return;

		mButtonsVisible = true;

		if (mTopBarIsSearch) {
			mSearchText.requestFocus();
			showKeyboard();
		}

		Animation anim = new TranslateAnimation(0, 0,
				-mTopBarSwitcher.getHeight(), 0);
		anim.setDuration(200);
		anim.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
				mTopBarSwitcher.setVisibility(View.VISIBLE);
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
			}
		});
		mTopBarSwitcher.startAnimation(anim);

		anim = new TranslateAnimation(0, 0, mPageSlider.getHeight(), 0);
		anim.setDuration(200);
		anim.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
				mPageSlider.setVisibility(View.VISIBLE);
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				mPageNumberView.setVisibility(View.VISIBLE);
				mPageThumbnail.setVisibility(View.VISIBLE);
			}
		});
		mPageSlider.startAnimation(anim);
	}

	// The top menu hide.
	public void hideTopMenu() {
		if (!mButtonsVisible)
			return;
		mButtonsVisible = false;

		hideKeyboard();

		Animation anim = new TranslateAnimation(0, 0, 0,
				-mTopBarSwitcher.getHeight());
		anim.setDuration(200);
		anim.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				mTopBarSwitcher.setVisibility(View.INVISIBLE);
			}
		});
		mTopBarSwitcher.startAnimation(anim);

		anim = new TranslateAnimation(0, 0, 0, mPageSlider.getHeight());
		anim.setDuration(200);
		anim.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
				mPageNumberView.setVisibility(View.INVISIBLE);
				mPageThumbnail.setVisibility(View.GONE);
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				mPageSlider.setVisibility(View.INVISIBLE);
			}
		});
		mPageSlider.startAnimation(anim);
	}

	// Page slider update
	public void updatePageNumber(int pageIdx, int pageCount) {
		mPageNumberView.setText(String.format("%d/%d", pageIdx, pageCount));
		mPageSlider.setMax(pageCount - 1);
		mPageSlider.setProgress(pageIdx - 1);
		mPageIdx = pageIdx - 1;
		if (mTopBarIsSearch && !mSearchText.getText().toString().isEmpty()) {
			mController.search(mSearchText.getText().toString(), 0);
		}
		
		android.os.AsyncTask<Void, Void, Void> entrie = new android.os.AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... v) {
				int pageHeight = 200;
				PointF pageSize = mController.getPageSize(mPageIdx);
				int pageWidth = pageHeight * (int)pageSize.x / (int)pageSize.y;
				mBitmap = Bitmap.createBitmap(pageWidth, pageHeight, PlugPDF.bitmapConfig());
				mController.drawPage(mBitmap, mPageIdx, pageWidth, pageHeight, 0, 0, pageWidth, pageHeight);
				return null;
			}

			protected void onPreExecute() {
				mPageThumbnail.setImageBitmap(mBitmap);
			}

			protected void onPostExecute(Void ret) {
				if (mBitmap != null) {
					mPageThumbnail.setImageBitmap(mBitmap);
				} 
			}
		};
		
		entrie.execute();
		
	}

	private void setSearchMode(boolean on) {
		if (on) {
			mTopBarSwitcher.showNext();
		} else {
			mTopBarSwitcher.showPrevious();
		}
	}

	// search mode enable.
	void searchModeOn() {
		mTopBarIsSearch = true;
		mSearchText.requestFocus();
		showKeyboard();
		setSearchMode(true);
	}

	// search mode disable.
	void searchModeOff() {
		mTopBarIsSearch = false;
		hideKeyboard();
		setSearchMode(false);
		mController.resetSearchInfo();
	}

	// keyboard enable.
	void showKeyboard() {
		InputMethodManager imm = (InputMethodManager) getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.showSoftInput(mSearchText, 0);
		}
	}

	// keyboard disable.
	void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(mSearchText.getWindowToken(), 0);
		}
	}

	private void setEditMode(boolean on) {
		if (on) {
			mController.changeGestureType(GestureType.EDIT);
			mTopBarSwitcher.showNext();
			mTopBarSwitcher.showNext();
		} else {
			mController.changeGestureType(GestureType.VIEW);
			mTopBarSwitcher.showPrevious();
			mTopBarSwitcher.showPrevious();
		}
	}

	void editModeOn() {
		mPageSlider.setVisibility(View.VISIBLE);
		setEditMode(true);
	}

	void editModeOff() {
		mPageSlider.setVisibility(View.INVISIBLE);
		mEditNoteButton.setSelected(false);
		mEditInkButton.setSelected(false);
		mEditEraserButton.setSelected(false);
		mEditHighlightButton.setSelected(false);
		mEditStrikeoutButton.setSelected(false);
		mEditUnderlineButton.setSelected(false);
		setEditMode(false);
	}

	public void showEditButton(boolean edit) {
		if (edit) {
			mEditButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					editModeOn();
				}
			});
		} else {
			mEditButton.setVisibility(View.GONE);
		}
	}

	// enable / disable the PDF document Outline button
	void showOutlineButton(boolean show) {
		if (show) {
			mOutlineButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// mListener.showOutline();
					mControlPanel.show(PanelType.OUTLINE, v);
				}
			});
		} else {
			mOutlineButton.setVisibility(View.GONE);
		}
	}

	// save the current state of the Bundle passed.
	public void saveState(Bundle state) {
		if (!mButtonsVisible) {
			state.putBoolean("ButtonsHidden", true);
		}

		if (mTopBarIsSearch) {
			state.putBoolean("SearchMode", true);
		}
	}

	// To restore the state from the Bundle passed.
	public void restoreState(Bundle state) {

		if (state == null || !state.getBoolean("ButtonsHidden", false)) {
			show();
		}

		if (state != null && state.getBoolean("SearchMode", false))
			searchModeOn();
	}

	// set the display view mode in the horizontal direction.
	void setHorizontalMode() {
		mPageDisplayModeButton
				.setBackgroundResource(R.drawable.st_btn_view_mode_horizontal);
		mController.setPageDisplayMode(PageDisplayMode.HORIZONTAL);
	}

	// set the display view mode in the vertical direction by bilateral.
	void setBilateralVerticalMode() {
		mPageDisplayModeButton
				.setBackgroundResource(R.drawable.st_btn_view_mode_bilateral);
		mController.setPageDisplayMode(PageDisplayMode.BILATERAL_VERTICAL);
	}
	
	// set the display view mode in the bilateral direction by bilateral.
	void setBilateralHorizontalMode() {
		mPageDisplayModeButton
				.setBackgroundResource(R.drawable.st_btn_view_mode_bilateral);
		mController.setPageDisplayMode(PageDisplayMode.BILATERAL_HORIZONTAL);
	}
	
	// set the display view mode with page flip effect by bilateral.
	void setBilateralRealisticMode() {
		mPageDisplayModeButton
				.setBackgroundResource(R.drawable.st_btn_view_mode_bilateral);
		mController.setPageDisplayMode(PageDisplayMode.BILATERAL_REALISTIC);
	}

	// set the display view mode in the vertical direction.
	void setVerticalMode() {
		mPageDisplayModeButton
				.setBackgroundResource(R.drawable.st_btn_view_mode_vertical);
		mController.setPageDisplayMode(PageDisplayMode.VERTICAL);
	}

	// set the display view mode in the vertical direction.
	void setContinuosMode() {
		mPageDisplayModeButton
				.setBackgroundResource(R.drawable.st_btn_view_mode_vertical);
		mController.setPageDisplayMode(PageDisplayMode.CONTINUOS);
	}

	// Setting the preview display mode of each page.
	void setThumbnailMode() {
		mPageDisplayModeButton
				.setBackgroundResource(R.drawable.st_btn_view_mode_thumbnail);
		mController.setPageDisplayMode(PageDisplayMode.THUMBNAIL);
		// mEditButton.setVisibility(View.GONE);
		// mSearchButton.setVisibility(View.GONE);
	}
	
	// set the display view mode with page flip effect.
	void setRealisticMode() {
		/*mPageDisplayModeButton
				.setBackgroundResource(R.drawable.st_btn_view_mode_thumbnail);*/
		mController.setPageDisplayMode(PageDisplayMode.REALISTIC);
		// mEditButton.setVisibility(View.GONE);
		// mSearchButton.setVisibility(View.GONE);
	}

	EditButtonClickHandler mEditButtonClickHandler = new EditButtonClickHandler();

	// edit button click event handler
	private class EditButtonClickHandler implements View.OnClickListener,
			View.OnLongClickListener {

		private boolean mLongClickEvented = false;

		@Override
		public boolean onLongClick(View v) {
			mLongClickEvented = true;

			if (v.getId() == mEditInkButton.getId()) {
				mAnnotSettingMenu.show(v, 0, 0, AnnotToolType.INK);
			} else if (v.getId() == mEditHighlightButton.getId()) {
				mAnnotSettingMenu.show(v, 0, 0, AnnotToolType.HIGHLIGHT);
			} else if (v.getId() == mEditUnderlineButton.getId()) {
				mAnnotSettingMenu.show(v, 0, 0, AnnotToolType.UNDERLINE);
			} else if (v.getId() == mEditStrikeoutButton.getId()) {
				mAnnotSettingMenu.show(v, 0, 0, AnnotToolType.STRIKEOUT);
			}
			return false;
		}

		@Override
		public void onClick(View v) {
			if (mLongClickEvented) {
				mLongClickEvented = false;
				return;
			}

			if (v.getId() == mEditInkButton.getId()) {
				mEditEraserButton.setSelected(false);
				mEditNoteButton.setSelected(false);
				mEditHighlightButton.setSelected(false);
				mEditUnderlineButton.setSelected(false);
				mEditStrikeoutButton.setSelected(false);

				mEditInkButton.setSelected(!mEditInkButton.isSelected());
				if (mEditInkButton.isSelected()) {
					mController.setAnnotationTool(AnnotToolType.INK);
				} else {
					mController.setAnnotationTool(AnnotToolType.NONE);
				}

			} else if (v.getId() == mEditEraserButton.getId()) {
				mEditInkButton.setSelected(false);
				mEditNoteButton.setSelected(false);
				mEditHighlightButton.setSelected(false);
				mEditUnderlineButton.setSelected(false);
				mEditStrikeoutButton.setSelected(false);
				
				mEditEraserButton.setSelected(!mEditEraserButton.isSelected());
				if (mEditEraserButton.isSelected()) {
					mController.setAnnotationTool(AnnotToolType.ERASER);
				} else {
					mController.setAnnotationTool(AnnotToolType.NONE);
				}
			} else if (v.getId() == mEditNoteButton.getId()) {
				mEditInkButton.setSelected(false);
				mEditEraserButton.setSelected(false);
				mEditHighlightButton.setSelected(false);
				mEditUnderlineButton.setSelected(false);
				mEditStrikeoutButton.setSelected(false);
				
				mEditNoteButton.setSelected(!mEditNoteButton.isSelected());
				if (mEditNoteButton.isSelected()) {
					mController.setAnnotationTool(AnnotToolType.NOTE);
				} else {
					mController.setAnnotationTool(AnnotToolType.NONE);
				}
			} else if (v.getId() == mEditHighlightButton.getId()) {
				mEditInkButton.setSelected(false);
				mEditEraserButton.setSelected(false);
				mEditNoteButton.setSelected(false);
				mEditUnderlineButton.setSelected(false);
				mEditStrikeoutButton.setSelected(false);
				
				mEditHighlightButton.setSelected(!mEditHighlightButton.isSelected());
				if (mEditHighlightButton.isSelected()) {
					mController.setAnnotationTool(AnnotToolType.HIGHLIGHT);
				} else {
					mController.setAnnotationTool(AnnotToolType.NONE);
				}
			} else if (v.getId() == mEditUnderlineButton.getId()) {
				mEditInkButton.setSelected(false);
				mEditEraserButton.setSelected(false);
				mEditNoteButton.setSelected(false);
				mEditHighlightButton.setSelected(false);
				mEditStrikeoutButton.setSelected(false);
				
				mEditUnderlineButton.setSelected(!mEditUnderlineButton.isSelected());
				if (mEditUnderlineButton.isSelected()) {
					mController.setAnnotationTool(AnnotToolType.UNDERLINE);
				} else {
					mController.setAnnotationTool(AnnotToolType.NONE);
				}
			} else if (v.getId() == mEditStrikeoutButton.getId()) {
				mEditInkButton.setSelected(false);
				mEditEraserButton.setSelected(false);
				mEditNoteButton.setSelected(false);
				mEditHighlightButton.setSelected(false);
				mEditUnderlineButton.setSelected(false);
				
				mEditStrikeoutButton.setSelected(!mEditStrikeoutButton.isSelected());
				if (mEditStrikeoutButton.isSelected()) {
					mController.setAnnotationTool(AnnotToolType.STRIKEOUT);
				} else {
					mController.setAnnotationTool(AnnotToolType.NONE);
				}
			}
		}
	}

	private void enableAnnotButton(Button button, boolean enable) {
		if (enable) {
			button.setVisibility(View.VISIBLE);
		} else {
			button.setVisibility(View.GONE);
		}
	}

	public void enableAnnotFeature(String types, boolean enable) {
		// INK, LINK, NOTE
		String targets[] = types.split(":");
		for (String target : targets) {
			if (target.equals("INK")) {
				enableAnnotButton(mEditInkButton, enable);
			} else if (target.equals("NOTE")) {
				enableAnnotButton(mEditNoteButton, enable);
			} else if (target.equals("LINK")) {
			}
		}

		if (mEditInkButton.getVisibility() == View.VISIBLE
				|| mEditNoteButton.getVisibility() == View.VISIBLE) {
			enableAnnotButton(mEditEraserButton, true);
		} else {
			enableAnnotButton(mEditEraserButton, false);
		}
	}

}
