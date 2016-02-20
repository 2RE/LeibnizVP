package com.epapyrus.plugpdf;

import java.util.List;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.epapyrus.plugpdf.core.OutlineItem;

public class OutlineAdapter extends BaseAdapter {
	private final List<OutlineItem> mItems;
	private final LayoutInflater mInflater;
	
	private final OutlintEditListener mListener; 
	
	interface OutlintEditListener {
		void onClickToRemove(List<OutlineItem> list, int index);
		void onClickToAdd(List<OutlineItem> list, int index, String title, boolean isChild);
	}
	
	/*public void setOutlineEditListener(OutlintEditListener outlintEditListener) {
		mListener = outlintEditListener;
	}
	*/
	private boolean enableAddBtn;
	private boolean enableRemoveBtn;
	
	private final int MAX_DEPS = 8;

	public OutlineAdapter(LayoutInflater inflater, List<OutlineItem> items, OutlintEditListener outlintEditListener) {
		mInflater = inflater;
		mItems = items;
		mListener = outlintEditListener;
	}
	
	public int getCount() {
		return mItems.size();
	}

	public Object getItem(int arg0) {
		return mItems.get(arg0);
	}

	public long getItemId(int arg0) {
		return 0;
	}
	
	public void setEnableAdd(boolean enable) {
		enableAddBtn = enable;
	}
	
	public void setEnableRemove(boolean enable) {
		enableRemoveBtn = enable;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View v;
		if (convertView == null) {
			v = mInflater.inflate(R.layout.panel_outline_item, null);
		} else {
			v = convertView;
		}
		final Context context = v.getContext();

		int deps = mItems.get(position).getDeps();
		if (deps > MAX_DEPS) {
			deps = MAX_DEPS;
		}

		String space = "";
		for (int i = 0; i < deps; i++) {
			space += "   ";
		}

		String title = space + mItems.get(position).getTitle();
		String pageNumber = String.valueOf(mItems.get(position).getPageIdx() + 1);

		((TextView) v.findViewById(R.id.title)).setText(title);
		((TextView) v.findViewById(R.id.page)).setText(pageNumber);
		
		if (enableAddBtn) {
			
			((Button) v.findViewById(R.id.add_btn)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					LayoutInflater layoutInflater = LayoutInflater.from(context);
	                View promptView = layoutInflater.inflate(R.layout.outline_dialog, null);
	                
	                final EditText input = (EditText) promptView.findViewById(R.id.outline_dialog_text_field);
	                final CheckBox childCheckBox = (CheckBox) promptView.findViewById(R.id.outline_dialog_child_check_box);
	                final InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
	                imm.showSoftInput(input, InputMethodManager.SHOW_FORCED);

	                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
	                alertDialogBuilder.setView(promptView);
	                alertDialogBuilder
	                        .setCancelable(true)
	                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                                    public void onClick(DialogInterface dialog, int id) {

	                                    	mListener.onClickToAdd(mItems, position + 1, input.getText().toString(), childCheckBox.isChecked());
	                                    	imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
	                                    }
	                                })
	                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                                    public void onClick(DialogInterface dialog, int id) {
	                                        dialog.cancel();
	                                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
	                                    }
	                                });

	                AlertDialog alertD = alertDialogBuilder.create();
	                alertD.show();
					
				}
			});
		
		} else {
			
			((Button) v.findViewById(R.id.add_btn)).setVisibility(View.GONE);
			
		}
		
		if (enableRemoveBtn) {
			
			((Button) v.findViewById(R.id.remove_btn)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mListener.onClickToRemove(mItems, position);
				}
			});
			
		} else {
				
			((Button) v.findViewById(R.id.remove_btn)).setVisibility(View.GONE);
			
		}

		return v;
	}
}
