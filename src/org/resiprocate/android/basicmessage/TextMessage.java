package org.resiprocate.android.basicmessage;

import android.os.Parcel;
import android.os.Parcelable;

public final class TextMessage implements Parcelable {
	String sender;
	String body;

    public static final Parcelable.Creator<TextMessage> CREATOR = new Parcelable.Creator<TextMessage>() {
        public TextMessage createFromParcel(Parcel in) {
            return new TextMessage(in);
        }

        public TextMessage[] newArray(int size) {
            return new TextMessage[size];
        }
    };

    public TextMessage() {
    }

    private TextMessage(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(sender);
        out.writeString(body);
    }

    public void readFromParcel(Parcel in) {
    	sender = in.readString();
    	body = in.readString();
    }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}
