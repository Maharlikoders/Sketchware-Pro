package mod.elfilibustero.sketch.beans;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

public class GitHubBean implements Parcelable {
    public static final Parcelable.Creator<GitHubBean> CREATOR = new Parcelable.Creator<>() {
        @Override
        public GitHubBean createFromParcel(Parcel source) {
            return new GitHubBean(source);
        }

        @Override
        public GitHubBean[] newArray(int size) {
            return new GitHubBean[size];
        }
    };

    public static final String GIT_USE_N = "N";
    public static final String GIT_USE_Y = "Y";

    @Expose
    public String branch;
    @Expose
    public String email;
    @Expose
    public String repoUrl;
    @Expose
    public String token;
    @Expose
    public String username;
    @Expose
    public String useYn;

    public GitHubBean() {
        branch = "";
        email = "";
        repoUrl = "";
        token = "";
        username = "";
        useYn = GIT_USE_N;
    }

    public GitHubBean(Parcel parcel) {
        branch = parcel.readString();
        email = parcel.readString();
        repoUrl = parcel.readString();
        token = parcel.readString();
        username = parcel.readString();
        useYn = parcel.readString();
    }

    public static Parcelable.Creator<GitHubBean> getCreator() {
        return CREATOR;
    }

    public void copy(GitHubBean bean) {
        branch = bean.branch;
        email = bean.email;
        repoUrl = bean.repoUrl;
        token = bean.token;
        username = bean.username;
        useYn = bean.useYn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isEnabled() {
        return useYn != null && !useYn.isEmpty() && useYn.equals(GIT_USE_Y);
    }

    public void print() {
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(branch);
        parcel.writeString(email);
        parcel.writeString(repoUrl);
        parcel.writeString(token);
        parcel.writeString(username);
        parcel.writeString(useYn);
    }

    @Override
    @NonNull
    public GitHubBean clone() {
        GitHubBean bean = new GitHubBean();
        bean.copy(this);
        return bean;
    }
}
