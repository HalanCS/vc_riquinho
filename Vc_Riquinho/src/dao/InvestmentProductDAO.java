package dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.FixedIncomeProduct;
import model.InvestmentProduct;
import model.VariableIncomeProduct;

public class InvestmentProductDAO extends BaseDAO {

    public boolean cadastrarProduto(String name, String description, BigDecimal monthlyYield, String tipo, Integer gracePeriodDays) {
        String sql = "{CALL CADASTRO_INVESTMENT_PRODUCT(?, ?, ?, ?, ?)}";
        
        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
             
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setBigDecimal(3, monthlyYield);
            stmt.setString(4, tipo);
            
            if (gracePeriodDays != null) {
                stmt.setInt(5, gracePeriodDays);
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            
            stmt.execute();
            return true;
            
        } catch (SQLException e) {
            System.out.println("Erro no DAO ao cadastrar produto de investimento: " + e.getMessage());
            return false;
        }
    }
    
    public List<InvestmentProduct> listarTodosViaProcedure() {
        List<InvestmentProduct> produtos = new ArrayList<>();
        String sql = "{CALL LISTAR_INVESTMENT_PRODUCTS()}";

        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("investment_name");
                String description = rs.getString("investment_description");
                BigDecimal yield = rs.getBigDecimal("monthlyYield");
                String tipo = rs.getString("tipo");

                if ("FIXED".equalsIgnoreCase(tipo)) {
                    int graceDays = rs.getInt("gracePeriodDays");
                    produtos.add(new FixedIncomeProduct(id, name, description, yield, graceDays));
                } else if ("VARIABLE".equalsIgnoreCase(tipo)){
                    produtos.add(new VariableIncomeProduct(id, name, description, yield));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro no DAO ao listar produtos via procedure: " + e.getMessage());
        }

        return produtos;
    }
    
    public boolean atualizarProduto(String id, String name, String description, BigDecimal monthlyYield, String tipo, Integer gracePeriodDays) {
        String sql = "{CALL ATUALIZAR_INVESTMENT_PRODUCT(?, ?, ?, ?, ?, ?)}";
        
        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
             
            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.setString(3, description);
            stmt.setBigDecimal(4, monthlyYield);
            stmt.setString(5, tipo);
            
            if (gracePeriodDays != null) {
                stmt.setInt(6, gracePeriodDays);
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
            
            stmt.execute();
            return true;
            
        } catch (SQLException e) {
            System.out.println("Erro no DAO ao atualizar produto: " + e.getMessage());
            return false;
        }
    }

    public boolean deletarProduto(String id) {
        String sql = "{CALL DELETAR_INVESTMENT_PRODUCT(?)}";
        
        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
             
            stmt.setString(1, id);
            stmt.execute();
            return true;
            
        } catch (SQLException e) {
            System.out.println("Erro no DAO ao deletar produto: " + e.getMessage());
            return false;
        }
    }
}