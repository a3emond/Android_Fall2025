package pro.aedev.parcelabletestapp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class TestObject implements Parcelable {

    // Height and Width fields
    private int height;
    private int width;

    // Constructor
    public TestObject(int height, int width) {
        if (height < 0 || width < 0) {
            throw new IllegalArgumentException("Height and Width cannot be negative");
        }
        this.height = height;
        this.width = width;
    }

    // getter and setter methods for height and width
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        if (height < 0) {
            throw new IllegalArgumentException("Height cannot be negative");
        }
        this.height = height;
    }
    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        if (width < 0) {
            throw new IllegalArgumentException("Width cannot be negative");
        }
        this.width = width;
    }


    // Parcelable implementation

    protected TestObject(Parcel in) {
        height = in.readInt();
        width = in.readInt();
    }

    public static final Creator<TestObject> CREATOR = new Creator<TestObject>() {
        @Override
        public TestObject createFromParcel(Parcel in) {
            return new TestObject(in);
        }

        @Override
        public TestObject[] newArray(int size) {
            return new TestObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(height);
        dest.writeInt(width);
    }
}
