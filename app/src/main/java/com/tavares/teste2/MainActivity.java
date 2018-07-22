package com.tavares.teste2;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.tavares.teste2.model.Livro;

import com.tavares.teste2.provider.LivroProvider;
import com.tavares.teste2.sqlite.LivroSQLHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
        Livro livro;
    String mTextoBusca;
    LivroCursorAdapter mAdapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        livro = new Livro();
        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        mAdapter = new LivroCursorAdapter(this);
        mAdapter.setHasStableIds(true);
        mAdapter = new LivroCursorAdapter(new LivroCursorAdapter.AoClicarNoItem() {
            @Override
            public void itemFoiClicado(Cursor cursor) {
                long id  = cursor.getLong(cursor.getColumnIndex(LivroSQLHelper.COLUNA_ID));
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentPetUri = ContentUris.withAppendedId(LivroProvider.CONTENT_URI, id);
                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(mAdapter);;




        configuraArrastaDelete();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                startActivity(intent);
            }
        });






        getLoaderManager().initLoader(0, null, this);
    }

    //Configura o delete arrastando o item seja para esquerda ou direita!
    private void configuraArrastaDelete() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int x = viewHolder.getLayoutPosition();
                Cursor cursor = mAdapter.getCursor();
                cursor.moveToPosition(x);
                final long id = cursor.getLong(cursor.getColumnIndex(LivroSQLHelper.COLUNA_ID));
                getBaseContext().getContentResolver().delete(
                        Uri.withAppendedPath(LivroProvider.CONTENT_URI, String.valueOf(id)),
                        null, null);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                LivroSQLHelper.COLUNA_ID,
                LivroSQLHelper.COLUNA_TITULO,
                LivroSQLHelper.COLUNA_AUTOR,
                LivroSQLHelper.COLUNA_IMAGEM,
                LivroSQLHelper.COLUNA_PRECO,
                LivroSQLHelper.COLUNA_QUANTIDADE};

        return new CursorLoader(this,
                LivroProvider.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.setCursor(null);
    }
}
