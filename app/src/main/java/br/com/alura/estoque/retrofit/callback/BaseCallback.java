package br.com.alura.estoque.retrofit.callback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class BaseCallback<T> implements Callback<T> {


    private DadosCarregadosCallback<T> callback;

    public BaseCallback(DadosCarregadosCallback<T> callback) {
        this.callback = callback;
    }

    @Override
    @EverythingIsNonNull
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            T result = response.body();
            if (result != null) {
                callback.quandoSucesso(result);
            } else {
                callback.quandoErro("Erro ao tentar capturar o resultado.");

            }
        }
    }

    @Override
    @EverythingIsNonNull
    public void onFailure(Call<T> call, Throwable t) {
        callback.quandoErro("Erro: " + t.getMessage());
    }

    public interface DadosCarregadosCallback<T> {
        void quandoSucesso(T result);

        void quandoErro(String erro);
    }
}
