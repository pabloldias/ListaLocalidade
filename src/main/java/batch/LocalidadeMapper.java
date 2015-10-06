package batch;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class LocalidadeMapper implements RowMapper<Localidade> {

	private static final String COLUNA_ID = "id";
	private static final String COLUNA_UF = "uf";
	private static final String COLUNA_NOME = "localidade";
	private static final String COLUNA_CEP = "cep";
	private static final String COLUNA_TIPO = "tipo";

	public Localidade mapRow(ResultSet rs, int rowNum) throws SQLException {
		Localidade localidade = new Localidade();
		
		localidade.setId(rs.getLong(COLUNA_ID));
		localidade.setUf(rs.getString(COLUNA_UF));
		localidade.setNome(rs.getString(COLUNA_NOME));
		localidade.setCep(rs.getString(COLUNA_CEP));
		localidade.setTipo(rs.getString(COLUNA_TIPO));
		
		return localidade;
	}

}
