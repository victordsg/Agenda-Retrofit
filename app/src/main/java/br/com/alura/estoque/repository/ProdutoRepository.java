package br.com.alura.estoque.repository;

import java.util.List;

import br.com.alura.estoque.asynctask.BaseAsyncTask;
import br.com.alura.estoque.database.dao.ProdutoDAO;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.retrofit.EstoqueRetrofit;
import br.com.alura.estoque.retrofit.callback.BaseCallback;
import retrofit2.Call;

public class ProdutoRepository {

    private ProdutoDAO dao;

    public ProdutoRepository(ProdutoDAO dao) {
        this.dao = dao;
    }

    public void buscaProdutos(DadosCarregadosCallback<List<Produto>> callback) {

        /**gostaria que ele iniciasse pegando as info do celular.*/
        new BaseAsyncTask<>(
                dao::buscaTodos,
                resultado -> {
                    /**interface que ira receber uma atualizacao do adapter
                     * o motivo de estar fazendo isso é pq o adapter faz parte da UI da activity*/
                    callback.quandoSalvo(resultado);

                    /**Ao terminar de buscar pelos dados internos, ele inicia a busca da API*/
                    buscaPorProdutosBancoDeDadosExterno(callback);
                })
                .execute();


    }

    private void buscaPorProdutosBancoDeDadosExterno(DadosCarregadosCallback<List<Produto>> callback) {
        Call<List<Produto>> call = new EstoqueRetrofit().getProdutoService().lista();

        call.enqueue(new BaseCallback<>(new BaseCallback.DadosCarregadosCallback<List<Produto>>() {
            @Override
            public void quandoSucesso(List<Produto> result) {
                buscaProdutoBancoDeDadosInterno(result, callback);
            }

            @Override
            public void quandoErro(String erro) {
                callback.quandoFalha(erro);
            }
        }));


    }

    private void buscaProdutoBancoDeDadosInterno(List<Produto> produtos, DadosCarregadosCallback<List<Produto>> callback) {
        /**adiciono para o banco de dados em uma asynctask (outra)*/
        new BaseAsyncTask<>(() -> {
            dao.salvaLista(produtos);
            return dao.buscaTodos();
        }, callback::quandoSalvo
        ).execute();
    }

    public void salva(Produto produto, DadosCarregadosCallback<Produto> callback) {

        Call<Produto> call = new EstoqueRetrofit().getProdutoService().salva(produto);
        call.enqueue(new BaseCallback<>(new BaseCallback.DadosCarregadosCallback<Produto>() {
            @Override
            public void quandoSucesso(Produto result) {
                adicionaInternamente(result, callback);
            }

            @Override
            public void quandoErro(String erro) {
                callback.quandoFalha(erro);
            }
        }));


    }

    private void adicionaInternamente(Produto produtoDaApi, DadosCarregadosCallback<Produto> callback) {
        new BaseAsyncTask<>(() -> {
            long id = dao.salva(produtoDaApi);
            return dao.buscaProduto(id);
        }, callback::quandoSalvo)
                .execute();
    }

    public interface DadosCarregadosCallback<T> {
        void quandoSalvo(T produto);

        void quandoFalha(String mensagem);

    }
}
