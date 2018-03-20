package br.com.liveo.searchview_materialdesign.model;

import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatImageView;

import java.util.ArrayList;

import br.com.liveo.searchview_materialdesign.R;

/**
 * Created by rudsonlima on 19/03/18.
 */

public class Company implements Parcelable {

    private int icon;
    private String name;

    @BindingAdapter("icon")
    public static void icon(AppCompatImageView imageView, int icon) {
        imageView.setImageResource(icon);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.getIcon());
        dest.writeString(this.getName());
    }

    private Company(String name, int icon) {
        this.setName(name);
        this.setIcon(icon);
    }

    private Company(Parcel in) {
        this.setIcon(in.readInt());
        this.setName(in.readString());
    }

    public static final Parcelable.Creator<Company> CREATOR = new Parcelable.Creator<Company>() {
        @Override
        public Company createFromParcel(Parcel source) {
            return new Company(source);
        }

        @Override
        public Company[] newArray(int size) {
            return new Company[size];
        }
    };

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static ArrayList<Company> getCompanies(){
        ArrayList<Company> companies = new ArrayList<>();

        companies.add(new Company("Appple", R.drawable.ic_apple));
        companies.add(new Company("Google", R.drawable.ic_google));
        companies.add(new Company("Github", R.drawable.ic_github));
        companies.add(new Company("Facebook", R.drawable.ic_facebook));
        companies.add(new Company("Instagram", R.drawable.ic_instagram));
        companies.add(new Company("Linkedin", R.drawable.ic_linkedin));
        companies.add(new Company("Twitter", R.drawable.ic_twitter));
        companies.add(new Company("Microsoft", R.drawable.ic_microsoft));

        return companies;
    }
}
