package com.example.brics_vm.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.brics_vm.R;
import com.example.brics_vm.models.CountryRanking;
import java.util.List;

public class CountryRankingAdapter extends RecyclerView.Adapter<CountryRankingAdapter.ViewHolder> {

    private List<CountryRanking> countries;

    public CountryRankingAdapter(List<CountryRanking> countries) {
        this.countries = countries;
    }

    public void updateCountries(List<CountryRanking> newCountries) {
        this.countries = newCountries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_country_ranking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CountryRanking country = countries.get(position);

        String rankText;
        switch ((int) country.getRank()) {
            case 1: rankText = "🥇 1"; break;
            case 2: rankText = "🥈 2"; break;
            case 3: rankText = "🥉 3"; break;
            default: rankText = String.valueOf((int) country.getRank());
        }

        holder.rankText.setText(rankText);
        holder.countryName.setText(country.getCountry());
        holder.scoreText.setText(String.format("%.1f", country.getAvg_score()));
        holder.detailsText.setText("👥 " + country.getTotal_users() + " участников • 📊 " + country.getTotal_tests() + " тестов");
    }

    @Override
    public int getItemCount() {
        return countries != null ? countries.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankText, countryName, scoreText, detailsText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rankText = itemView.findViewById(R.id.rank_text);
            countryName = itemView.findViewById(R.id.country_name);
            scoreText = itemView.findViewById(R.id.score_text);
            detailsText = itemView.findViewById(R.id.details_text);
        }
    }
}