import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class queries {
    private static final String URL = "jdbc:sqlserver://10.62.73.87:1433;user=L3NG_1;password=L3NG_1;databaseName=L3NG_1";
    private static Connection con;
    private static Statement stmt = null;
    private static PreparedStatement pstmt = null;

    private static ResultSet rs = null;
    

    public static void main(String[] args) throws SQLException {    //USED FOR TESTS DELETE ON END
        /*getNumLugaresAutocarro("IVECO","PRO");
        List<String> list = getAutocarrosActive();
        int numAutocarros = list.size()/2;
        System.out.println(getIdViagemWithAutocarroOrComboio(true));
        System.out.println(list);*/

        List<Integer> list = getIdsViagem();
        int numviagens = list.size();
        for(int n=0;n<numviagens;n++) {
            System.out.print(list.get(n) + "  ");
            System.out.print(getTransport(list.get(n)) + "  ");
            if(getTransport(list.get(n)) == "autocarro") System.out.println(getNumLugAutocarro(list.get(n)) + "  ");
            if(getTransport(list.get(n)) == "comboio") System.out.println(getNumLug1Comboio(list.get(n)) + "  " +
                    getNumLug2Comboio(list.get(n)));
        }

    }

    // TODO: verificar restrições na base de dados já feita no inicio da execução do programa

    private static void connect() throws SQLException {
        try {
            con = DriverManager.getConnection(URL); //Estabelecer a ligacao
        } catch (SQLException sqlex) {
            System.out.println("Erro : " + sqlex.getMessage());
        }
    }

    public static void executeUpdate(String querry) throws SQLException{
        try {
            connect();
            stmt = con.createStatement();
            int n = stmt.executeUpdate(querry);
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }
    }

    public static int getNumCols(String table) throws SQLException{
        int val = -1;
        try {
            connect();
            pstmt = con.prepareStatement("SELECT COUNT(*) FROM ?");
            pstmt.setString(1,table);
            rs = pstmt.executeQuery();
            rs.next();
            val = rs.getInt(1);
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return val;
    }

    public static int getNumLugAutocarro(int idviagem) throws SQLException{
        int numlug = -1;
        try {
            connect();
            pstmt = con.prepareStatement("SELECT nlugares FROM ((VIAGEM JOIN TRANSPORTE ON " +
                    "VIAGEM.ident = viagem) JOIN AUTOCARRO ON TRANSPORTE.ident = AUTOCARRO.transporte)" +
                    "JOIN AUTOCARROTIPO ON AUTOCARRO.marca = AUTOCARROTIPO.marca AND AUTOCARRO.modelo = " +
                    "AUTOCARROTIPO.modelo WHERE atrdiscriminante = 'A' AND VIAGEM.ident = ?");
            pstmt.setInt(1,idviagem);
            rs = pstmt.executeQuery();
            rs.next();
            numlug = rs.getInt(1);
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return numlug;
    }

    public static int getNumLug1Comboio(int idviagem) throws SQLException{
        int numlug1 = -1;
        try {
            connect();
            pstmt = con.prepareStatement("SELECT nlugclasse1 FROM ((VIAGEM JOIN TRANSPORTE ON " +
                            "VIAGEM.ident = viagem) JOIN COMBOIO ON TRANSPORTE.ident = COMBOIO.transporte) JOIN " +
                            "COMBOIOTIPO ON COMBOIO.tipo = COMBOIOTIPO.id WHERE atrdiscriminante = 'C' AND " +
                            "VIAGEM.ident = ?");
            pstmt.setInt(1,idviagem);
            rs = pstmt.executeQuery();
            rs.next();
            numlug1 = rs.getInt(1);
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return numlug1;
    }

    public static int getNumLug2Comboio(int idviagem) throws SQLException{
        int numlug2 = -1;
        try {
            connect();
            pstmt = con.prepareStatement("SELECT nlugclasse2 FROM ((VIAGEM JOIN TRANSPORTE ON " +
                    "VIAGEM.ident = viagem) JOIN COMBOIO ON TRANSPORTE.ident = COMBOIO.transporte) JOIN " +
                    "COMBOIOTIPO ON COMBOIO.tipo = COMBOIOTIPO.id WHERE atrdiscriminante = 'C' AND " +
                    "VIAGEM.ident = ?");
            pstmt.setInt(1,idviagem);
            rs = pstmt.executeQuery();
            rs.next();
            numlug2 = rs.getInt(1);
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return numlug2;
    }

    public static String getTransport(int idviagem) {
        String meiotransporte = "";
        try {
            connect();
            pstmt = con.prepareStatement("SELECT atrdiscriminante FROM VIAGEM JOIN TRANSPORTE ON " +
                    "VIAGEM.ident = viagem WHERE VIAGEM.ident = ?");
            pstmt.setInt(1, idviagem);
            rs = pstmt.executeQuery();
            rs.next();
            meiotransporte = rs.getString(1);
            if(meiotransporte.equals("A")) meiotransporte = "autocarro";
            if(meiotransporte.equals("C")) meiotransporte = "comboio";
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return meiotransporte;
    }

    public static List<Integer> getIdsViagem() {
        List<Integer> idsviagem = new ArrayList<Integer>();
        try {
            connect();
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT VIAGEM.ident FROM VIAGEM");
            while(rs.next()) idsviagem.add(rs.getInt(1));
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return idsviagem;
    }

    public static List<String> getAutocarrosActive() throws SQLException{   //NÃO IMPLEMENTA LISTA AUTOCARROS EXISTENTES FORA DE SERVIÇO
        List<String> marcaAndModelo = new ArrayList<>();    // Lista armazena marca em posições pares e modelo em posições impares
        try {
            connect();
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT marca, modelo FROM AUTOCARRO");
            while (rs.next()){
                marcaAndModelo.add(rs.getString(1));
                marcaAndModelo.add(rs.getString(2));
            }
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return marcaAndModelo;
    }

    public static List<Integer> getIdViagemWithAutocarroOrComboio(boolean meiotransp) throws SQLException{
        //  meiotransp true para Autocarro e false para Comboio
        List<Integer> idsviagem = new ArrayList<Integer>();
        char vardisc = 'z';
        if (meiotransp) vardisc = 'A';
        if (!meiotransp) vardisc = 'C';
        try {
            connect();
            pstmt = con.prepareStatement("SELECT viagem FROM TRANSPORTE WHERE atrdiscriminante = ?");
            pstmt.setString(1, String.valueOf(vardisc));
            rs = pstmt.executeQuery();
            while(rs.next()) idsviagem.add(rs.getInt(1));
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return idsviagem;
    }

    public static void getNumLugaresAutocarro(String marca, String modelo) throws SQLException{
        try {
            connect();
            pstmt = con.prepareStatement("SELECT nlugares FROM AUTOCARRO JOIN " +
                    "AUTOCARROTIPO ON (AUTOCARRO.modelo=AUTOCARROTIPO.modelo AND AUTOCARRO.marca=AUTOCARROTIPO.marca) " +
                    "WHERE AUTOCARRO.marca = ? AND AUTOCARRO.modelo = ?");
            pstmt.setString(1,marca);
            pstmt.setString(2,modelo);
            rs = pstmt.executeQuery();
            rs.next();
            System.out.println(rs.getInt(1));
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }
    }

    public static void reserva(String datares, String modopag, int idviagem) throws SQLException{
        try {
            connect();
            pstmt = con.prepareStatement("INSERT INTO RESERVA (ident, datareserva, modopagamento, viagem) " +
                    "VALUES (?,?,?,?)");
            pstmt.setInt(1, maxIdentReserva()+1);    //ident reserva
            pstmt.setString(2, datares);    //data da reserva
            pstmt.setString(3, modopag);    //modopagamento
            pstmt.setInt(4, idviagem);    //ident viagem
            pstmt.executeUpdate();
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }
    }

    public static void alterViagem(Date data, Time timepart, Time timecheg, int dist, String estpart, String estcheg) throws SQLException{
        try {
            connect();
            pstmt = con.prepareStatement("UPDATE VIAGEM SET ident = ?, dataviagem = ?, horapartida = ?," +
                    " horachegada  = ?, distancia = ?, estpartida = ?, estchegada = ?");
            pstmt.setInt(1,getLastInt("ident","VIAGEM")+1);
            pstmt.setDate(2, data);
            pstmt.setTime(3, timepart);
            pstmt.setTime(4, timecheg);
            pstmt.setInt(5, dist);
            pstmt.setString(6, estpart);
            pstmt.setString(7, estcheg);
            pstmt.executeUpdate();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }
    }

    public static void availableViagem() throws  SQLException{
        try {
            connect();
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT  ident, dataviagem, horapartida, horachegada, distancia, estpartida, estchegada FROM VIAGEM");
            ResultSetMetaData rsmd = rs.getMetaData();
            //int columnsNumber = rsmd.getColumnCount();
            System.out.println("| Ident | Data Viagem | Hora Part | Hora Cheg |  Dist |    Estação Part |    Estação Cheg |");
            while (rs.next()) {
                System.out.print("|" + fillGap(rs.getInt("ident"),6) + " |  ");
                System.out.print(rs.getDate("dataviagem") + " |  ");
                System.out.print(rs.getTime("horapartida") + " |  ");
                System.out.print(rs.getTime("horachegada") + " | ");
                System.out.print(fillGap(rs.getInt("distancia"),5) + " | ");
                System.out.print(fillGap(rs.getString("estpartida"),15) + " | ");
                System.out.println(fillGap(rs.getString("estchegada"),15) + " | ");
            }
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }
    }

    public static void getLocalPartAndChegada() throws SQLException{
        try{
            connect();
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT ident, dataviagem, horapartida, horachegada, estpartida, b.nome cidadepart, estchegada, LOCALIDADE.nome cidadecheg "
                + "FROM VIAGEM JOIN ESTACAO ON estchegada = nome JOIN LOCALIDADE ON ESTACAO.localidade = LOCALIDADE.codpostal "
                + "JOIN ESTACAO a ON estpartida = a.nome JOIN LOCALIDADE b ON a.localidade = b.codpostal");
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }
    }

    public static boolean checkIfIfCityOnPartida(String element) throws SQLException{
        boolean bol = false;
        try{
            connect();
            pstmt = con.prepareStatement("SELECT 1 FROM VIAGEM JOIN ESTACAO ON estpartida = nome " +
                    "JOIN LOCALIDADE ON ESTACAO.localidade = LOCALIDADE.codpostal WHERE LOCALIDADE.nome = ?");
            pstmt.setString(1, element);
            rs = pstmt.executeQuery();
            bol = rs.next();
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }
        return bol;
    }

    public static boolean checkIfIfCityOnChegada(String element) throws SQLException{
        boolean bol = false;
        try{
            connect();
            pstmt = con.prepareStatement("SELECT 1 FROM VIAGEM JOIN ESTACAO ON estchegada = nome " +
                    "JOIN LOCALIDADE ON ESTACAO.localidade = LOCALIDADE.codpostal WHERE LOCALIDADE.nome = ?");
            pstmt.setString(1, element);
            rs = pstmt.executeQuery();
            bol = rs.next();
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }
        return bol;
    }

    public static void addCityToDB(String cidade, int codpostal) throws SQLException{
        try{
            connect();
            pstmt = con.prepareStatement("INSERT INTO LOCALIDADE (codpostal, nome) VALUES ( ?, ?)");
            pstmt.setInt(1, codpostal);
            pstmt.setString(2, cidade);
            pstmt.executeUpdate();
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }
    }

    public static void addStationToDB(String nome, String tipo, int nplataforma, int localidade) throws SQLException{
        try{
            connect();
            pstmt = con.prepareStatement("INSERT INTO ESTACAO (nome, tipo, nplataforma, localidade) VALUES ( ?, ?, ?, ?)");
            pstmt.setString(1, nome);
            pstmt.setString(2, tipo);
            pstmt.setInt(3, nplataforma);
            pstmt.setInt(4, localidade);
            pstmt.executeUpdate();
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }
    }

    public static int printEstacoesFromLocalidade(String cidade){
        int numStations = -1;
        try{
            connect();
            pstmt = con.prepareStatement("SELECT ESTACAO.nome FROM ESTACAO JOIN  LOCALIDADE ON " +
                    "ESTACAO.localidade = LOCALIDADE.codpostal WHERE LOCALIDADE.nome = ?");
            pstmt.setString(1, cidade);
            rs = pstmt.executeQuery();
            numStations = (printTable(rs,1));
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }
        return numStations;
    }

    public static int maxIdentReserva() throws SQLException{   //STATEMENT NOT PREPARED NOT PROTECTED FROM SQL INJECTION
        int maxInt = -1;
        try{
            connect();
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT MAX(ident) FROM RESERVA");
            rs.next();
            maxInt = rs.getInt(1);
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return maxInt;
    }

    public static boolean checkIfInDBwithStmt(String attribute, String table, String element) throws SQLException{
        boolean bol = false;
        try{
            connect();
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT 1 FROM " + table + " WHERE " + attribute + " = " + element);
            bol = rs.next();
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return bol;
    }

    //IS STMT
    public static int getCodpostal(String element) throws SQLException{
        int val = -1;
        try{
            connect();
            pstmt = con.prepareStatement("SELECT codpostal FROM LOCALIDADE WHERE nome = ?");
            pstmt.setString(1, element);
            rs = pstmt.executeQuery();
            rs.next();
            val = rs.getInt("codpostal");
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return val;
    }

    public static boolean checkIfInDBwithPstmt(String attribute, String table, String element) throws SQLException{
        boolean bol = false;
        try{
            connect();
            pstmt = con.prepareStatement("SELECT 1 FROM ? WHERE ? = ?");
            pstmt.setString(1, table);
            pstmt.setString(2, attribute);
            pstmt.setString(3, element);
            rs = pstmt.executeQuery();
            bol = rs.next();
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return bol;
    }

    public static void getlugaresfromcidade(String local){
        String query = "SELECT H.transporte, (nlugares - ocupados) as vazios\n" +
                "FROM (SELECT nlugares, transporte\n" +
                "     FROM AUTOCARROTIPO join\n" +
                "         (SELECT modelo, transporte\n" +
                "         FROM AUTOCARRO join\n" +
                "             (SELECT TRANSPORTE.ident\n" +
                "             FROM TRANSPORTE join\n" +
                "                 (SELECT ident\n" +
                "                 FROM VIAGEM join\n" +
                "                     (SELECT ESTACAO.nome\n" +
                "                     FROM ESTACAO join\n" +
                "                         LOCALIDADE L on ESTACAO.localidade = L.codpostal\n" +
                "                         WHERE L.nome = ?)\n" +
                "                         as A on estpartida = A.nome)\n" +
                "                         as B on B.ident = viagem\n" +
                "                         WHERE atrdiscriminante = 'A')\n" +
                "                         as C on transporte = C.ident)\n" +
                "                         as D on AUTOCARROTIPO.modelo = D.modelo) as H join\n" +
                "     (SELECT F.transporte, COUNT(F.transporte) as ocupados\n" +
                "     FROM BILHETE join\n" +
                "         (SELECT transporte\n" +
                "          FROM LUGAR join\n" +
                "              (SELECT TRANSPORTE.ident\n" +
                "              FROM TRANSPORTE join\n" +
                "                 (SELECT ident\n" +
                "                 FROM VIAGEM join\n" +
                "                     (SELECT ESTACAO.nome\n" +
                "                     FROM ESTACAO join\n" +
                "                         LOCALIDADE L on ESTACAO.localidade = L.codpostal\n" +
                "                         WHERE L.nome = ?)\n" +
                "                         as A on estpartida = A.nome)\n" +
                "                         as B on B.ident = viagem\n" +
                "                         WHERE atrdiscriminante = 'A')\n" +
                "                         as E on transporte = E.ident)\n" +
                "                         as F on F.transporte = BILHETE.transporte\n" +
                "                         GROUP BY (F.transporte)) as G on H.transporte = G.transporte\n";
        try{
            connect();
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, local);
            pstmt.setString(2, local);
            rs = pstmt.executeQuery();
            while(rs.next())printTable(rs,2);
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }
    }

    private static String fillGap(int number ,int spaces) {
        int numberOfDigits=0;
        String stringNum = "";
        int num = number;
        for(int i=1; num>0; i++) {
            num /= 10;
            numberOfDigits = i;
        }spaces -= numberOfDigits;
        for(int n = spaces; n>0; n--) stringNum += " ";
        return stringNum+= number;
    }

    private static String fillGap(String line ,int spaces) {
        String finalString = "";
        for(int n=0; spaces>line.length()+n; n++){
            finalString += " ";
        }return finalString+=line;
    }

    public static int getLastInt(String attribute, String table) throws SQLException{   //STATEMENT NOT PREPARED NOT PROTECTED FROM SQL INJECTION
        int maxInt = -1;
        try{
            connect();
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT MAX(" + attribute + ") FROM " + table);
            rs.next();
            maxInt = rs.getInt(1);
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return maxInt;
    }

    public static int printTable(ResultSet rs, int columnsNumber) throws SQLException {
        int numRows = 1;
        while (rs.next()) {
            for(int i = 1 ; i <= columnsNumber; i++){
                if(i==1) System.out.print(numRows + " > ");
                System.out.print(rs.getString(i) + " "); //Print one element of a row
            }System.out.println();//Move to the next line to print the next row.
            numRows++;
        }return numRows-1;
    }

    private static void closeConnection() throws SQLException {
        if (rs != null) rs.close(); //libertar os recursos do ResultSet
        if (stmt != null) stmt.close(); //libertar os recursos do Statement
        if (pstmt != null) pstmt.close(); //libertar os recursos do Prepared Statement
        if (con != null) con.close(); //fechar ligacao
    }

    public static void closeResources() throws SQLException {
        if (rs != null) rs.close(); //libertar os recursos do ResultSet
        if (stmt != null) stmt.close(); //libertar os recursos do Statement
        if (pstmt != null) pstmt.close(); //libertar os recursos do Prepared Statement
    }
}