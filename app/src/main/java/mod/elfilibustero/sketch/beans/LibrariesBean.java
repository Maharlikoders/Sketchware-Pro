package mod.elfilibustero.sketch.beans;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.sketchware.pro.R;

public class LibrariesBean implements Parcelable {

    public static final Parcelable.Creator<LibrariesBean> CREATOR = new Parcelable.Creator<>() {
        @Override
        public LibrariesBean createFromParcel(Parcel source) {
            return new LibrariesBean(source);
        }

        @Override
        public LibrariesBean[] newArray(int size) {
            return new LibrariesBean[size];
        }
    };

    public static final String LIB_USE_N = "N";
    public static final String LIB_USE_Y = "Y";

    @Expose
    public String name;
    @Expose
    public String dependency;
    @Expose
    public String useYn;

    public LibrariesBean() {
        useYn = LIB_USE_N;
        name = "";
        dependency = "";
    }

    public LibrariesBean(String newName) {
        useYn = LIB_USE_N;
        name = newName;
        dependency = "";
    }

    public LibrariesBean(Parcel parcel) {
        useYn = parcel.readString();
        name = parcel.readString();
        dependency = parcel.readString();
    }

    public String getName() {
        return name;
    }

    public static Parcelable.Creator<LibrariesBean> getCreator() {
        return CREATOR;
    }

    public void copy(LibrariesBean librariesBean) {
        useYn = librariesBean.useYn;
        name = librariesBean.name;
        dependency = librariesBean.dependency;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isEnabled() {
        return useYn != null && !useYn.isEmpty() && useYn.equals(LIB_USE_Y);
    }

    public void print() {
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(useYn);
        parcel.writeString(name);
        parcel.writeString(dependency);
    }

    @Override
    @NonNull
    public LibrariesBean clone() {
        LibrariesBean librariesBean = new LibrariesBean();
        librariesBean.copy(this);
        return librariesBean;
    }
}
