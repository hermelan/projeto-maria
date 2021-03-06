package br.senai.sc.edu.projetomaria.dao;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.senai.sc.edu.projetomaria.resource.Messages;

public class EstimativaDAO extends AbstractDAO {
	private static final Logger LOGGER = LogManager.getLogger();

	public List<Integer> listarSKU() {
		String sql = "SELECT SKU FROM PRODUTO WHERE SKU IN (SELECT SKU FROM HISTORICO WHERE MES_ANO > (SELECT DATE_ADD(SYSDATE(),INTERVAL -6 MONTH))) group by SKU;";
		List<Integer> listSku = new ArrayList<>();

		try (Connection conn = getConnection();
				Statement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery(sql);) {

			while (rs.next()) {
				Integer sku;
				sku = rs.getInt("SKU");
				listSku.add(sku);
			}
		} catch (SQLException e) {
			LOGGER.info(Messages.BD_ERRO_CONEXAO);
		}
		return listSku;
	}

	public List<Integer> listaHistorico(Integer sku) {
		String sql = "SELECT QUANTIDADE FROM HISTORICO WHERE SKU IN (SELECT SKU_PHASE_OUT FROM SKU_PHASE WHERE SKU_PHASE_IN = "
				+ sku + ") OR SKU = " + sku + " ORDER BY MES_ANO ASC";

		try (Connection conn = getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery();) {

			List<Integer> listaHistorico = new ArrayList<>();
			while (rs.next()) {

				Integer skuHistorico;
				skuHistorico = rs.getInt("QUANTIDADE");
				listaHistorico.add(skuHistorico);
			}
			return listaHistorico;

		} catch (SQLException e) {
			LOGGER.info(Messages.BD_ERRO_CONEXAO);
		}
		return Collections.emptyList();
	}
}
