import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Collections;
import java.util.List;

class App {

    public static Scanner input = new Scanner(System.in);

    private static final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

    private static final int CURRENT_YEAR = cal.get(Calendar.YEAR);
    private static final int CURRENT_MONTH = cal.get(Calendar.MONTH)+1; //STARTS COUNTING ON 0 = JAN
    private static final int CURRENT_DAY = cal.get(Calendar.DAY_OF_MONTH);

    private static boolean restricitonscheck = false;  //turn false to skip check of restrictions


    private static final String[] MODOS_PAGAMENTOS = {"MB" ,"Pay Pal", "CC"};


    public static void main(String[] args) throws SQLException {
        getCurrentDateAndTime();
        if(restricitonscheck) checkRestrictions();
        optionsMenu();

    }

    private static void optionsMenu() throws SQLException {
        optionsMenuDisplay();
        switch (getValInt()) {
            case 1:
                newReserve();
                break;
            case 2:
                alterViagem();
                break;
            case 3:
                outofservice();
                break;
            case 4:

                break;
            case 5:
                buskilometers();
                break;
            case 6:
                lugaresVazios();
                break;
            case 7:
                sumOfPrice();
                break;
            case 8:
                averageAge();
                break;
            case 9:
                travelintimestamp();
                break;
            case 10:
                typeofpaymentmethod();
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
    }

    private static void checkRestrictions() throws SQLException{
        correctDBerrors();
        checkBilhetesAndCapacity();
        checkHoraChegada();
        checkPagMBway();
        makeSureAPncarruagens6();
    }

    private static void correctDBerrors() throws SQLException{
        if(!queries.checkIfTransporte11Exists()) {
            queries.executeUpdate("SET NOCOUNT ON INSERT INTO TRANSPORTE (ident, viagem, velmaxima, dataentradaservico, atrdiscriminante) " +
                    "VALUES (11, 645, 100, '2020-01-20', 'C')");
            queries.executeUpdate("SET NOCOUNT ON INSERT INTO COMBOIO (transporte, tipo, ncarruagens) VALUES (11, 'IC', 3)");
            queries.executeUpdate("SET NOCOUNT ON INSERT INTO LOCOMOTIVA (nserie, comboio, marca) VALUES (295, 11, 'Roco')");
        }
    }

    private static void checkBilhetesAndCapacity() throws SQLException{
        List<Integer> transp = queries.getActiveTransports();
        Collections.sort(transp);
        sortList(transp);
        for (Integer integer : transp) {
            int lugdisp = -1;
            if (queries.getTransport(integer).equals("autocarro")) lugdisp=queries.getNumLugAutocarro(integer);
            if (queries.getTransport(integer).equals("comboio")) lugdisp=queries.getNumLugComboio(integer);
            if(lugdisp<queries.getNumLugaresOcupados(integer)) System.out.print("DB INCONSISTENT");
        }
    }

    private static void checkHoraChegada() throws SQLException{
        //tempochegada = horapartida + distancia*velmaxima
        List<Integer> viagens = queries.getIdsViagem();
        for (Integer viagem : viagens) {
            int vel = queries.getVelMax(viagem);
            int dist = queries.getDist(viagem);
            //5min margem de erro e arredondamento
            int timeminutes = getMinutes(queries.getHoraPart(viagem)) + dist / vel * 60 + (dist % vel) * 60 / 100 + 5;
            int hour = timeminutes / 60;
            int minute = timeminutes % 60;
            if (hour > 23) {        //limitação DB horachegada>horapartida
                hour = 23;
                minute = 59;
            }
            String timechegada = "";
            if(hour<10) timechegada += "0";
            timechegada += hour + ":";
            if(minute<10) timechegada += "0";
            timechegada += minute + ":00";
            if (!queries.getHoraCheg(viagem).equals(timechegada)) queries.updateDataChegada(viagem, timechegada);
        }
    }

    private static int getMinutes(String time){
        String hora = ""+time.charAt(0)+time.charAt(1);
        String minute = ""+time.charAt(3)+time.charAt(4);
        int horas = Integer.parseInt(hora);
        int minutes = Integer.parseInt(minute);
        minutes += horas*60;
        return minutes;
    }

    private static List<Integer> sortList(List<Integer> list){
        for(int n=1;n<list.size();n++) if(list.get(n).equals(list.get(n - 1))) list.remove(n);
        return list;
    }

    private static void checkPagMBway(){
        String defaultnumtel = "+351999999999";
        List<Integer> reservas = queries.getReservasMB();
        for (Integer reserva : reservas) {
            if (!queries.checkIfExistsinPagMBway(reserva)) queries.insertIntoPagMBway(reserva, defaultnumtel);
        }
    }

    private static void makeSureAPncarruagens6() {
        queries.updatencarruagensAP();
    }

    private static void exit() throws SQLException {
        System.out.println("Confirma Saída do Programa");
        if(checkConsent(true)) System.exit(0);
        else optionsMenu();
    }

    private static boolean checkConsent(boolean print) {
        if(print) System.out.println("prima S para confirmar ou qualquer outra tecla para cancelar");
        char confirmExit = input.next().charAt(0);
        input.nextLine();
        return (confirmExit =='s' || confirmExit =='S');
    }

    private static void newReserve() throws SQLException {
        System.out.println("Nova Reserva");
        //System.out.println("Data da reserva");
        String date = getCurrentDateAndTime();

        System.out.println("Modo de Pagamento");
        System.out.println("Modos de Pagamento permitidos: MB, Pay Pal, CC");
        String modospag = checkIfInArray(MODOS_PAGAMENTOS);

        System.out.println("Meio de Transporte:");
        System.out.println("comboio ou autocarro");
        String meiotransporte = checkIfInArray(new String[]{"comboio", "autocarro"});
        String tipo = "";
        if(meiotransporte.equals("comboio")) tipo = "terminal";
        if(meiotransporte.equals("autocarro")) tipo = "paragem";

        System.out.println("Cidade de Partida:");
        String cidadepart = cidade(tipo);
        String estpart = "";
        System.out.println("Cidade de Chegada:");
        String cidadecheg = cidade(tipo);
        String estcheg = "";

        int idviagem = -1;

       if(queries.checkViagem(estpart, estcheg)) {
           idviagem = queries.getIdViagem(estpart, estcheg);  //viagem exists get viagemid

       }

        // TODO: substituir id viagem por cidade/ estaçao de destino
        // TODO: se não existir tem de se acrescentar viagem

        queries.reserva(date ,modospag ,idviagem);
        if (modospag.equals("MB")){
            System.out.println("Número de telefone:");
            String num = getValString();
            queries.insertIntoPagMBway(queries.getLastInt("ident","RESERVA"), num);
        }
    }

    private static String cidade(String tipo) throws SQLException {
        String estacao = "";
        String cidade = getValString();
        int codpostal = -1;

        if(queries.checkIfCityOnPartida(cidade)){  //CIDADE PARTIDA EXISTE
            System.out.println("Cidade existe na Base de Dados");    //SE A CIDADE EXISTE NA DB PARTIMOS DO PRINCIPIO QUE TEM ESTAÇÕES OU TERMINAIS ATRIBUIDOS
            codpostal = queries.getCodpostal(cidade);
            System.out.println("Escolha uma das seguintes Estações:");    //para simplificar partimos do principio q casdo haja estações vai ser utilizada uma das mesmas
            checkIfInArray(queries.printEstacoesFromLocalidade(cidade));
            // MAYBE ADD CHECK TO STATION TO VERIFY IF IT ALLOWS THE MEANS OF TRANSPORTATION
        }else{  //CIDADE NÃO EXISTE
            System.out.println("Cidade não existe na DataBase");
            System.out.println("Nome Cidade:");
            String newcidade = getValString();
            System.out.println("Código Postal:");
            codpostal = getValInt();        //restrição?    ??generate??
            //COMO A CIDADE NÃO EXISTIA NA DB TB NÃO EXISTEM ESTAÇÕES PARA A MESMA
            addLocalidadeAndStation(cidade, codpostal, tipo);
            //Por default colocamos o nplataforma como 1,pois se a estação n se encontrava na DB antes à plataforma 1 deverá estar desocupada

        }return estacao;
    }

    private static void addLocalidadeAndStation(String cidadePartida, int codpostal, String tipo) throws SQLException{
        queries.addCityToDB(cidadePartida, codpostal);
        queries.addStationToDB(cidadePartida, tipo, 1, codpostal);
    }

    private static void addEstacao() throws SQLException{

    }

    private static void addViagem() throws SQLException{
        System.out.println("Adicionar Viagem");
        System.out.println("Data Viagem:");

        System.out.println("Hora Partida:");
        System.out.println("Hora Chegada:");    //calculate
        System.out.println("distancia");    //calculate
        System.out.println("");
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

    private static void outofservice() throws SQLException{
        System.out.println("Colocar um autocarro fora de serviço");
        System.out.println("Que autocarro deseja verificar?  ll-nn-ll, l-> letra, n-> numero ");
        String matricula = null;
        do {
            matricula = input.nextLine();
        }while (matricula == "");
        System.out.println("Confirma a sua escolha?");
        if (checkConsent(true)) {
            if(!queries.checkOutOfService());
            else {
                queries.busrelatedtuples(matricula);
            }


        }

    }


    private static void buskilometers() throws SQLException{
        System.out.println("Número de kilometragem de um autocarro");
        System.out.println("Que autocarro deseja verificar?  ll-nn-ll, l-> letra, n-> numero ");
        String matricula = null;
        do {
            matricula = input.nextLine();
        }while (matricula == "");
        System.out.println("Confirma a sua escolha?");
        if (checkConsent(true)){
            String sum = queries.getsumofkilometers(matricula);
            if (sum == "" || sum == null){
                System.out.println("O autocarro escolhido não fez nenhuma viagem até ao momento");
            }else {
                System.out.println("Kilometragem do autocarro: " + sum + "Km");
            }
        }
    }

    private static void lugaresVazios() throws SQLException{
        System.out.println("Lugares Vazios nos transportes que partem de determinada cidade");
        System.out.println("Que cidade deseja verificar?");
        String cidade = null;
        do {
            cidade = input.nextLine();
        }while (cidade == "");
        System.out.println("Confirma a sua escolha?");
        if (checkConsent(true)){
            System.out.println("Números de lugares disponiveis na cidade selecionada:");
            System.out.println("  Transporte | NºLugares Vazios");
            queries.getlugaresfromcidade(cidade);
        }

    }


    private static void sumOfPrice() throws SQLException{
        System.out.println("Soma de preços para certa categoria");
        System.out.println("Que categoria quer selecionar? crianca, jovem, adulto, senior, militar");
        String categoria = null;
        do {
            categoria = input.nextLine();
        }while (categoria == "");
        if (checkConsent(true)) {
            String sum = queries.getsumofprice(categoria);
            if (sum == "" || sum == null){
                System.out.println("Não foram vendidos bilhetes desta categoria");
            }else {
                System.out.println("Soma do valor dos bilhetes: " + sum + "€");
            }
        }
    }

    private static void averageAge(){
        System.out.println("Média de idades por reserva");
        System.out.println("Confirma a sua escolha?");
        if (checkConsent(true)){
            System.out.println("      Reserva |      Data da reserva      | Media de idades");
            queries.avgbyreserve();
        }
    }

    private static void travelintimestamp() {
        System.out.println("Viagens entre localidades numa janela de tempo");
        String localpartida = "";
        String localchegada = "";
        String horapartida = "";
        String horachegada = "";
        System.out.println("Localidade de partida?");
        do {
            localpartida = input.nextLine();
        } while (localpartida.equals(""));
        System.out.println("Localidade de chegada?");
        do {
            localchegada = input.nextLine();
        } while (localchegada.equals(""));
        System.out.println("Hora de partida?  HH:MM:SS");
        do {
            horapartida = input.nextLine();
        } while (horapartida.equals(""));
        System.out.println("Hora de chegada?  HH:MM:SS");
        do {
            horachegada = input.nextLine();
        } while (horachegada.equals(""));

        if (checkConsent(true)) {
            System.out.println("Viagens entre " + localpartida + " e " + localchegada + " desde " + horapartida + " ás " + horachegada);
            System.out.println("    Est.Partida |   Est.Chegada   |     Viagem      ");
            queries.travelsbytimestamp(localpartida,localchegada,horapartida,horachegada);
        }
    }

    private static void typeofpaymentmethod(){
        System.out.println("Média de idades por método de pagamento");
        System.out.println("Confirma a sua escolha?");
        if (checkConsent(true)){
            System.out.println("    Pagamento  |   Média de Idades");
            queries.avgbypayment();
        }
    }

    private static String getCurrentDateAndTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
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
        }while(var < min);
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
        int val = input.nextInt();
        input.nextLine();   //consume rest of line

        return val;

    }

    private static String getValString(){
        System.out.print("> ");
        return input.nextLine();
    }


}
