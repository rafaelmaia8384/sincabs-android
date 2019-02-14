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


public class FragmentSincabsActivityResultadoDaBuscaUsuarios extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<ListaUsuario> listaUsuarios;
    private ListaUsuarioAdapter listaUsuariosAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_sincabsactivity_resultado_da_busca_usuarios, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());

        recyclerView = getActivity().findViewById(R.id.recyclerViewUsuarios);
        recyclerView.setLayoutManager(llm);
        listaUsuarios = new ArrayList<>();
        listaUsuariosAdapter = new ListaUsuarioAdapter(getActivity(), recyclerView, listaUsuarios);

        listaUsuariosAdapter.setOnLoadMoreListener(new ListaUsuarioAdapter.OnLoadMoreListener() {

            @Override
            public void onLoadMore(int index) {

                listaUsuarios.add(null);
                listaUsuariosAdapter.notifyItemInserted(listaUsuarios.size() - 1);

                SincabsActivity.sincabsServer.buscarUsuario(index, new SincabsResponse() {

                    @Override
                    void onResponseNoError(String msg, JSONObject extra) {

                        if (!isVisible()) return;

                        listaUsuarios.remove(listaUsuarios.size() - 1);
                        listaUsuariosAdapter.notifyItemRemoved(listaUsuarios.size());

                        try {

                            JSONArray jsonArray = extra.getJSONArray("Resultado");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject json = jsonArray.getJSONObject(i);

                                listaUsuarios.add(new ListaUsuario(json.getString("img_principal").toString(), json.getString("img_busca").toString(), json.getString("id_usuario").toString(), json.getString("nome_completo").toString(), json.getString("instituicao").toString(), json.getString("uf").toString(), json.getString("data_registro").toString(), json.getString("pontuacao").toString()));
                            }

                            listaUsuariosAdapter.notifyDataSetChanged();
                        }
                        catch (JSONException e) { }
                    }

                    @Override
                    void onResponseError(String error) {

                        if (!isVisible()) return;

                        listaUsuarios.remove(listaUsuarios.size() - 1);
                        listaUsuariosAdapter.notifyItemRemoved(listaUsuarios.size());
                    }

                    @Override
                    void onNoResponse(String error) {

                        if (!isVisible()) return;

                        listaUsuarios.remove(listaUsuarios.size() - 1);
                        listaUsuariosAdapter.notifyItemRemoved(listaUsuarios.size());
                    }

                    @Override
                    void onPostResponse() {

                        if (!isVisible()) return;

                        listaUsuariosAdapter.setLoaded();
                    }
                });
            }
        });

        recyclerView.setAdapter(listaUsuariosAdapter);

        SincabsActivity.sincabsServer.buscarUsuario(1, new SincabsResponse() {

            @Override
            void onResponseNoError(String msg, JSONObject extra) {

                if (!isVisible()) return;

                try {

                    JSONArray jsonArray = extra.getJSONArray("Resultado");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject json = jsonArray.getJSONObject(i);

                        listaUsuarios.add(new ListaUsuario(json.getString("img_principal").toString(), json.getString("img_busca").toString(), json.getString("id_usuario").toString(), json.getString("nome_completo").toString(), json.getString("instituicao").toString(), json.getString("uf").toString(), json.getString("data_registro").toString(), json.getString("pontuacao").toString()));
                    }

                    listaUsuariosAdapter.notifyDataSetChanged();
                    listaUsuariosAdapter.setLoaded();

                    getActivity().findViewById(R.id.layoutCarregandoUsuarios).setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                catch (JSONException e) { }
            }

            @Override
            void onResponseError(String error) {

                if (!isVisible()) return;

                ((TextView)getActivity().findViewById(R.id.textViewUsuarios)).setText(error);
            }

            @Override
            void onNoResponse(String error) {

                if (!isVisible()) return;

                ((TextView)getActivity().findViewById(R.id.textViewUsuarios)).setText(error);
            }

            @Override
            void onPostResponse() {

                if (!isVisible()) return;

                getActivity().findViewById(R.id.progressUsuarios).setVisibility(View.GONE);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }
}
