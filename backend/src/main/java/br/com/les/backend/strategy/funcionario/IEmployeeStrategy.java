package br.com.les.backend.strategy.funcionario;

import br.com.les.backend.entity.EntidadeDominio;
import br.com.les.backend.utils.Result;

public interface IEmployeeStrategy {

	Result execute( EntidadeDominio entity, String callerMethod );
}
