package batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class LocalidadeProcessor implements ItemProcessor<Localidade, Localidade> {
	
	private static final Logger log = LoggerFactory.getLogger(LocalidadeProcessor.class);

	public Localidade process(final Localidade localidade) throws Exception {
		final String nome = localidade.getNome().toUpperCase();
		localidade.setNome(nome);
		return localidade;
	}

}
