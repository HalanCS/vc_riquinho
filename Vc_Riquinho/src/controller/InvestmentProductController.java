package controller;

import dao.InvestmentProductDAO;
import model.FixedIncomeProduct;
import model.VariableIncomeProduct;
import model.InvestmentProduct;

import java.math.BigDecimal;
import java.util.List;

public class InvestmentProductController {
    
    private InvestmentProductDAO productDao = new InvestmentProductDAO();
    
    
    /**
     * Método para registrar novo produto de investimento no banco de dados
     * @param name
     * @param description
     * @param monthlyYield
     * @param tipo
     * @param gracePeriodDays
     * @return
     */
    public boolean registerNewProduct(String name, String description, BigDecimal monthlyYield, int tipo, Integer gracePeriodDays) {
        String tipoStr;
        
        if (tipo == 1) {
            tipoStr = "FIXED";
            new FixedIncomeProduct(null, name, description, monthlyYield, gracePeriodDays != null ? gracePeriodDays : 0);
        } else if (tipo == 2) {
            tipoStr = "VARIABLE";
            new VariableIncomeProduct(null, name, description, monthlyYield);
        } else {
            System.out.println("Tipo de produto inválido.");
            return false;
        }
        
        boolean sucesso = productDao.cadastrarProduto(name, description, monthlyYield, tipoStr, gracePeriodDays);
        
        if (sucesso) {
            System.out.println("Produto de investimento cadastrado com sucesso!");
            return true;
        }
        
        System.out.println("Erro ao cadastrar produto.");
        return false;
    }
    
    public void listAllProducts() {
    	
    	List<InvestmentProduct> products = productDao.listarTodosViaProcedure();
        if (products == null || products.isEmpty()) {
            System.out.println("Nenhum produto de investimento cadastrado.");
            return;
        }

        System.out.println(" ________________________________________");
        System.out.println("|           LISTA DE INVESTIMENTOS       |");
        System.out.println("|________________________________________|");
        
        for (InvestmentProduct p : products) {
            System.out.print("ID: " + p.getId() + " | Nome: " + p.getName() + " | Rendimento: " + p.getMonthlyYield() + "%");
            
            if (p instanceof FixedIncomeProduct) {
                FixedIncomeProduct fixed = (FixedIncomeProduct) p;
                System.out.println(" | Tipo: Renda Fixa | Carência: " + fixed.getGracePeriodDays() + " dias");
            } else if (p instanceof VariableIncomeProduct) {
                System.out.println(" | Tipo: Renda Variável");
            }
        }
        System.out.println("|________________________________________|");
    }
    
    public boolean updateProduct(String id, String name, String description, BigDecimal monthlyYield, int tipo, Integer gracePeriodDays) {
        String tipoStr = (tipo == 1) ? "FIXED" : "VARIABLE";
        return productDao.atualizarProduto(id, name, description, monthlyYield, tipoStr, gracePeriodDays);
    }

    public boolean deleteProduct(String id) {
        return productDao.deletarProduto(id);
    }
}