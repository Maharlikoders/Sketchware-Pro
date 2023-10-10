package mod.elfilibustero.sketch.beans;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.sketchware.pro.R;

import java.util.ArrayList;
import java.util.List;

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

    @Expose
    public List<DependencyBean> dependencies;
    @Expose
    public List<LibrariesBean> libraries;

    public ExternalLibraryBean() {
        dependencies = new ArrayList<>();
        libraries = new ArrayList<>();
    }

    public ExternalLibraryBean(Parcel parcel) {
        dependencies = (List<DependencyBean>) parcel.readSerializable();
        libraries = (List<LibrariesBean>) parcel.readSerializable();
    }

    public List<DependencyBean> getDependencies() {
        return dependencies;
    }

    public List<LibrariesBean> getLibraries() {
        return libraries;
    }

    public static Parcelable.Creator<ExternalLibraryBean> getCreator() {
        return CREATOR;
    }

    public void copy(ExternalLibraryBean externalLibraryBean) {
        dependencies = externalLibraryBean.dependencies;
        libraries = externalLibraryBean.libraries;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void print() {
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(dependencies);
        parcel.writeSerializable(libraries);
    }

    @Override
    @NonNull
    public ExternalLibraryBean clone() {
        ExternalLibraryBean externalLibraryBean = new ExternalLibraryBean();
        externalLibraryBean.copy(this);
        return externalLibraryBean;
    }
}
