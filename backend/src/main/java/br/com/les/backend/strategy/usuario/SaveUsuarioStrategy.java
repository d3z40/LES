package br.com.les.backend.strategy.usuario;

import org.springframework.stereotype.Component;

import br.com.les.backend.entity.EntidadeDominio;
import br.com.les.backend.utils.Resultado;

@Component
public class SaveUsuarioStrategy extends AbstractUsuarioStrategy {

	@Override
	public Resultado execute(EntidadeDominio entidade, String callerMethod ) {

		resultado.setSucesso( "Usuário salvo com sucesso!" );
		return resultado;
	}

}
