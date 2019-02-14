package br.com.sincabs.appsincabs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;


import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ListaSuspeitoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<ListaSuspeito> listaSuspeitos;

    private RecyclerView rv;

    private final int VIEW_ITEM = 0;
    private final int VIEW_LOADING = 1;

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int index = 1;
    private int lastVisibleItem, totalItemCount;

    private OnLoadMoreListener onLoadMoreListener;

    public ListaSuspeitoAdapter(Context context, RecyclerView rv, ArrayList<ListaSuspeito> listaSuspeitos) {

        this.context = context;
        this.rv = rv;
        this.listaSuspeitos = listaSuspeitos;

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

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_lista_suspeito, parent, false);

            final RecyclerView rv = parent.findViewById(R.id.recyclerViewSuspeitos); //parent.getRootView().findViewById(R.id.recyclerViewSuspeitos);

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    int pos = rv.getChildAdapterPosition(view);

                    SincabsActivity.dialogHelper.showProgress();

                    SincabsActivity.sincabsServer.perfilSuspeito(listaSuspeitos.get(pos).id_suspeito, new SincabsResponse() {

                        @Override
                        void onResponseNoError(String msg, JSONObject extra) {

                            DataHolder.getInstance().setPerfilSuspeitoData(extra);

                            Intent i = new Intent(context, PerfilSuspeitoActivity.class);

                            ((SincabsActivity)context).startActivityForResult(i, 400);
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
            });

            view.findViewById(R.id.imagemPerfil).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    int pos = rv.getChildAdapterPosition((View)view.getParent().getParent());

                    String img_principal = listaSuspeitos.get(pos).img_perfil_principal;

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

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof mViewHolder) {

            ListaSuspeito listaSuspeito = listaSuspeitos.get(position);

            String img_busca = listaSuspeito.img_perfil_busca;

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

            ((mViewHolder)holder).nomeAlcunha.setText(listaSuspeito.nome_alcunha);
            ((mViewHolder)holder).areasAtuacao.setText(listaSuspeito.areas_atuacao);
            ((mViewHolder)holder).dataCadastro.setText(pegarData(listaSuspeito.data_cadastro));
        }
    }

    @Override
    public int getItemCount() {

        return listaSuspeitos.size();
    }

    @Override
    public int getItemViewType(int position) {

        return listaSuspeitos.get(position) == null ? VIEW_LOADING : VIEW_ITEM;
    }

    public class mViewHolder extends RecyclerView.ViewHolder {

        ImageView imagemPerfil;
        TextView nomeAlcunha;
        TextView areasAtuacao;
        TextView dataCadastro;

        public mViewHolder(View view) {

            super(view);

            imagemPerfil = view.findViewById(R.id.imagemPerfil);
            nomeAlcunha = view.findViewById(R.id.nomeAlcunha);
            areasAtuacao = view.findViewById(R.id.areasAtuacao);
            dataCadastro = view.findViewById(R.id.dataCadastro);
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
