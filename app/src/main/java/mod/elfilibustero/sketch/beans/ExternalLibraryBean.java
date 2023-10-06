package mod.elfilibustero.sketch.beans;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.sketchware.pro.R;

import java.util.ArrayList;

public class ExternalLibraryBean implements Parcelable {

    public static final Parcelable.Creator<ExternalLibraryBean> CREATOR = new Parcelable.Creator<>() {
        @Override
        public ExternalLibraryBean createFromParcel(Parcel source) {
            return new ExternalLibraryBean(source);
        }

        @Override
        public ExternalLibraryBean[] newArray(int size) {
            return new ExternalLibraryBean[size];
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

    public ExternalLibraryBean() {
        useYn = LIB_USE_N;
        name = "";
        dependency = "";
    }

    public ExternalLibraryBean(String newName) {
        useYn = LIB_USE_N;
        name = newName;
        dependency = "";
    }

    public ExternalLibraryBean(Parcel parcel) {
        useYn = parcel.readString();
        name = parcel.readString();
        dependency = parcel.readString();
    }

    public String getName() {
        return name;
    }

    public static Parcelable.Creator<ExternalLibraryBean> getCreator() {
        return CREATOR;
    }

    public void copy(ExternalLibraryBean externalLibraryBean) {
        useYn = externalLibraryBean.useYn;
        name = externalLibraryBean.name;
        dependency = externalLibraryBean.dependency;
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
    public ExternalLibraryBean clone() {
        ExternalLibraryBean externalLibraryBean = new ExternalLibraryBean();
        externalLibraryBean.copy(this);
        return externalLibraryBean;
    }
}
