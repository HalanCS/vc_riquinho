package view;

import controller.ClientController;
import controller.InvestmentProductController;
import model.Client;

import java.math.BigDecimal;
import java.util.Scanner;

public class ViewInterface {
    public static void main(String args[]) {
        Scanner scan = new Scanner(System.in); 
        ClientController controller = new ClientController();
        int control = 0;
        
        do {
            System.out.println("\tSISTEMA BANCARIO");
            System.out.println(" __________________________________________");
            System.out.println("|                                          |");
            System.out.println("| Digite para selecionar                   |");
            System.out.println("| 1. Fazer Login                           |");
            System.out.println("| 2. Realizar Cadastro                     |");
            System.out.println("| 3. Controle de produtos de investimento  |");
            System.out.println("| 4. Sair                                  |");
            System.out.println("|__________________________________________|");
            System.out.print("Escolha uma opção: ");
            
            while (!scan.hasNextInt()) {
                System.out.println("Por favor, digite um número válido!");
                scan.next();
            }
            control = scan.nextInt();
            scan.nextLine(); // Consome a quebra de linha pendente
            
            switch (control) {
                case 1: 
                    System.out.print("Nome: ");
                    String nomeLogin = scan.nextLine();
                    System.out.print("Senha: ");
                    String senhaLogin = scan.nextLine();
                    
                    Client cl = controller.clientLogin(nomeLogin, senhaLogin);
                    if (cl == null) {
                        break;
                    }
                    int menuContaControl = 0;
                   
                    do {
                        System.out.println(" ________________________________________");
                        System.out.println("|                                        |");
                        System.out.println("| 1. Cadastrar nova conta                |");
                        System.out.println("| 2. Listar contas vinculadas            |");
                        System.out.println("| 3. Verificar saldo                     |");
                        System.out.println("| 4. Calcular rendimento                 |");
                        System.out.println("| 5. Verificar taxa de servico           |");    
                        System.out.println("| 6. Realizar depósito                   |");
                        System.out.println("| 7. Editar informações cadastrais       |");
                        System.out.println("| 8. Sair (Voltar ao menu principal)     |");
                        System.out.println("|________________________________________|");
                        
                        System.out.print("Escolha uma opção: ");
                        while (!scan.hasNextInt()) {
                            System.out.println("Por favor, digite um número válido!");
                            scan.next();
                        }
                        menuContaControl = scan.nextInt();
                        scan.nextLine(); 
                        
                        switch (menuContaControl) {
                            case 1:
                                System.out.println(" ________________________________________");
                                System.out.println("|                                        |");
                                System.out.println("|           CADASTRAR NOVA CONTA         |");
                                System.out.println("|________________________________________|");
                                System.out.println("| 1. Conta Corrente                      |");
                                System.out.println("| 2. Conta CDI                           |");
                                System.out.println("| 3. Conta Auto-Investimento             |");
                                System.out.println("|________________________________________|");
                                System.out.print("Selecione o tipo de conta desejada: ");
                                
                                while (!scan.hasNextInt()) {
                                    System.out.println("Por favor, digite um número válido!");
                                    scan.next();
                                }
                                int tipoCont = scan.nextInt();
                                scan.nextLine(); // Consome o ENTER
                                
                                controller.registerNewAccount(cl, tipoCont);
                                break;
                                
                            case 2:
                                System.out.println(" ________________________________________");
                                System.out.println("|                                        |");
                                System.out.println("|       CONTAS VINCULADAS AO CLIENTE     |");
                                System.out.println("|________________________________________|");
                                controller.listAccounts(cl);
                                System.out.println("|________________________________________|");
                                break;
                                
                            case 3:
                                System.out.println(" ________________________________________");
                                System.out.println("|                                        |");
                                System.out.println("|            CONSULTA DE SALDO           |");
                                System.out.println("|________________________________________|");
                                System.out.println("ID da conta: ");
                                String idConta = scan.nextLine();
                                controller.consultaSaldo(cl, idConta);
                                break;
                                
                            case 4:
                                System.out.println(" ________________________________________");
                                System.out.println("|                                        |");
                                System.out.println("|          CALCULAR RENDIMENTO           |");
                                System.out.println("|________________________________________|");
                                System.out.println("| - 30 DIAS                              |");
                                System.out.println("| - 60 DIAS                              |");
                                System.out.println("| - 90 DIAS                              |");
                                System.out.println("| - 120 DIAS                             |");
                                System.out.println("|________________________________________|");
                                System.out.print("Digite o id da conta: ");
                                idConta = scan.nextLine();
                                
                                System.out.print("Digite a quantidade de dias: ");
                                while (!scan.hasNextInt()) {
                                    System.out.println("Por favor, digite um número válido!");
                                    scan.next();
                                }
                                int quantidadeDias = scan.nextInt();
                                scan.nextLine();

                                controller.calcularRendimento(cl, idConta, quantidadeDias);
                                break;
                                
                            case 5:
                                System.out.println(" ________________________________________");
                                System.out.println("|                                        |");
                                System.out.println("|        CONSULTAR TAXA DE SERVIÇO       |");
                                System.out.println("|________________________________________|");
                                System.out.print("Digite o id da conta: ");
                                idConta = scan.nextLine();
                                
                                System.out.print("Digite a quantidade de dias: ");
                                while (!scan.hasNextInt()) {
                                    System.out.println("Por favor, digite um número válido!");
                                    scan.next();
                                }
                                quantidadeDias = scan.nextInt();
                                scan.nextLine();

                                controller.consultaTaxaServico(cl, idConta, quantidadeDias);
                                break;
                                
                            case 6:
                                System.out.println(" ________________________________________");
                                System.out.println("|                                        |");
                                System.out.println("|           REALIZAR DEPÓSITO            |");
                                System.out.println("|________________________________________|");
                                System.out.print("Digite o id da conta: ");
                                idConta = scan.nextLine();
                                
                                System.out.print("Digite o valor a ser depositado: ");
                                String entradaValor = scan.nextLine();
                                entradaValor = entradaValor.replace(",", ".");
                                
                                try {
                                    BigDecimal montante = new BigDecimal(entradaValor);
                                    controller.realizarDeposito(cl, idConta, montante);
                                } catch (NumberFormatException e) {
                                    System.out.println("Valor inválido! Digite um número válido.");
                                }
                                break;
                                
                            case 7:
                                System.out.println(" ________________________________________");
                                System.out.println("|                                        |");
                                System.out.println("|      EDITAR INFORMAÇÕES CADASTRAIS     |");
                                System.out.println("|________________________________________|");
                                System.out.println("Dica: Pressione ENTER sem digitar nada para manter o dado atual.");
                                
                                System.out.print("\nNovo nome (Atual: " + cl.getName() + "): ");
                                String novoNome = scan.nextLine();
                                
                                System.out.print("Novo email (Atual: " + cl.getEmail() + "): ");
                                String novoEmail = scan.nextLine();
                                
                                System.out.print("Nova senha: ");
                                String novaSenha = scan.nextLine();
                                
                                controller.updateClientData(cl, novoNome, novoEmail, novaSenha);
                                break;
                                
                            case 8:
                                System.out.println(" ________________________________________");
                                System.out.println("|                                        |");
                                System.out.println("|      Encerrando sessão de " + cl.getName() + "   |");
                                System.out.println("|________________________________________|");
                                
                                controller.updateClientAndAccounts(cl); 
                                cl = null;
                                break;
                                
                            default:
                                System.out.println("Opção inválida! Escolha entre 1 e 8.");
                        }
                        
                        if (menuContaControl != 8) {
                            System.out.println("\nPressione ENTER para continuar...");
                            scan.nextLine(); 
                        }
                        
                    } while (menuContaControl != 8);
                    break;
                
                case 2:
                    System.out.println(" ________________________________________");
                    System.out.println("|                                        |");
                    System.out.println("|          CADASTRO DE CLIENTE           |");
                    System.out.println("|________________________________________|");
                    
                    System.out.print("Nome: ");
                    String nomeCadastro = scan.nextLine();
                    
                    System.out.print("Email: ");
                    String emailCadastro = scan.nextLine();
                    
                    System.out.print("Senha: ");
                    String senhaCadastro = scan.nextLine();
                    
                    System.out.print("Tipo (PF | PJ): ");
                    String tipoCadastro = scan.nextLine().toUpperCase();
                    
                    String docLabel = tipoCadastro.equals("PJ") ? "CNPJ" : "CPF";
                    System.out.print(docLabel + ": ");
                    String documentoCadastro = scan.nextLine();
                    
                    controller.registerNewClient(nomeCadastro, emailCadastro, senhaCadastro, documentoCadastro, tipoCadastro);
                    control = 0; 
                    break;
                    
                case 3:
                    int menuInvestControl = 0;
                    do {
                        System.out.println(" ________________________________________");
                        System.out.println("|                                        |");
                        System.out.println("| CONTROLE DE PRODUTOS DE INVESTIMENTO   |");
                        System.out.println("|________________________________________|");
                        System.out.println("| 1. Cadastrar novo produto de renda     |");
                        System.out.println("| 2. Listar Produtos de investimento     |");
                        System.out.println("| 3. Atualizar produto                   |");
                        System.out.println("| 4. Deletar produto                     |");
                        System.out.println("| 5. Voltar ao menu principal            |");
                        System.out.println("|________________________________________|");
                        System.out.print("Escolha uma opção: ");
                        
                        while (!scan.hasNextInt()) {
                            System.out.println("Por favor, digite um número válido!");
                            scan.next();
                        }
                        menuInvestControl = scan.nextInt();
                        scan.nextLine(); 
                        
                        InvestmentProductController invController = new InvestmentProductController();
        
                        switch (menuInvestControl) {
                            case 1:
                                System.out.print("Nome do produto: ");
                                String nomeProd = scan.nextLine();
                                
                                System.out.print("Descrição: ");
                                String descProd = scan.nextLine();
                                
                                System.out.print("Rendimento mensal (% ou valor): ");
                                BigDecimal rendimento = new BigDecimal(scan.nextLine().replace(",", "."));
                                
                                System.out.println("Tipo de Renda:");
                                System.out.println("1. Renda Fixa (Fixed Income)");
                                System.out.println("2. Renda Variável (Variable Income)");
                                System.out.print("Escolha o tipo: ");
                                
                                while (!scan.hasNextInt()) {
                                    System.out.println("Por favor, digite um número válido!");
                                    scan.next();
                                }
                                int tipoProd = scan.nextInt();
                                
                                Integer gracePeriod = null;
                                if (tipoProd == 1) {
                                    System.out.print("Dias de carência (Grace Period): ");
                                    while (!scan.hasNextInt()) {
                                        System.out.println("Por favor, digite um número válido!");
                                        scan.next();
                                    }
                                    gracePeriod = scan.nextInt();
                                }
                                scan.nextLine();
                                
                                invController.registerNewProduct(nomeProd, descProd, rendimento, tipoProd, gracePeriod);
                                break;
                                
                            case 2:
                                invController.listAllProducts();
                                break;
                                
                            case 3:
                                System.out.print("Digite o ID do produto que deseja atualizar (ex: INV001): ");
                                String idUp = scan.nextLine();
                                
                                System.out.print("Novo nome: ");
                                String nomeUp = scan.nextLine();
                                
                                System.out.print("Nova descrição: ");
                                String descUp = scan.nextLine();
                                
                                System.out.print("Novo rendimento mensal (%): ");
                                BigDecimal rendUp = new BigDecimal(scan.nextLine().replace(",", "."));
                                
                                System.out.print("Tipo (1 - Renda Fixa | 2 - Renda Variável): ");
                                while (!scan.hasNextInt()) {
                                    System.out.println("Por favor, digite um número válido!");
                                    scan.next();
                                }
                                int tipoUp = scan.nextInt();
                                
                                Integer graceUp = null;
                                if (tipoUp == 1) {
                                    System.out.print("Novos dias de carência: ");
                                    while (!scan.hasNextInt()) {
                                        System.out.println("Por favor, digite um número válido!");
                                        scan.next();
                                    }
                                    graceUp = scan.nextInt();
                                }
                                scan.nextLine();
                                
                                invController.updateProduct(idUp, nomeUp, descUp, rendUp, tipoUp, graceUp);
                                System.out.println("Produto atualizado com sucesso!");
                                break;
                                
                            case 4:
                                System.out.print("Digite o ID do produto que deseja deletar (ex: INV001): ");
                                String idDel = scan.nextLine();
                                
                                invController.deleteProduct(idDel);
                                System.out.println("Produto removido com sucesso!");
                                break;
                                
                            case 5:
                                // Voltar ao menu principal
                                break;
                                
                            default:
                                System.out.println("Opção inválida!");
                                break;
                        }
                        
                        if (menuInvestControl != 5) {
                            System.out.println("\nPressione ENTER para continuar...");
                            scan.nextLine();
                        }
                    } while (menuInvestControl != 5);
                    
                    control = 0;
                    break;
                    
                case 4:
                    System.out.println(" ________________________________________");
                    System.out.println("|                                        |");
                    System.out.println("|         Encerrando o sistema...        |");
                    System.out.println("|________________________________________|");
                    break;
                    
                default:
                    System.out.println("Opção inválida!");
                    control = 0;
            }
        } while (control != 4);
        
        scan.close(); 
    }
}