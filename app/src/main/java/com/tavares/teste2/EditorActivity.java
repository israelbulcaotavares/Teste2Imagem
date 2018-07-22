package com.tavares.teste2;


import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tavares.teste2.model.Livro;

import com.tavares.teste2.provider.LivroProvider;
import com.tavares.teste2.sqlite.LivroSQLHelper;

import java.io.File;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static java.lang.Long.getLong;

public class EditorActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor> {

    long id;
    Livro livro;
    EditText mEdtTitulo ,editPreco;
    EditText mEdtDescricao;
    private Uri mCurrentLivros;
    int qtde = 0;


    TextView resultQuantidade;


    ImageView editImagem;
    private View layout;
    private final int CAMERA = 1;
    private final int GALERIA = 2;
    private String localArquivoFoto;
    private static final int TIRA_FOTO = 123;
    private boolean fotoResource = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        livro = new Livro();
        layout = findViewById(R.id.mainLayout);
        mEdtTitulo = findViewById(R.id.editText);
        mEdtDescricao = findViewById(R.id.editText2);
        editImagem = findViewById(R.id.editImagem);
        editPreco = findViewById(R.id.editPreco);
        Button botao = findViewById(R.id.button);
        resultQuantidade =  findViewById(R.id.textQuantidade);


        Intent intent = getIntent();
        if (intent != null) {
            mCurrentLivros = intent.getData();
            if (mCurrentLivros == null) {
                invalidateOptionsMenu();
            } else {
                getSupportLoaderManager().initLoader(0, null, this);
            }
        }
        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditorActivity.this, MainActivity.class);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                salvar();
                startActivity(intent);
            }
        });

        File imgFile = new File(livro.getImagem());
        if(imgFile.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            editImagem.setImageBitmap(bitmap);

        }

        editImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertaImagem();
            }
        });



    }

        private void salvar(){
            ContentValues values = new ContentValues();
            livro.setNome(mEdtTitulo.getText().toString()); // INSERE O NOME
            livro.setEndereco(mEdtDescricao.getText().toString()); // INSERE O NOME
            livro.setImagem((String) editImagem.getTag());
            livro.setQuantidade(resultQuantidade.getText().toString());
            int quantidade = 0;
            if (!TextUtils.isEmpty(livro.getQuantidade())) {
                quantidade = Integer.parseInt(livro.getQuantidade());
            }
            livro.setPreco(editPreco.getText().toString());

            float preco = 0f;

                if (!TextUtils.isEmpty(livro.getPreco())) {
                    preco = Float.parseFloat(livro.getPreco());

                }


            values.put(LivroSQLHelper.COLUNA_TITULO,     livro.getNome());
            values.put(LivroSQLHelper.COLUNA_AUTOR, livro.getEndereco());
            values.put(LivroSQLHelper.COLUNA_QUANTIDADE, quantidade);
            values.put(LivroSQLHelper.COLUNA_PRECO,preco);
            values.put(LivroSQLHelper.COLUNA_IMAGEM, livro.getImagem());

            if (mCurrentLivros == null) {
                Uri uri = getContentResolver().insert(LivroProvider.CONTENT_URI, values);
                if (uri == null) {
                    Toast.makeText(this, "erro", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "salvo", Toast.LENGTH_SHORT).show();
                }
            } else {
                int linhasAfetadas = getContentResolver().update(mCurrentLivros, values, null, null);
                if (linhasAfetadas == 0) {
                    Toast.makeText(this, "erro", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "atualizado", Toast.LENGTH_SHORT).show();

                }
            }
        }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                LivroSQLHelper.COLUNA_ID,
                LivroSQLHelper.COLUNA_TITULO,
                LivroSQLHelper.COLUNA_AUTOR,
                LivroSQLHelper.COLUNA_IMAGEM,
                LivroSQLHelper.COLUNA_QUANTIDADE,
                LivroSQLHelper.COLUNA_PRECO};

        return new CursorLoader(this,
                mCurrentLivros,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(LivroSQLHelper.COLUNA_TITULO);
            int enderecoColumnIndex = cursor.getColumnIndex(LivroSQLHelper.COLUNA_AUTOR);
            int imagemColumnIndex = cursor.getColumnIndex(LivroSQLHelper.COLUNA_IMAGEM);
            int quantidadeColumnIndex = cursor.getColumnIndex(LivroSQLHelper.COLUNA_QUANTIDADE);
            int precoColumnIndex = cursor.getColumnIndex(LivroSQLHelper.COLUNA_PRECO);

            String nome = cursor.getString(nameColumnIndex);
            String endereco = cursor.getString(enderecoColumnIndex);
            String imagem = cursor.getString(imagemColumnIndex);
            carregaImagem(imagem);
            int quantidade = cursor.getInt(quantidadeColumnIndex);
            Float preco = cursor.getFloat(precoColumnIndex);

            mEdtTitulo.setText(nome);
            mEdtDescricao.setText(endereco);
            resultQuantidade.setText(String.valueOf(quantidade));
            editImagem.setTag(imagem);
            editPreco.setText(String.valueOf(preco));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mEdtTitulo.setText("");
        mEdtDescricao.setText("");
        resultQuantidade.setText("");
        editPreco.setText("");
        editImagem.setTag("");

    }


//TODO: PARTE DA CAMERA E GALERIA
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!fotoResource) {
            if (resultCode == RESULT_OK && null != data) {
                Uri imagemSel = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(
                        imagemSel,
                        filePathColumn,
                        null,
                        null,
                        null);

                cursor.moveToFirst();
                //int columnIndex = cursor.getColumnIndex(LivroSQLHelper.COLUNA_IMAGEM);
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String caminhoFoto = cursor.getString(columnIndex);
                cursor.close();

                carregaImagem(caminhoFoto);
            }

        }else{
            if (requestCode == TIRA_FOTO) {
                if(resultCode == Activity.RESULT_OK) {
                    carregaImagem(localArquivoFoto);
                } else {
                    localArquivoFoto = null;
                }
            }
        }

    }

    public void decrementa(View view){
        if (qtde != 0){
            qtde = qtde - 1;
        }
        displlay(qtde);
    }
    public void incrementa(View view){
        qtde = qtde + 1;
        displlay(qtde);
    }


    public void displlay(int qtd){
        resultQuantidade.setText(String.valueOf(qtd));
    }

    public void carregaImagem(String localArquivoFoto) {
        if(localArquivoFoto != null) {
            Bitmap imagemFoto = BitmapFactory.decodeFile(localArquivoFoto);
            editImagem.setImageBitmap(imagemFoto);
            editImagem.setTag(localArquivoFoto);
        }
    }

    private void alertaImagem(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione a fonte da info");
        builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clicaTirarFoto();
            }
        });
        builder.setNegativeButton("Galeria", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clicaCarregaImagem();
            }
        });
        builder.create().show();
    }

    private void clicaTirarFoto(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED){
            requestCameraPermission();
        } else {
            abrirCamera();
        }
    }

    private void requestCameraPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)){

            Snackbar.make(layout, "É necessário permitir para utilizar a câmera!",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(EditorActivity.this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            CAMERA);
                }
            }).show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CAMERA);
        }
    }



    public void abrirCamera(){
        fotoResource = true;
        localArquivoFoto = getExternalFilesDir(null) + "/"+ System.currentTimeMillis()+".jpg";

        Intent irParaCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        irParaCamera.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this,
                this.getApplicationContext().getPackageName() + ".provider", new File(localArquivoFoto)));
        startActivityForResult(irParaCamera, 123);
    }


    private void clicaCarregaImagem(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            requestGaleriaPermission();
        } else {
            abrirGaleria();
        }
    }

    private void requestGaleriaPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)){

            Snackbar.make(layout, "É necessário permitir para utilizar a galeria!",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(EditorActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            GALERIA);
                }
            }).show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    GALERIA);
        }
    }

    private void abrirGaleria(){
        fotoResource=false;
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    clicaTirarFoto();
                }
                break;
            case GALERIA:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    clicaCarregaImagem();
                }
                break;
        }
    }


}


