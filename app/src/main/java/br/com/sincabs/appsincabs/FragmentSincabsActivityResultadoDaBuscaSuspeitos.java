package br.com.sincabs.appsincabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FragmentSincabsActivityResultadoDaBuscaSuspeitos extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<ListaSuspeito> listaSuspeitos;
    private ListaSuspeitoAdapter listaSuspeitosAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_sincabsactivity_resultado_da_busca_suspeitos, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());

        recyclerView = getActivity().findViewById(R.id.recyclerViewSuspeitos);
        recyclerView.setLayoutManager(llm);
        listaSuspeitos = new ArrayList<>();
        listaSuspeitosAdapter = new ListaSuspeitoAdapter(getActivity(), recyclerView, listaSuspeitos);

        listaSuspeitosAdapter.setOnLoadMoreListener(new ListaSuspeitoAdapter.OnLoadMoreListener() {

            @Override
            public void onLoadMore(int index) {

                listaSuspeitos.add(null);
                listaSuspeitosAdapter.notifyItemInserted(listaSuspeitos.size() - 1);

                SincabsActivity.sincabsServer.buscarSuspeito(index, new SincabsResponse() {

                    @Override
                    void onResponseNoError(String msg, JSONObject extra) {

                        if (!isVisible()) return;

                        listaSuspeitos.remove(listaSuspeitos.size() - 1);
                        listaSuspeitosAdapter.notifyItemRemoved(listaSuspeitos.size());

                        try {

                            JSONArray jsonArray = extra.getJSONArray("Resultado");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject json = jsonArray.getJSONObject(i);

                                listaSuspeitos.add(new ListaSuspeito(json.getString("img_principal").toString(), json.getString("img_busca").toString(), json.getString("id_suspeito").toString(), json.getString("nome_alcunha").toString(), json.getString("areas_de_atuacao").toString(), json.getString("data_registro").toString()));
                            }

                            listaSuspeitosAdapter.notifyDataSetChanged();
                        }
                        catch (JSONException e) { }
                    }

                    @Override
                    void onResponseError(String error) {

                        if (!isVisible()) return;

                        listaSuspeitos.remove(listaSuspeitos.size() - 1);
                        listaSuspeitosAdapter.notifyItemRemoved(listaSuspeitos.size());
                    }

                    @Override
                    void onNoResponse(String error) {

                        if (!isVisible()) return;

                        listaSuspeitos.remove(listaSuspeitos.size() - 1);
                        listaSuspeitosAdapter.notifyItemRemoved(listaSuspeitos.size());
                    }

                    @Override
                    void onPostResponse() {

                        if (!isVisible()) return;

                        listaSuspeitosAdapter.setLoaded();
                    }
                });
            }
        });

        recyclerView.setAdapter(listaSuspeitosAdapter);

        SincabsActivity.sincabsServer.buscarSuspeito(1, new SincabsResponse() {

            @Override
            void onResponseNoError(String msg, JSONObject extra) {

                if (!isVisible()) return;

                try {

                    JSONArray jsonArray = extra.getJSONArray("Resultado");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject json = jsonArray.getJSONObject(i);

                        listaSuspeitos.add(new ListaSuspeito(json.getString("img_principal").toString(), json.getString("img_busca").toString(), json.getString("id_suspeito").toString(), json.getString("nome_alcunha").toString(), json.getString("areas_de_atuacao").toString(), json.getString("data_registro").toString()));
                    }

                    listaSuspeitosAdapter.notifyDataSetChanged();
                    listaSuspeitosAdapter.setLoaded();

                    getActivity().findViewById(R.id.layoutCarregandoSuspeitos).setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                catch (JSONException e) { }
            }

            @Override
            void onResponseError(String error) {

                if (!isVisible()) return;

                ((TextView)getActivity().findViewById(R.id.textViewSuspeitos)).setText(error);
            }

            @Override
            void onNoResponse(String error) {

                if (!isVisible()) return;

                ((TextView)getActivity().findViewById(R.id.textViewSuspeitos)).setText(error);
            }

            @Override
            void onPostResponse() {

                if (!isVisible()) return;

                getActivity().findViewById(R.id.progressSuspeitos).setVisibility(View.GONE);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }
}
