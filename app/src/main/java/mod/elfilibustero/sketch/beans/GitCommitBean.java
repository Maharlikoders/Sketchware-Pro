package mod.elfilibustero.sketch.beans;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

import com.sketchware.pro.R;

public class GitCommitBean implements Parcelable {
    public static final Parcelable.Creator<GitCommitBean> CREATOR = new Parcelable.Creator<>() {
        @Override
        public GitCommitBean createFromParcel(Parcel source) {
            return new GitCommitBean(source);
        }

        @Override
        public GitCommitBean[] newArray(int size) {
            return new GitCommitBean[size];
        }
    };

    public static final int COMMIT_ADDED = 0;
    public static final int COMMIT_CHANGED = 1;
    public static final int COMMIT_UNCOMMITTED = 2;
    public static final int COMMIT_REMOVED = 3;
    public static final int COMMIT_MODIFICATON = 4;
    public static final int COMMIT_MISSING = 5;
    public static final int COMMIT_UNTRACKED = 6;
    public static final int COMMIT_UNTRACKED_FOLDER = 7;

    @Expose
    public int type;
    @Expose
    public String name;

    public GitCommitBean() {
        type = -1;
        name = "";
    }

    public GitCommitBean(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public GitCommitBean(Parcel parcel) {
        type = parcel.readInt();
        name = parcel.readString();
    }

    public static Parcelable.Creator<GitCommitBean> getCreator() {
        return CREATOR;
    }

    public void copy(GitCommitBean bean) {
        type = bean.type;
        name = bean.name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void print() {
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(type);
        parcel.writeString(name);
    }

    @Override
    @NonNull
    public GitCommitBean clone() {
        GitCommitBean bean = new GitCommitBean();
        bean.copy(this);
        return bean;
    }

    public static int getIcon(int type) {
        return switch (type) {
            case COMMIT_ADDED, COMMIT_UNTRACKED ->
                R.drawable.event_on_child_added_48dp;
            case COMMIT_CHANGED, COMMIT_UNCOMMITTED, COMMIT_MODIFICATON ->
                R.drawable.event_on_child_changed_48dp;
            case COMMIT_REMOVED, COMMIT_MISSING ->
                R.drawable.event_on_child_removed_48dp;
            default ->
                R.drawable.event_on_file_picked_cancel_48dp;
        };
    }
}
