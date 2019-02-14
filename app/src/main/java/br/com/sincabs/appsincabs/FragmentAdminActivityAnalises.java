package br.com.sincabs.appsincabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FragmentAdminActivityAnalises extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<ListaUsuario> listaUsuarios;
    private ListaUsuarioAdapterAnalises listaUsuariosAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_adminactivity_analises, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        processData();
    }

    private void processData() {

        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());

        recyclerView = getActivity().findViewById(R.id.recyclerViewAnalises);
        recyclerView.setLayoutManager(llm);
        listaUsuarios = new ArrayList<>();
        listaUsuariosAdapter = new ListaUsuarioAdapterAnalises(getActivity(), recyclerView, listaUsuarios);

        listaUsuariosAdapter.setOnLoadMoreListener(new ListaUsuarioAdapterAnalises.OnLoadMoreListener() {

            @Override
            public void onLoadMore(int index) {

                listaUsuarios.add(null);
                listaUsuariosAdapter.notifyItemInserted(listaUsuarios.size() - 1);

                AdminActivity.sincabsServer.adminObterAnalises(index, new SincabsResponse() {

                    @Override
                    void onResponseNoError(String msg, JSONObject extra) {

                        listaUsuarios.remove(listaUsuarios.size() - 1);
                        listaUsuariosAdapter.notifyItemRemoved(listaUsuarios.size());

                        try {

                            JSONArray jsonArray = extra.getJSONArray("Resultado");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject json = jsonArray.getJSONObject(i);

                                listaUsuarios.add(new ListaUsuario(json.getString("img_principal").toString(), json.getString("img_busca").toString(), json.getString("id_usuario").toString(), json.getString("nome_completo").toString(), json.getString("instituicao").toString(), json.getString("uf").toString(), json.getString("data_registro").toString(), "0"));
                            }

                            listaUsuariosAdapter.notifyDataSetChanged();
                        }
                        catch (JSONException e) { }
                    }

                    @Override
                    void onResponseError(String error) {

                        listaUsuarios.remove(listaUsuarios.size() - 1);
                        listaUsuariosAdapter.notifyItemRemoved(listaUsuarios.size());
                    }

                    @Override
                    void onNoResponse(String error) {

                        listaUsuarios.remove(listaUsuarios.size() - 1);
                        listaUsuariosAdapter.notifyItemRemoved(listaUsuarios.size());
                    }

                    @Override
                    void onPostResponse() {

                        listaUsuariosAdapter.setLoaded();
                    }
                });
            }
        });

        recyclerView.setAdapter(listaUsuariosAdapter);

        AdminActivity.dialogHelper.showProgress();

        AdminActivity.sincabsServer.adminObterAnalises(1, new SincabsResponse() {

            @Override
            void onResponseNoError(String msg, JSONObject extra) {

                try {

                    JSONArray jsonArray = extra.getJSONArray("Resultado");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject json = jsonArray.getJSONObject(i);

                        listaUsuarios.add(new ListaUsuario(json.getString("img_principal").toString(), json.getString("img_busca").toString(), json.getString("id_usuario").toString(), json.getString("nome_completo").toString(), json.getString("instituicao").toString(), json.getString("uf").toString(), json.getString("data_registro").toString(), "0"));
                    }

                    listaUsuariosAdapter.notifyDataSetChanged();
                    listaUsuariosAdapter.setLoaded();
                }
                catch (JSONException e) { }
            }

            @Override
            void onResponseError(String error) {

                AdminActivity.dialogHelper.showError(error);
            }

            @Override
            void onNoResponse(String error) {

                AdminActivity.dialogHelper.showError(error);
            }

            @Override
            void onPostResponse() {

                AdminActivity.dialogHelper.dismissProgress();
            }
        });
    }
}
