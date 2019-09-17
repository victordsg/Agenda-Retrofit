package br.com.alura.estoque.repository;

import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import br.com.alura.estoque.asynctask.BaseAsyncTask;
import br.com.alura.estoque.database.dao.ProdutoDAO;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.retrofit.EstoqueRetrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProdutoRepository {

    private ProdutoDAO dao;

    public ProdutoRepository(ProdutoDAO dao) {
        this.dao = dao;
    }

    public void buscaProdutos(AdicionaDadosParaAdapter listener) {

        /**gostaria que ele iniciasse pegando as info do celular.*/
        new BaseAsyncTask<>(
                dao::buscaTodos,
                resultado -> {
                    /**interface que ira receber uma atualizacao do adapter
                     * o motivo de estar fazendo isso é pq o adapter faz parte da UI da activity*/
                    listener.quandoFinalizado(resultado);

                    /**Ao terminar de buscar pelos dados internos, ele inicia a busca da API*/
                    buscaPorProdutosBancoDeDadosExterno(listener);
                })
                .execute();


    }

    public void buscaPorProdutosBancoDeDadosExterno(AdicionaDadosParaAdapter listener) {
        Call<List<Produto>> call = new EstoqueRetrofit().getProdutoService().lista();

        call.enqueue(new Callback<List<Produto>>() {
            @Override
            public void onResponse(Call<List<Produto>> call, Response<List<Produto>> response) {

                /**lista do servidor.*/
                List<Produto> produtos = response.body();
                buscaProdutoBancoDeDadosInterno(produtos, listener);

            }

            @Override
            public void onFailure(Call<List<Produto>> call, Throwable t) {
                Log.e("Error", t.getMessage());
            }
        });
    }

    private void buscaProdutoBancoDeDadosInterno(List<Produto> produtos, AdicionaDadosParaAdapter listener) {
        /**adiciono para o banco de dados em uma asynctask (outra)*/
        new BaseAsyncTask<>(() -> {
            dao.salvaLista(produtos);
            return dao.buscaTodos();
        }, produtosAtualizados ->
                listener.quandoFinalizado(produtosAtualizados)
        ).execute();
    }

    public interface AdicionaDadosParaAdapter {
        void quandoFinalizado(List<Produto> listener);
    }
}
