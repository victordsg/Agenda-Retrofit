package br.com.alura.estoque.ui.activity;

import android.os.AsyncTask;

import java.util.List;

import br.com.alura.estoque.database.dao.ProdutoDAO;
import br.com.alura.estoque.model.Produto;

public class AdicionaListaParaBancoDeDados extends AsyncTask<Void, Void, List<Produto>> {

    private ProdutoDAO produtoDAO;
    private List<Produto> produtos;
    private AtualizaAdapterListener listener;

    public AdicionaListaParaBancoDeDados(ProdutoDAO produtoDAO, List<Produto> produtos, AtualizaAdapterListener listener) {
        this.produtoDAO = produtoDAO;
        this.produtos = produtos;
        this.listener = listener;
    }

    @Override
    protected List<Produto> doInBackground(Void... voids) {
        produtoDAO.salvaLista(produtos);
        return produtos;
    }

    @Override
    protected void onPostExecute(List<Produto> produtos) {
        super.onPostExecute(produtos);
        listener.quandoTerminado(produtos);

    }

    interface AtualizaAdapterListener {
        void quandoTerminado(List<Produto> produtos);
    }
}
