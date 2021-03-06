package com.android.keyboard.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.keyboard.R;
import com.android.keyboard.utils.ParseConstants;
import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<ParseObject> {
	
	protected Context mContext;
	protected List<ParseObject> mMessages;
	
	public MessageAdapter(Context context, List<ParseObject> messages) {
		super(context, R.layout.message_item, messages);
		mContext = context;
		mMessages = messages;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
			holder = new ViewHolder();
			holder.iconImageView = (ImageView)convertView.findViewById(R.id.messageIcon);
            holder.nameLabel = (TextView)convertView.findViewById(R.id.senderLabel);
			holder.timeLabel = (TextView)convertView.findViewById(R.id.timeLabel);
			holder.textMessage = (TextView)convertView.findViewById(R.id.textMessage);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		ParseObject message = mMessages.get(position);
		Date createdAt = message.getCreatedAt();
        long now = new Date().getTime();
        String convertedData = DateUtils.getRelativeTimeSpanString(
                createdAt.getTime(),
                now,
                DateUtils.SECOND_IN_MILLIS).toString();

		if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)) {
			holder.iconImageView.setImageResource(R.drawable.ic_picture);
		} else if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_TEXT)) {
			holder.iconImageView.setImageResource(R.drawable.ic_picture);
			holder.textMessage.setText(message.getString(ParseConstants.KEY_MESSAGE));
		} else {
			holder.iconImageView.setImageResource(R.drawable.ic_video);
		}
		holder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));
        holder.timeLabel.setText(convertedData);

        return convertView;
	}
	
	private static class ViewHolder {
		ImageView iconImageView;
        TextView nameLabel;
		TextView timeLabel;
		TextView textMessage;
	}
	
	public void refill(List<ParseObject> messages) {
		mMessages.clear();
		mMessages.addAll(messages);
		notifyDataSetChanged();
	}
}






