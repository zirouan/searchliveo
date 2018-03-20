package br.com.liveo.searchview_materialdesign;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Locale;

import br.com.liveo.searchview_materialdesign.databinding.ContentMainItemBinding;
import br.com.liveo.searchview_materialdesign.model.Company;

/**
 * Created by rudsonlima on 19/03/18.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private ArrayList<Company> mCompanys;
    private ArrayList<Company> mSearchCompanys;

    public MainAdapter(ArrayList<Company> Companys) {
        this.mCompanys = Companys;
        this.mSearchCompanys = new ArrayList<>();
        this.mSearchCompanys.addAll(this.mCompanys);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }

        ContentMainItemBinding getBinding() {
            return DataBindingUtil.getBinding(itemView);
        }
    }

    @NonNull
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.content_main_item, parent, false).getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Company company = mCompanys.get(holder.getAdapterPosition());

        holder.getBinding().setVariable(BR.company, company);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return (mCompanys == null ? 0 : mCompanys.size());
    }

    private static String removeAccent(String text) {
        String result = Normalizer.normalize(text, Normalizer.Form.NFD);
        return result.replaceAll("[^\\p{ASCII}]", "");
    }

    void searchCompanyes(CharSequence charText) {

        charText = removeAccent((String) charText).toLowerCase(Locale.getDefault());

        mCompanys.clear();
        if (charText.length() == 0) {
            mCompanys.addAll(mSearchCompanys);
        } else {
            for (Company Company : mSearchCompanys) {
                String name = removeAccent(Company.getName());
                if (name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    mCompanys.add(Company);
                }
            }
        }

        notifyDataSetChanged();
    }
}

