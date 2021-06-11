import java.sql.SQLException;
import java.util.Calendar;
import java.util.Scanner;
import java.util.TimeZone;

class App {

    public static Scanner input = new Scanner(System.in);

    private static final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

    private static final int CURRENT_YEAR = cal.get(Calendar.YEAR);
    private static final int CURRENT_MONTH = cal.get(Calendar.MONTH)+1; //STARTS COUNTING ON 0 = JAN
    private static final int CURRENT_DAY = cal.get(Calendar.DAY_OF_MONTH);

    private static final String[] MODOS_PAGAMENTOS = {"MB" ,"Pay Pal", "CC"};


    public static void main(String[] args) throws SQLException {
        optionsMenu();

    }

    private static void optionsMenu() throws SQLException {
        optionsMenuDisplay();
        switch (input.nextInt()) {
            case 1:
                newReserve();
                break;
            case 2:
                alterViagem();
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
        System.out.println("4. Lista Autocarros em Serviço e Fora de Serviço");
        System.out.println("5. Número Total de Quilómetros de um Autocarro (d)");
        System.out.println("6. Número de Lugares Vazios dos Autocarros ou Comboios que Partiram de Determinada Cidade (2d)");
        System.out.println("7. Soma dos Preços de Bilhete para a Categoria de Adultos (2f)");
        System.out.println("8. Média de Idades dos Passageiros por Reserva (2g)");
        System.out.println("9. Viagens (3c)"); //entre duas cidades numa determinada janela temporal
        System.out.println("10. Tipo de Pagamento pela Média de Idades dos Passageiros que Efectuaram a Reserva (3d)");
        System.out.println("11. Sair");
        System.out.print("> ");
    }

    private static void exit() throws SQLException {
        System.out.println("Confirma Saída do Programa");
        if(checkConsent(true)) System.exit(0);
        else optionsMenu();
    }

    private static boolean checkConsent(boolean print) {
        if(print) System.out.println("prima S para confirmar ou qualquer outra tecla para cancelar");
        char confirmExit = input.next().charAt(0);
        return (confirmExit =='s' || confirmExit =='S');
    }

    private static void newReserve() throws SQLException {
        System.out.println("Nova Reserva");
        System.out.println("Data da reserva");
        //String date = getDateAndTime();

        System.out.println("Modo de Pagamento");
        System.out.println("Modos de Pagamento permitidos: MB, Pay Pal, CC");
        String modospag = checkIfInArray(MODOS_PAGAMENTOS);

        System.out.println("Cidade de Partida:");
        String cidadePartida = getValString();
        if(queries.checkIfIfCityOnPartida(cidadePartida)){  //CIDADE PARTIDA EXISTE
            System.out.println("Cidade existe na DataBase");    //SE A CIDADE EXISTE NA DB PARTIMOS DO PRINCIPIO QUE TEM ESTAÇÕES OU TERMINAIS ATRIBUIDOS
            System.out.println("Com as seguintes Estações:");
            queries.printEstacoesFromLocalidade(cidadePartida);
        }else{  //CIDADE PARTIDA NÃO EXISTE
            System.out.println("Cidade não existe na DataBase");
            queries.addCityToDB(cidadePartida); //COMO A CIDADE NÃO EXISTIA NA DB TB NÃO EXISTEM ESTAÇÕES PARA A MESMA
            
            // TODO: add estação
            // TODO: repetir para chegada passar para método para tal
        }

        // TODO: substituir id viagem por cidade/ estaçao de destino
        // TODO: se não existir tem de se acrescentar viagem
        // TODO: ?? imprimir destinos possiveis, mesmo assim se n existir o destino temos de o acrescentar

        //queries.reserva(date ,modopagamento ,idviagem);
    }

    private static void alterViagem() throws SQLException{
        System.out.println("Alterar Viagem");
        System.out.println("Deseja Ver Viagens Disponiveis?");
        if(checkConsent(true)){
            System.out.println("Viagens Disponiveis:");
            queries.availableViagem();
        }

        System.out.println("Viagem a Alterar");     //CONSIDERAMOS QUE A VIAGEM EXISTE E SÓ PODE SER SELECIONADA UMA DAS VIAGENS EXISTENTES
        System.out.println("Escolha de entre as viagens disponiveis, através do digito que a precede");
        // TODO: GET MAX VALUE
        //checkBetweenBoundaries(1,max);

    }

    private static String getDateAndTime(){
        System.out.println("Ano");
        int year = checkIfAboveMin(CURRENT_YEAR);
        System.out.println("Mês");
        System.out.println("Entre 1 e 12");
        System.out.println("1 = JAN  12 = DEZ");
        int month;
        if(year == CURRENT_YEAR ) month = checkBetweenBoundaries(CURRENT_MONTH, 12);
        else month = checkBetweenBoundaries(1, 12);
        System.out.println("Dia");
        int lastdaymonth = lastDayMonth(month, year);
        System.out.println("Entre 1 e " + lastdaymonth);
        int day = checkBetweenBoundaries(1, lastdaymonth);
        System.out.println("Hora");
        int hour = checkBetweenBoundaries(0,23);
        System.out.println("Minutos");
        int minutes = checkBetweenBoundaries(0,59); // não se justifica colocar segundos
        return getStringDate(year,month,day,hour,minutes);
    }

    private static String getDate(){
        System.out.println("Ano");
        int year = checkIfAboveMin(CURRENT_YEAR);
        System.out.println("Mês");
        System.out.println("Entre 1 e 12");
        System.out.println("1 = JAN  12 = DEZ");
        int month;
        if(year == CURRENT_YEAR ) month = checkBetweenBoundaries(CURRENT_MONTH, 12);
        else month = checkBetweenBoundaries(1, 12);
        System.out.println("Dia");
        int lastdaymonth = lastDayMonth(month, year);
        System.out.println("Entre 1 e " + lastdaymonth);
        int day = checkBetweenBoundaries(1, lastdaymonth);
        return getStringDate(year,month,day);
    }

    private static String getTime(){
        System.out.println("Hora");
        int hour = checkBetweenBoundaries(0,23);
        System.out.println("Minutos");
        int minutes = checkBetweenBoundaries(0,59); // não se justifica colocar segundos
        return getStringTime(hour,minutes);
    }

    public static int lastDayMonth(int month, int year){
        int lastday = -1;
        int[] lastdayarray = {-1, 31, -1, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if(month == 2) {
            Calendar testcal = Calendar.getInstance();
            testcal.set(Calendar.YEAR, year);
            if (cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365) lastday = 29;
            else lastday = 28;
        }
        if(month != 2) lastday = lastdayarray[month];
        return lastday;
    }

    public static String getStringDate(int year, int month, int day, int hour, int minutes){
        String date = year + "-";
        if(checkIfBelowMax(month,10)) date += month + "-";
        else date += "0" + month + "-";
        if(checkIfBelowMax(day,10)) date += day + " ";
        else date += "0" + day + " ";
        if(checkIfBelowMax(hour,10)) date += hour + ":";
        else date += "0" + hour + ":";
        if(checkIfBelowMax(minutes,10)) date += minutes;
        else date += "0" + minutes;
        date += ":00";
        return date;
    }

    public static String getStringDate(int year, int month, int day){
        String date = year + "-";
        if(checkIfBelowMax(month,10)) date += month + "-";
        else date += "0" + month + "-";
        if(checkIfBelowMax(day,10)) date += day;
        else date += "0" + day;
        return date;
    }

    public static String getStringTime(int hour, int minutes){
        String date = "";
        if(checkIfBelowMax(hour,10)) date += hour + ":";
        else date += "0" + hour + ":";
        if(checkIfBelowMax(minutes,10)) date += minutes;
        else date += "0" + minutes;
        date += ":00";
        return date;
    }

    public static int checkBetweenBoundaries(int min, int max) {
        int var;
        do{
            var = getValInt();
        }while(var < min || var > max);
        return var;
    }

    public static boolean checkBetweenBoundaries(int var, int min, int max) {return (var < min || var > max);}

    public static int checkIfAboveMin(int min) {
        int var;
        do{
            var = getValInt();
        }while(var <= min);
        return var;
    }

    public static boolean checkIfAboveMin(int var, int min) {return var < min;}

    public static int checkIfBelowMax(int max) {
        int var;
        do{
            var = getValInt();
        }while(var >= max);
        return var;
    }

    public static boolean checkIfBelowMax(int var, int max) {return var >= max;}

    public static String checkIfInArray(String[] array){
        String var;
        input.nextLine();
        do{
            var = getValString();
        }while (!checkIfInArray(var, array));
        return var;
    }

    public static boolean checkIfInArray(String var, String[] array){
        for (String s : array) if (s.equals(var)) return true;
        return false;
    }

    private static boolean stringCheckCityChegadaInDB() throws SQLException{
        String var = getValString();
        return queries.checkIfIfCityOnChegada(var);
    }

    private static int getValInt(){
        System.out.print("> ");
        return input.nextInt();
    }

    private static String getValString(){
        System.out.print("> ");
        return input.nextLine();
    }


}
