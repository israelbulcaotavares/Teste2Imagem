package com.tavares.teste2.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tavares.teste2.sqlite.LivroSQLHelper;

public class LivroProvider extends ContentProvider {

 public static final String LOG_TAG = LivroProvider.class.getSimpleName();
    private static final String AUTHORITY = "com.tavares.teste2";
    private static final String PATH = "livros";
    private static final int LIVROS_GERAL = 100;
    private static final int LIVRO_ID = 101;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY +"/"+ PATH);
    private LivroSQLHelper mHelper;

    public static final String CONTENT_LIST_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH;

    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);;


    static {
        sUriMatcher.addURI(AUTHORITY, PATH, LIVROS_GERAL);
        sUriMatcher.addURI(AUTHORITY, PATH + "/#", LIVRO_ID);
    }



    @Override
    public boolean onCreate() {
        mHelper = new LivroSQLHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case LIVROS_GERAL:
                cursor = database.query(LivroSQLHelper.TABELA,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case LIVRO_ID:
                selection = LivroSQLHelper.COLUNA_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(LivroSQLHelper.TABELA,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LIVROS_GERAL:
                return CONTENT_LIST_TYPE;
            case LIVRO_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LIVROS_GERAL:
                return insertPet(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues values) {

        String name = values.getAsString(LivroSQLHelper.COLUNA_TITULO);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        String endereco = values.getAsString(LivroSQLHelper.COLUNA_AUTOR);
        if (endereco == null) {
            throw new IllegalArgumentException("Pet requires a endereco");
        }

        Integer quantidade = values.getAsInteger(LivroSQLHelper.COLUNA_QUANTIDADE);
         if (quantidade != null && quantidade < 0) {
                throw new IllegalArgumentException("Livro requires valid quantidade");
            }

        Float preco = values.getAsFloat(LivroSQLHelper.COLUNA_PRECO);
        if (preco != null && preco < 0) {
            throw new IllegalArgumentException("Livro requires valid preço");
        }

        String imagem = values.getAsString(LivroSQLHelper.COLUNA_IMAGEM);
            if (imagem == null) {
                throw new IllegalArgumentException("Livro requires a imagem");
            }


        SQLiteDatabase database = mHelper.getWritableDatabase();
        long id = database.insert(LivroSQLHelper.TABELA, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case LIVROS_GERAL:
                rowsDeleted = database.delete(LivroSQLHelper.TABELA, selection, selectionArgs);
                break;
            case LIVRO_ID:
                selection = LivroSQLHelper.COLUNA_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(LivroSQLHelper.TABELA, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LIVROS_GERAL:
                return updatePet(uri, values, selection, selectionArgs);
            case LIVRO_ID:
                selection = LivroSQLHelper.COLUNA_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(LivroSQLHelper.COLUNA_TITULO)) {
            String name = values.getAsString(LivroSQLHelper.COLUNA_TITULO);
            if (name == null) {
                throw new IllegalArgumentException("Livro requires a name");
            }
        }

        if (values.containsKey(LivroSQLHelper.COLUNA_AUTOR)) {
            String autor = values.getAsString(LivroSQLHelper.COLUNA_AUTOR);
            if (autor == null) {
                throw new IllegalArgumentException("Livro requires a autor");
            }
        }

        if (values.containsKey(LivroSQLHelper.COLUNA_QUANTIDADE)) {
            Integer quantidade = values.getAsInteger(LivroSQLHelper.COLUNA_QUANTIDADE);
            if (quantidade != null && quantidade < 0) {
                throw new IllegalArgumentException("Livro requires valid quantidade");
            }
        }

        if (values.containsKey(LivroSQLHelper.COLUNA_PRECO)) {
            Float preco = values.getAsFloat(LivroSQLHelper.COLUNA_PRECO);
            if (preco != null && preco < 0) {
                throw new IllegalArgumentException("Livro requires valid preço");
            }
        }

        if (values.containsKey(LivroSQLHelper.COLUNA_IMAGEM)) {
            String imagem = values.getAsString(LivroSQLHelper.COLUNA_IMAGEM);
            if (imagem == null) {
                throw new IllegalArgumentException("Livro requires a imagem");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mHelper.getWritableDatabase();

        int rowsUpdated = database.update(LivroSQLHelper.TABELA, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}