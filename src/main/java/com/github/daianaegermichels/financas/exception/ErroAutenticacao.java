package com.github.daianaegermichels.financas.exception;

public class ErroAutenticacao extends RuntimeException{
    public ErroAutenticacao(String mensagem){
        super(mensagem);
    }
}
