package com.tavares.teste2;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tavares.teste2.model.Livro;
import com.tavares.teste2.sqlite.LivroSQLHelper;


public class LivroCursorAdapter extends RecyclerView.Adapter<LivroCursorAdapter.MensagemViewHolder> {
    private Cursor mCursor;
    private AoClicarNoItem mListener;
    private Context mContext;

    Livro livro;
    public LivroCursorAdapter(AoClicarNoItem listener) {
        mListener = listener;
    }

    public LivroCursorAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MensagemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hotel, parent, false);
        final MensagemViewHolder mensagemViewHolder = new MensagemViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mensagemViewHolder.getAdapterPosition();
                mCursor.moveToPosition(position);
                if (mListener != null) mListener.itemFoiClicado(mCursor);
            }
        });

        return mensagemViewHolder;
    }

    @Override
    public void onBindViewHolder(MensagemViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        int idx_titulo = mCursor.getColumnIndex(LivroSQLHelper.COLUNA_TITULO);
        int idx_imagem = mCursor.getColumnIndex(LivroSQLHelper.COLUNA_IMAGEM);
        int idx_quantidade = mCursor.getColumnIndex(LivroSQLHelper.COLUNA_QUANTIDADE);
        int idx_preco = mCursor.getColumnIndex(LivroSQLHelper.COLUNA_PRECO);

        String nome = mCursor.getString(idx_titulo);
        holder.mNome.setText(nome);

        String imagem = mCursor.getString(idx_imagem);
//          holder.mImagem.setImageResource(android.R.drawable.ic_menu_report_image);
//         holder.carregaImagem(imagem);

       Glide.with(holder.mImagem.getContext())
                .load(imagem)
               .asBitmap()
               .error(android.R.drawable.ic_menu_report_image)
               .diskCacheStrategy(DiskCacheStrategy.ALL)
               .into(holder.mImagem);

        int quantidade = mCursor.getInt(idx_quantidade);
        String quantidadeTexto = "" + quantidade;
        holder.mQuantidade.setText(quantidadeTexto);

        float preco = mCursor.getFloat(idx_preco);
        String precoText = "" + preco;
        holder.mPreco.setText("R$:" + precoText);
    }

    @Override
    public int getItemCount() {
        return (mCursor != null) ? mCursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        if (mCursor != null) {
            if (mCursor.moveToPosition(position)) {
                int idx_id = mCursor.getColumnIndex(LivroSQLHelper.COLUNA_ID);
                return mCursor.getLong(idx_id);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public Cursor getCursor(){
        return mCursor;
    }

    public void setCursor(Cursor newCursor){
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public interface AoClicarNoItem {
        void itemFoiClicado(Cursor cursor);
    }

    public static class MensagemViewHolder extends RecyclerView.ViewHolder {
        public TextView mNome;
        public TextView mQuantidade;
        public TextView mPreco;
        public ImageView mImagem;
        Context mContext;
        public CardView card;
        Livro livro;

        public MensagemViewHolder(View v) {
            super(v);
            mNome =  v.findViewById(R.id.txtNome);
            mImagem =  v.findViewById(R.id.imageView5);
            mQuantidade =  v.findViewById(R.id.textQuantidade);
            mPreco =  v.findViewById(R.id.textPreco);
            card =  v.findViewById(R.id.card_view);

        }
        public void carregaImagem(String localArquivoFoto) {
            if(localArquivoFoto != null) {
                Bitmap imagemFoto = BitmapFactory.decodeFile(localArquivoFoto);
                mImagem.setImageBitmap(imagemFoto);
                mImagem.setTag(localArquivoFoto);


            }

        }
    }
}