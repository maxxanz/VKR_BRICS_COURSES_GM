package com.example.brics_vm.ui.ranking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.brics_vm.R;
import com.example.brics_vm.adapters.CountryRankingAdapter;
import com.example.brics_vm.api.NstuClient;
import com.example.brics_vm.api.NstuApi;
import com.example.brics_vm.models.CountryRanking;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankingFragment extends Fragment {

    private RecyclerView recyclerView;
    private CountryRankingAdapter adapter;
    private NstuApi nstuApi;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ranking, container, false);

        recyclerView = root.findViewById(R.id.recyclerView);

        setupRecyclerView();
        loadRanking();

        return root;
    }

    private void setupRecyclerView() {
        adapter = new CountryRankingAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadRanking() {
        nstuApi = NstuClient.getClient();
        nstuApi.getCountryRankingOrdered().enqueue(new Callback<List<CountryRanking>>() {
            @Override
            public void onResponse(Call<List<CountryRanking>> call,
                                   Response<List<CountryRanking>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    List<CountryRanking> countries = response.body();
                    if (!countries.isEmpty()) {
                        adapter.updateCountries(countries);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CountryRanking>> call, Throwable t) {
                Toast.makeText(getContext(), "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}