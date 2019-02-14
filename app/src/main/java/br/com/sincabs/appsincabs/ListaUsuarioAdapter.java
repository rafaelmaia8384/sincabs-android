package br.com.sincabs.appsincabs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;


import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ListaUsuarioAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<ListaUsuario> listaUsuarios;

    private RecyclerView rv;

    private final int VIEW_ITEM = 0;
    private final int VIEW_LOADING = 1;

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int index = 1;
    private int lastVisibleItem, totalItemCount;

    private OnLoadMoreListener onLoadMoreListener;

    public ListaUsuarioAdapter(Context context, RecyclerView rv, ArrayList<ListaUsuario> listaUsuarios) {

        this.context = context;
        this.rv = rv;
        this.listaUsuarios = listaUsuarios;

        final LinearLayoutManager llm = (LinearLayoutManager) rv.getLayoutManager();

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = llm.getItemCount();
                lastVisibleItem = llm.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {

                    if (onLoadMoreListener != null) {

                        onLoadMoreListener.onLoadMore(++index);
                    }

                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {

        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void setLoaded() {

        this.isLoading = false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_ITEM) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_lista_usuario, parent, false);

            final RecyclerView rv = parent.findViewById(R.id.recyclerViewUsuarios); //parent.getRootView().findViewById(R.id.recyclerViewUsuarios);

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    final int pos = rv.getChildAdapterPosition(view);

                    if (DataHolder.getInstance().getLoginData("admin").equals("1")) {

                        MaterialDialog.SingleButtonCallback positiveCallback = new MaterialDialog.SingleButtonCallback() {

                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                int id = ((RadioGroup)dialog.getCustomView().findViewById(R.id.radioGroup)).getCheckedRadioButtonId();

                                if (id == -1) {

                                    SincabsActivity.dialogHelper.showError("Escolha uma opção.");

                                    return;
                                }

                                String opcao = dialog.getCustomView().findViewById(id).getTag().toString();

                                if (opcao.equals("1")) {

                                    abrirPerfilUsuario(pos);
                                }
                                else {

                                    abrirInformacoesDoUsuario(pos);
                                }
                            }
                        };

                        new MaterialDialog.Builder(context)
                                .title("Admin")
                                .customView(R.layout.layout_opcao_admin, true)
                                .positiveText("OK")
                                .negativeText("Cancelar")
                                .onPositive(positiveCallback)
                                .show();
                    }
                    else {

                        abrirPerfilUsuario(pos);
                    }
                }
            });

            view.findViewById(R.id.imagemPerfil).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    int pos = rv.getChildAdapterPosition((View)view.getParent().getParent());

                    String img_principal = listaUsuarios.get(pos).img_perfil_principal;

                    if (!img_principal.equals("null")) {

                        Intent i = new Intent(context, ImageViewActivity.class);
                        i.putExtra("img_principal", img_principal);
                        context.startActivity(i);
                    }
                }
            });

            return new mViewHolder(view);
        }
        else { //VIEW_LOADING

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading, parent, false);

            return new mLoadingViewHolder(view);
        }
    }

    private void abrirPerfilUsuario(int pos) {

        SincabsActivity.dialogHelper.showProgress();

        SincabsActivity.sincabsServer.perfilUsuario(listaUsuarios.get(pos).id_usuario, new SincabsResponse() {

            @Override
            void onResponseNoError(String msg, JSONObject extra) {

                DataHolder.getInstance().setPerfilUsuarioData(extra);

                Intent i = new Intent(context, PerfilUsuarioActivity.class);

                ((SincabsActivity)context).startActivityForResult(i, 500);
            }

            @Override
            void onResponseError(String error) {

                SincabsActivity.dialogHelper.showError(error);
            }

            @Override
            void onNoResponse(String error) {

                SincabsActivity.dialogHelper.showError(error);
            }

            @Override
            void onPostResponse() {

                SincabsActivity.dialogHelper.dismissProgress();
            }
        });
    }

    private void abrirInformacoesDoUsuario(int pos) {

        SincabsActivity.dialogHelper.showProgress();

        SincabsActivity.sincabsServer.adminObterInformacoesUsuario(listaUsuarios.get(pos).id_usuario, new SincabsResponse() {

            @Override
            void onResponseNoError(String msg, JSONObject extra) {

                DataHolder.getInstance().setPerfilUsuarioAdminData(extra);

                Intent i = new Intent(context, PerfilUsuarioAdminActivity.class);

                ((SincabsActivity)context).startActivity(i);
            }

            @Override
            void onResponseError(String error) {

                SincabsActivity.dialogHelper.showError(error);
            }

            @Override
            void onNoResponse(String error) {

                SincabsActivity.dialogHelper.showError(error);
            }

            @Override
            void onPostResponse() {

                SincabsActivity.dialogHelper.dismissProgress();
            }
        });
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof mViewHolder) {

            ListaUsuario listaUsuario = listaUsuarios.get(position);

            String img_busca = listaUsuario.img_perfil_busca;

            if (!img_busca.equals("null")) {

                ImageLoader.getInstance().loadImage(SincabsServer.getImageAddress(img_busca), new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                        ((mViewHolder)holder).imagemPerfil.setImageResource(R.drawable.img_perfil);

                        super.onLoadingStarted(imageUri, view);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                        ((mViewHolder)holder).imagemPerfil.setImageBitmap(loadedImage);

                        super.onLoadingComplete(imageUri, view, loadedImage);
                    }
                });
            }
            else{

                ((mViewHolder)holder).imagemPerfil.setImageResource(R.drawable.img_perfil);
            }

            String ocupacao = listaUsuario.ocupacao;

            if (ocupacao.equals("3") || ocupacao.equals("4") || ocupacao.equals("6") || ocupacao.equals("8") || ocupacao.equals("9") || ocupacao.equals("10")) {

                ocupacao = pegarOcupacao(ocupacao);
            }
            else {

                if (ocupacao.equals("2") && listaUsuario.uf.equals("23")) {

                    ocupacao = "Brigada Militar, " + pegarUF(listaUsuario.uf);
                }
                else {

                    ocupacao = pegarOcupacao(ocupacao) + ", " + pegarUF(listaUsuario.uf);
                }
            }

            ((mViewHolder)holder).nomeCompleto.setText(listaUsuario.nome);
            ((mViewHolder)holder).ocupacaoUF.setText(ocupacao);
            ((mViewHolder)holder).dataCadastro.setText(pegarData(listaUsuario.data_cadastro));
            ((mViewHolder)holder).pontuacao.setText(listaUsuario.pontuacao);
        }
    }

    @Override
    public int getItemCount() {

        return listaUsuarios.size();
    }

    @Override
    public int getItemViewType(int position) {

        return listaUsuarios.get(position) == null ? VIEW_LOADING : VIEW_ITEM;
    }

    public class mViewHolder extends RecyclerView.ViewHolder {

        ImageView imagemPerfil;
        TextView nomeCompleto;
        TextView ocupacaoUF;
        TextView dataCadastro;
        TextView pontuacao;

        public mViewHolder(View view) {

            super(view);

            imagemPerfil = view.findViewById(R.id.imagemPerfil);
            nomeCompleto = view.findViewById(R.id.nomeCompleto);
            ocupacaoUF = view.findViewById(R.id.ocupacaoUF);
            dataCadastro = view.findViewById(R.id.dataCadastro);
            pontuacao = view.findViewById(R.id.pontuacao);
        }
    }

    public class mLoadingViewHolder extends RecyclerView.ViewHolder {

        public mLoadingViewHolder(View view) {

            super(view);
        }
    }

    public interface OnLoadMoreListener {

        void onLoadMore(int index);
    }

    private String pegarOcupacao(String i) {

        if (i.equals("1")) {

            return "Policial Civil";
        }
        else if (i.equals("2")) {

            return "Policial Militar";
        }
        else if (i.equals("3")) {

            return "Policial Federal";
        }
        else if (i.equals("4")) {

            return "Policial Rodoviário Federal";
        }
        else if (i.equals("5")) {

            return "ASP Estadual";
        }
        else if (i.equals("6")) {

            return "ASP Federal";
        }
        else if (i.equals("7")) {

            return "Bombeiro Militar";
        }
        else if (i.equals("8")) {

            return "Militar da Marinha";
        }
        else if (i.equals("9")) {

            return "Militar do Exército";
        }
        else {

            return "Militar da Aeronáutica";
        }
    }

    private String pegarUF(String i) {

        int uf = Integer.parseInt(i);

        return context.getResources().getStringArray(R.array.uf_cadastro)[uf];
    }

    public String pegarData(String data) {

        SimpleDateFormat entrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat saida = new SimpleDateFormat("dd/MM/yyyy");

        try {

            Date dataEntrada = entrada.parse(data);

            return saida.format(dataEntrada);
        }
        catch (Exception e) {

            return "erro";
        }
    }
}
