package com.android.keyboard.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.keyboard.R;
import com.android.keyboard.utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import static com.android.keyboard.utils.ParseConstants.TYPE_TEXT;

public class TextActivity extends Activity {
    private static final String TAG = TextActivity.class.getSimpleName();
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected List<ParseUser> mFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                setProgressBarIndeterminateVisibility(false);

                if (e == null) {
                    mFriends = friends;
                }
                else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(TextActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
    protected ArrayList<String> getRecipientIds() {
        ArrayList<String> recipientIds = new ArrayList<String>();
        for (int i = 0; i < mFriends.size(); i++) {
            //if (getListView().isItemChecked(i)) {
                recipientIds.add(mFriends.get(i).getObjectId());
            //}
        }
        return recipientIds;
    }
    public void onSendMessage(View view) {
        EditText text = (EditText)findViewById(R.id.textMessage);
        String msg = text.getText().toString();
        Log.d(TAG, "Sending: " + msg);
        ParseObject message = createMessage(msg);
        if (message == null) {
            // error
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Error sending msg")
                    .setTitle("Error")
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            send(message);
            finish();
        }
    }
    protected ParseObject createMessage(String msg) {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientIds());
        message.put(ParseConstants.KEY_FILE_TYPE, TYPE_TEXT);
//        ParseFile file = new ParseFile(new File(""));
//        message.put(ParseConstants.KEY_FILE, file);
        message.put(ParseConstants.KEY_MESSAGE, msg);
        return message;
    }
    protected void send(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // success!
                    Toast.makeText(TextActivity.this, R.string.success_message, Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(TextActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
