import java.sql.SQLException;
import java.util.Scanner;
import java.util.Date;

class App {

    public static Scanner input = new Scanner(System.in);

    public static void main(String[] args) throws SQLException {
        optionsMenu();


    }

    private static void optionsMenu() throws SQLException {
        optionsMenuDisplay();
        switch ( input.nextInt() ) {
            case 1:
                break;
            case 2:

                break;
            case 3:

                break;
            case 4:

                break;
            case 5:

                break;
            case 6:

                break;
            case 7:

                break;
            case 8:

                break;
            case 9:

                break;
            case 10:

                break;
            case 11:
                exit();
                break;
            default:
                System.err.println("Opção não reconhecido");
                break;
        }
    }

    private static void optionsMenuDisplay() {
        System.out.println("Reserva viagens");
        System.out.println();
        System.out.println("1. Nova Reserva (a)");
        System.out.println("2. Alterar Viagem (b)");
        System.out.println("3. Colocar Autocarro Fora de Serviço (c)");
        System.out.println("4. Lista Autocarros e se se Encontram ou Não em Serviço");
        System.out.println("5. Número Total de Quilómetros de um Autocarro (d)");
        System.out.println("6. Número de Lugares Vazios dos Autocarros ou Comboios que Partiram de Determinada Cidade (2d)");
        System.out.println("7. Soma dos Preços de Bilhete para a Categoria de Adultos (2f)");
        System.out.println("8. Média de Idades dos Passageiros por Reserva (2g)");
        System.out.println("9. Viagens (3c)"); //entre duas cidades numa determinada janela temporal
        System.out.println("10. Tipo de Pagamento pela Média de Idades dos Passageiros que Efectuaram a Reserva (3d)");
        System.out.println("11. Sair");
        System.out.print(">");
    }

    private static void exit() throws SQLException {
        System.out.println("Confirma Saída do Programa");
        System.out.println("prima S para confirmar ou qualquer outra tecla para retornar ao menu");
        char confirmExit = input.next().charAt(0);
        if(confirmExit =='s' || confirmExit =='S') System.exit(0);
        else optionsMenu();
    }

    private static void newReserve() throws SQLException {
        System.out.println("Nova Reserva");
        System.out.println("Data da reserva");
        Date d = new Date();
        int year = input.nextInt();
        //while(year < d.getYear() ){} // is deprecated need best way to get year
        int month = input.nextInt();
        int day = input.nextInt();
        int hour = input.nextInt();
        int minutes = input.nextInt();
        int seconds = input.nextInt();
    }

}
