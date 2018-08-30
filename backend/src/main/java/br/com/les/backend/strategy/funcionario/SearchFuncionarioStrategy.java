package br.com.les.backend.strategy.funcionario;

import org.springframework.stereotype.Component;

import br.com.les.backend.entity.EntidadeDominio;
import br.com.les.backend.entity.Login;
import br.com.les.backend.entity.Usuario;
import br.com.les.backend.strategy.usuario.AbstractUsuarioStrategy;
import br.com.les.backend.utils.Result;

@Component
public class SearchFuncionarioStrategy extends AbstractEmployeeStrategy {

	@Override
	public Result execute( EntidadeDominio entidade, String callerMethod ) {
		
		result = new Result();
		
		Usuario u = ( Usuario ) entidade;
		
		if ( u != null ) {

			Login l = u.getLogin();
			
			switch ( callerMethod ) {
			
				case "findAll":
					
				break;
				
				case "findByUsuario": // search for list of users
					
					if ( l != null && l.getNomeLogin() == null && l.getDataCriacao() != null && u.getEmail() == null && u.getNome() == null )
						result.setErro( "Ao menos um dos campos deve ser preenchido!");
					break;
					
				case "findByLogin": // search for logged user
					
					if ( l != null ) {
						if ( l.getNomeLogin() == null || l.getNomeLogin().equals("") )
							result.setErro( "Login deve ser preenchido!" );
							
						if ( l.getSenha() == null || l.getSenha().equals("") )
							result.setErro( "Senha deve ser preenchida!" );
					
					}
					break;
					
				default:
		        break;
			}
		}
		return result;
	}
}
