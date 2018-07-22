package com.tavares.teste2.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LivroSQLHelper extends SQLiteOpenHelper {
    private static final String NOME_BANCO = "livrosTeste.db";
    private static final int VERSAO_BANCO = 1;
    public static final String TABELA = "livros";
    public static final String COLUNA_ID = "_id";
    public static final String COLUNA_TITULO = "nome";
    public static final String COLUNA_AUTOR = "endereco";
    public static final String COLUNA_QUANTIDADE = "quantidade";
    public static final String COLUNA_PRECO = "preco";
    public static final String COLUNA_IMAGEM= "imagem";

    public LivroSQLHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE "+ TABELA +" (" +
                        COLUNA_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                        COLUNA_TITULO +" TEXT NOT NULL, "+
                        COLUNA_AUTOR +" TEXT, "+
                        COLUNA_PRECO     +" REAL , "+
                        COLUNA_QUANTIDADE     +" INTEGER NOT NULL DEFAULT 0, "+
                        COLUNA_IMAGEM +" TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // para as próximas versões
    }
}
