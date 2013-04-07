package info.guardianproject.justpayphone.app.popups;

import java.util.List;
import java.util.Vector;

import info.guardianproject.justpayphone.R;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class KeypadPopup extends Popup implements OnClickListener {
	Object context;
	Activity a;
	
	Button commit, clear;
	TextView output;
	GridView numbers;
	int quantifyingStringResource;
	protected String currentNum = String.valueOf(0);
	
	List<Integer> num = new Vector<Integer>();
	
	public KeypadPopup(Activity a, final Object context, int quantifyingStringResource) {
		super(a, R.layout.popup_keypad);
		this.context = context;
		this.a = a;
		this.quantifyingStringResource = quantifyingStringResource; 
		
		for(int n=1; n<10; n++) {
			num.add(n);
		}
		num.add(0);
		
		commit = (Button) layout.findViewById(R.id.keypad_popup_commit);
		commit.setOnClickListener(this);
		
		clear = (Button) layout.findViewById(R.id.keypad_popup_clear);
		clear.setOnClickListener(this);
		
		output = (TextView) layout.findViewById(R.id.keypad_popup_output);
		
		numbers = (GridView) layout.findViewById(R.id.keypad_popup_numbers);
		numbers.setAdapter(new KeypadGridAdapter());		
		updateOutput();
		Show();
	}
	
	public void updateOutput() {
		output.setText(a.getString(quantifyingStringResource, Integer.parseInt(currentNum)));
	}

	@Override
	public void onClick(View v) {
		if(v == commit) {
			cancel();
		} else if(v == clear) {
			currentNum = String.valueOf(0);
			updateOutput();
		}
		
	}

	class KeypadGridAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return num.size();
		}

		@Override
		public Object getItem(int position) {
			return num.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(a).inflate(R.layout.popup_keypad_numbers, null);
			
			Button n = (Button) convertView.findViewById(R.id.popup_keypad_number);
			n.setText(String.valueOf(num.get(position)));
			n.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					currentNum += String.valueOf(num.get(position));
					updateOutput();
				}
			});
			
			return convertView;
		}
		
	}
}
