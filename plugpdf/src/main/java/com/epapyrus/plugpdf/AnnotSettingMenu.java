package com.epapyrus.plugpdf;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.epapyrus.plugpdf.core.annotation.tool.AnnotSetting;
import com.epapyrus.plugpdf.core.annotation.tool.BaseAnnotTool.AnnotToolType;

public class AnnotSettingMenu {
	private Context mContext;
	private LayoutInflater mInflater;
	private PopupWindow mPopupWindow;

	private static final String PX = "px";
	private static final String PERCENT = "%";
	private AnnotToolType mAnnotType;

	public enum ColorType {
		BLACK(0), RED(1), YELLOW(2), GREEN(3), BLUE(4), WHITE(5);

		private int mValue;

		ColorType(int value) {
			mValue = value;
		}

		public int value() {
			return mValue;
		}
	}

	public AnnotSettingMenu(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private int getSpinnerPos(int color) {
		switch (color) {
		case 0x000000:
			return 0;
		case 0xFF0000:
			return 1;
		case 0xFFFF00:
			return 2;
		case 0x00FF00:
			return 3;
		case 0x0000FF:
			return 4;
		case 0xFFFFFF:
			return 5;
		}
		return 0;
	}

	private int getColorFromSpinner(int pos) {
		switch (pos) {
		case 0:
			return 0x000000;
		case 1:
			return 0xFF0000;
		case 2:
			return 0xFFFF00;
		case 3:
			return 0x00FF00;
		case 4:
			return 0x0000FF;
		case 5:
			return 0xFFFFFF;
		}
		return 0x000000;
	}

	private void createPopupWindow(AnnotToolType type) {
		View menuView = mInflater.inflate(R.layout.annot_setting, null);

		mPopupWindow = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(mContext
				.getResources()));

		final AnnotSetting setting = AnnotSetting.instance();
		// line color
		Spinner lineColorSpinner = (Spinner) menuView
				.findViewById(R.id.annot_line_color_value);
		lineColorSpinner.setSelection(getSpinnerPos(setting.getLineColor(mAnnotType)));
		lineColorSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						setting.setLineColor(getColorFromSpinner(position), mAnnotType);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

		// fill color
		Spinner fillColorSpinner = (Spinner) menuView
				.findViewById(R.id.annot_fill_color_value);
		fillColorSpinner.setSelection(getSpinnerPos(setting.getFillColor(mAnnotType)));
		fillColorSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						setting.setFillColor(getColorFromSpinner(position), mAnnotType);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});
		
		// line opacity
		final TextView lineOpacityValueDisplay = (TextView) menuView
				.findViewById(R.id.annot_opacity_value_display);
		lineOpacityValueDisplay.setText(Integer.toString((int) (AnnotSetting
				.instance().getOpacity(mAnnotType) / 255.0f * 100)) + PERCENT);

		SeekBar lineOpacityValue = (SeekBar) menuView
				.findViewById(R.id.annot_opacity_value);
		lineOpacityValue.setProgress(setting.getOpacity(mAnnotType));
		lineOpacityValue
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						lineOpacityValueDisplay.setText(Integer
								.toString((int) (progress / 255.0f * 100))
								+ PERCENT);
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						setting.setOpacity(seekBar.getProgress(), mAnnotType);
						lineOpacityValueDisplay.setText(Integer
								.toString((int) (setting.getOpacity(mAnnotType) / 255.0f * 100))
								+ PERCENT);
					}
				});

		// line width
		final TextView lineWidthValueDisplay = (TextView) menuView
				.findViewById(R.id.annot_line_width_value_display);
		lineWidthValueDisplay.setText(Integer.toString(setting.getLineWidth(mAnnotType))
				+ PX);

		SeekBar lineWidthValue = (SeekBar) menuView
				.findViewById(R.id.annot_line_width_value);
		lineWidthValue.setProgress(setting.getLineWidth(mAnnotType));
		lineWidthValue
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						lineWidthValueDisplay.setText(Integer
								.toString(progress) + PX);
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						setting.setLineWidth(seekBar.getProgress(), mAnnotType);
						lineWidthValueDisplay.setText(Integer.toString(setting
								.getLineWidth(mAnnotType)) + PX);
					}
				});

		// line straight
		final CheckBox straight = (CheckBox) menuView
				.findViewById(R.id.annot_line_straight);
		straight.setChecked(setting.getInkLineStraight());
		straight.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				straight.setChecked(isChecked);
				setting.setInkLineStraight(isChecked);
			}
		});
		
		// line squiggly
		final CheckBox squiggly = (CheckBox) menuView
				.findViewById(R.id.annot_line_squiggly);
		squiggly.setChecked(setting.isSquiggly());
		squiggly.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				squiggly.setChecked(isChecked);
				setting.setSquiggly(isChecked);
			}
		});

		switch (mAnnotType) {
		case INK:
			menuView.findViewById(R.id.annot_fill_color_title).setVisibility(View.GONE);
			menuView.findViewById(R.id.annot_fill_color_value).setVisibility(View.GONE);
			menuView.findViewById(R.id.annot_line_squiggly).setVisibility(View.GONE);
			break;
		case STRIKEOUT:
		case HIGHLIGHT:
			menuView.findViewById(R.id.annot_line_squiggly).setVisibility(View.GONE);
		case UNDERLINE:
			menuView.findViewById(R.id.annot_line_straight).setVisibility(View.GONE);
			menuView.findViewById(R.id.annot_fill_color_title).setVisibility(View.GONE);
			menuView.findViewById(R.id.annot_fill_color_value).setVisibility(View.GONE);
			menuView.findViewById(R.id.annot_opacity_title).setVisibility(View.GONE);
			menuView.findViewById(R.id.annot_opacity_value).setVisibility(View.GONE);
			menuView.findViewById(R.id.annot_opacity_value_display).setVisibility(View.GONE);
			menuView.findViewById(R.id.annot_line_width_title).setVisibility(View.GONE);
			menuView.findViewById(R.id.annot_line_width_value).setVisibility(View.GONE);
			menuView.findViewById(R.id.annot_line_width_value_display).setVisibility(View.GONE);
			break;
		case SQUARE:
		case CIRCLE:
			menuView.findViewById(R.id.annot_line_straight).setVisibility(View.GONE);
			menuView.findViewById(R.id.annot_line_squiggly).setVisibility(View.GONE);
			((TextView) menuView.findViewById(R.id.annot_line_color_title)).setText("line color");
		default:
			break;
		}
	}

	public synchronized void show(final View anchor, final int x, final int y, AnnotToolType type) {

		mAnnotType = type;
		
		createPopupWindow(mAnnotType);
		
		mPopupWindow.showAtLocation(anchor, Gravity.CENTER, x, y);
	}

	public synchronized void close() {
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
		}
	}

}
