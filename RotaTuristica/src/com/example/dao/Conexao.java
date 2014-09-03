package com.example.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class Conexao extends SQLiteOpenHelper{

	private static final String BANCO_DADOS = "rota_turistica_testando";
	private static int VERSAO = 1;
//	private static int VERSAO = 4;

	public Conexao(Context context) {
		super(context, BANCO_DADOS, null, VERSAO);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE `item_descricao` ( `id` INTEGER NOT NULL, `nome` VARCHAR (255) NOT NULL,"
				+ " `municipio` INTEGER NOT NULL, `id_versao` INTEGER NOT NULL, `data_atualizacao` INTEGER NOT NULL, PRIMARY KEY (`id`));");

		db.execSQL("CREATE TABLE `ponto_localizavel` ( `id` INTEGER NOT NULL, `latitude` DOUBLE NOT NULL,"
				+ " `longitude` DOUBLE NOT NULL, PRIMARY KEY (`id`), FOREIGN KEY (id) REFERENCES item_descricao(id));");

		db.execSQL("CREATE TABLE `atracao_turistica` ( `id` INTEGER NOT NULL, `tipo` INTEGER NOT NULL, PRIMARY KEY (`id`), FOREIGN KEY (id) REFERENCES ponto_localizavel(id));");

		db.execSQL("CREATE TABLE `servico_turistico` ( `id` INTEGER NOT NULL, `tipo` INTEGER NOT NULL, PRIMARY KEY (`id`), FOREIGN KEY (id) REFERENCES ponto_localizavel(id));");

		db.execSQL("CREATE TABLE `tipo_atracao` ( `id` INTEGER NOT NULL, `atracao` VARCHAR (255) NOT NULL,"
				+ "`pictograma` INTEGER NOT NULL, PRIMARY KEY (`id`));");

		db.execSQL("CREATE TABLE `tipo_servico` ( `id` INTEGER NOT NULL, `servico` VARCHAR (255) NOT NULL,"
				+ "`pictograma` INTEGER NOT NULL, PRIMARY KEY (`id`), FOREIGN KEY (pictograma) REFERENCES pictograma(id));");

		db.execSQL("CREATE TABLE `pictograma` ( `id` INTEGER NOT NULL, `pictograma` VARCHAR (255) NOT NULL,"
				+ " PRIMARY KEY (`id`));");


		db.execSQL("CREATE TABLE `descricao` ( `id` INTEGER NOT NULL, `descricao` TEXT NOT NULL, `item_descricao_id` INTEGER NOT NULL,"
				+ " `idiomaid` INTEGER NOT NULL, PRIMARY KEY (`id`),FOREIGN KEY (item_descricao_id) REFERENCES item_descricao(id));");

		db.execSQL("CREATE TABLE `multimidia` ( `id` INTEGER NOT NULL, `url` VARCHAR (255) NOT NULL,"
				+ " `item_descricao_id` INTEGER NOT NULL, PRIMARY KEY (`id`),FOREIGN KEY (item_descricao_id) REFERENCES item_descricao(id));");

		db.execSQL("CREATE TABLE `endereco` ( `id` INTEGER NOT NULL, `bairro` varchar (255) NOT NULL, `cep` varchar (255) NOT NULL,"
				+ "`complemento` varchar (255) NULL, `logradouro` varchar (255) NOT NULL, `numero` varchar (255) NOT NULL,"
				+ " `ponto_localizavel_id` INTEGER NOT NULL, PRIMARY KEY (`id`), FOREIGN KEY (ponto_localizavel_id) REFERENCES ponto_localizavel(id));");

		db.execSQL("CREATE TABLE `info_do_responsavel` ( `id` INTEGER NOT NULL, `email` varchar (255) NOT NULL, `facebook` varchar (255) NULL,"
				+ "`instagram` varchar (255) NULL, `nome` varchar (255) NOT NULL, `website` varchar (255) NULL,"
				+ " `ponto_localizavel_id` INTEGER NOT NULL, PRIMARY KEY (`id`), FOREIGN KEY (ponto_localizavel_id) REFERENCES ponto_localizavel(id));");


		db.execSQL("CREATE TABLE `telefone` ( `id` INTEGER NOT NULL, `cod_area` varchar (255) NOT NULL, `numero` varchar (255) NOT NULL,"
				+ "`responsavel_id` INTEGER NOT NULL, PRIMARY KEY (`id`), FOREIGN KEY (responsavel_id) REFERENCES info_do_responsavel(id));");


		db.execSQL("CREATE TABLE `rota_turistica` ( `id` INTEGER NOT NULL, `tipo_rota` INTEGER NOT NULL, PRIMARY KEY (`id`), FOREIGN KEY (tipo_rota) REFERENCES tipo_rota(id));");

		db.execSQL("CREATE TABLE `ordem_ponto_rota` ( `id` INTEGER NOT NULL, `posicao` INTEGER NOT NULL,"
				+ "`ponto_localizavel_id` INTEGER NOT NULL, `rota_turistica_id` INTEGER NOT NULL, PRIMARY KEY (`id`), FOREIGN KEY (rota_turistica_id) REFERENCES rota_turistica(id));");

		db.execSQL("CREATE TABLE `tipo_rota` ( `id` INTEGER NOT NULL, `nome` varchar (255) NOT NULL, PRIMARY KEY (`id`));");
		
		db.execSQL("CREATE TABLE `polo_turistico` ( `id` INTEGER NOT NULL, `polo` varchar (255) NOT NULL, PRIMARY KEY (`id`));");
		
		db.execSQL("CREATE TABLE `municipio` ( `id` INTEGER NOT NULL, `municipio` varchar (255) NOT NULL, `polo_id` INTEGER NOT NULL, PRIMARY KEY (`id`));");

		db.execSQL("CREATE TABLE `modo_transmissao` ( `_id` INTEGER NOT NULL, `online` INTEGER NOT NULL, PRIMARY KEY (`_id`));");

		ContentValues values = new ContentValues();

		values.put("online", 1);

		db.insert("modo_transmissao", null, values);
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

//		db.execSQL("DROP TABLE IF EXISTS item_descricao");
		//		db.execSQL("DROP TABLE IF EXISTS ponto_localizavel");
		//		db.execSQL("DROP TABLE IF EXISTS servico_turistico");
		//		db.execSQL("DROP TABLE IF EXISTS atracao_turistica");
		//		db.execSQL("DROP TABLE IF EXISTS tipo_servico");
		//		db.execSQL("DROP TABLE IF EXISTS tipo_atracao");
		//		db.execSQL("DROP TABLE IF EXISTS pictograma");
		//		db.execSQL("DROP TABLE IF EXISTS descricao");
		//		db.execSQL("DROP TABLE IF EXISTS multimidia");

//		db.execSQL("CREATE TABLE `item_descricao` ( `id` INTEGER NOT NULL, `nome` VARCHAR (255) NOT NULL,"
//				+ " `municipio` INTEGER NOT NULL, `id_versao` INTEGER NOT NULL, `data_atualizacao` INTEGER NOT NULL, PRIMARY KEY (`id`));");
		
		
		
		
	}
}


//String[] nome = {"Churrascaria Boi D'ouro", "Italia Box", "Tulip Inn", "Expresso 21"}; 
//double[] latitude = {-1.462632,-1.45832, -1.456025,  -1.456669}; 
//double[] longitude = {-48.494942, -48.502613, -48.492957,  -48.493697};
//int[] tipo = {1,1,2,2};
//
//int[] pictograma = {1,1,2,2};
//
//String[] servico = {"RESTAURANTES", "HOTEIS"};
//String[] picto = {"RESTAURANTE", "HOTEL"};
//
//for (int i = 0; i < nome.length; i++){
//
//	ContentValues values = new ContentValues();
//	long lastId = 0;
//
//	values.put("id_versao", 1);
//	values.put("municipio", 1);
//	values.put("nome", nome[i]);
//
//	db.insert("item_descricao", null, values);
//
//	values = new ContentValues();
//
//	String query = "SELECT ROWID from item_descricao order by ROWID DESC limit 1";
//	Cursor c = db.rawQuery(query, null);
//	if (c != null && c.moveToFirst()) {
//		lastId = c.getLong(0);
//	}
//
//	values.put("item_descricaoid", lastId);
//	values.put("latitude", latitude[i]);
//	values.put("longitude", longitude[i]);
//
//	db.insert("ponto_localizavel", null, values);
//
//	query = "SELECT ROWID from ponto_localizavel order by ROWID DESC limit 1";
//	c = db.rawQuery(query, null);
//	if (c != null && c.moveToFirst()) {
//		lastId = c.getLong(0);
//	}
//
//	values = new ContentValues();
//
//	values.put("ponto_localizavelid", lastId);
//	values.put("tipo", tipo[i]);
//
//	db.insert("servico_turistico", null, values);
//}
//
//for(int i =0; i < servico.length; i++){
//	ContentValues values = new ContentValues();
//
//	values.put("servico", servico[i]);
//	values.put("pictograma", i+1);
//
//	db.insert("tipo_servico", null, values);
//
//	values = new ContentValues();
//
//	values.put("pictograma", picto[i]);
//
//	db.insert("pictograma", null, values);
//}

//String[] descricao = {"Inaugurada em 13 de maio de 2000, a Estação das Docas é um dos espaços que mais refletem a região amazônica. O complexo turístico e cultural congrega gastronomia, cultura, moda e eventos nos 500 metros de orla fluvial do antigo porto de Belém. ",
//		"Mercado de ferro importado da Europa no fim do século XIX, tendo sido inaugurado em 1901, é o principal ponto turístico e cultural da cidade, é a maior feira ao ar livre da America Latina",
//		"Marco de fundação da cidade de Belém em 1616 e da colonização da Amazônia pelos Portugueses.",
//		"A Catedral Metropolitana de Belém ou simplesmente Catedral da Sé / Igreja da Sé é a sede da Arquidiocese de Belém, na cidade de Belém no bairro da Cidade Velha. Faz parte integrante do complexo histórico e religioso da cidade velha, denominado Feliz Lusitânia.", 
//"Igreja construída em 1640, originalmente de taipa. Mais tarde, em 1753, foi reconstruído em alvenaria de pedra, com traça do arquiteto italiano Antônio José Landi em estilo barroco."};
//
//String[] multimidia = {"estacao_das_docas", "ver_o_peso", "forte_da_presepio", "igreja_da_se", "igreja_das_merces"};

//String[] nomeAtracao = {"Estação das Docas", "Ver-o-Peso", "Forte do Castelo", "Igreja da Sé", "Igreja das Mercês"};
//double[] latitudeAtracao = {-1.448689,-1.45225, -1.45432, -1.456068, -1.450861}; 
//double[] longitudeAtracao = {-48.500156, -48.503557, -48.505231, -48.504716, -48.501166};
//int[] tipoAtracao = {2,2,2,2,2};
//
//String[] atracao = {"NATURAIS", "CULTURAIS"};
//String[] pictoAtracao = {"NATURAL", "CULTURAL"};
//
//for (int i = 0; i < nomeAtracao.length; i++){
//
//	ContentValues values = new ContentValues();
//	long lastId = 0;
//
//	values.put("id_versao", 1);
//	values.put("municipio", 1);
//	values.put("nome", nomeAtracao[i]);
//
//	db.insert("item_descricao", null, values);
//
//	values = new ContentValues();
//
//	String query = "SELECT ROWID from item_descricao order by ROWID DESC limit 1";
//	Cursor c = db.rawQuery(query, null);
//	if (c != null && c.moveToFirst()) {
//		lastId = c.getLong(0);
//	}
//
//	values.put("item_descricaoid", lastId);
//	values.put("latitude", latitudeAtracao[i]);
//	values.put("longitude", longitudeAtracao[i]);
//
//	db.insert("ponto_localizavel", null, values);
//	
//	values = new ContentValues();
//	
//	values.put("descricao", descricao[i]);
//	values.put("item_descricaoid", lastId);
//	values.put("idioma", 1);
//	db.insert("descricao", null, values);
//	
//	values = new ContentValues();
//	
//	values.put("url", multimidia[i]);
//	values.put("item_descricaoid", lastId);
//	db.insert("multimidia", null, values);
//
//	query = "SELECT ROWID from ponto_localizavel order by ROWID DESC limit 1";
//	c = db.rawQuery(query, null);
//	if (c != null && c.moveToFirst()) {
//		lastId = c.getLong(0);
//	}
//
//	values = new ContentValues();
//
//	values.put("ponto_localizavelid", lastId);
//	values.put("tipo", tipoAtracao[i]);
//
//	db.insert("atracao_turistica", null, values);
//}
//
//for(int i =0; i < atracao.length; i++){
//	ContentValues values = new ContentValues();
//
//	values.put("atracao", atracao[i]);
//	values.put("pictograma", i+3);
//
//	db.insert("tipo_atracao", null, values);
//
//	values = new ContentValues();
//	values.put("pictograma", pictoAtracao[i]);
//
//	db.insert("pictograma", null, values);
//}
