import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class queries {
    private static final String URL = "jdbc:sqlserver://10.62.73.87:1433;user=L3NG_1;password=L3NG_1;databaseName=L3NG_1";
    private static Connection con = null;
    private static Statement stmt = null;
    private static PreparedStatement pstmt = null;
    private static ResultSet rs = null;

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

    public static boolean checkIfTransporte11Exists(){
        boolean exists = false;
        try {
            connect();
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT 1 FROM TRANSPORTE WHERE ident=11");
            if(rs.next())exists = exists=true;
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return exists;
    }

    public static boolean checkIfMBWAYexists(){
        boolean exists = false;
        try {
            connect();
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT 1 FROM RESERVA WHERE modopagamento ='MBWAY'");
            if(rs.next())exists = exists=true;
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return exists;
    }

    public static List<Integer> getActiveTransports() throws SQLException{
        List<Integer> activetransports = new ArrayList<Integer>();
        try {
            connect();
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT viagem FROM BILHETE JOIN LUGAR ON BILHETE.nlugar = LUGAR.numero JOIN " +
                    "TRANSPORTE ON LUGAR.transporte = TRANSPORTE.ident");
            while(rs.next()) activetransports.add(rs.getInt(1));
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return activetransports;
    }

    public static int getNumLugaresOcupados(int numlugaresocupados) throws SQLException{
        int val = -1;
        try {
            connect();
            pstmt = con.prepareStatement("SELECT COUNT(BILHETE.transporte) FROM BILHETE JOIN LUGAR ON " +
                    "BILHETE.nlugar = LUGAR.numero JOIN TRANSPORTE ON LUGAR.transporte = TRANSPORTE.ident " +
                    "WHERE viagem = ?");
            pstmt.setInt(1, numlugaresocupados);
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

    public static int getNumLugComboio(int idviagem) throws SQLException{
        int numlug1 = -1;
        int numlug2 = -1;
        try {
            connect();
            pstmt = con.prepareStatement("SELECT nlugclasse1, nlugclasse2 FROM ((VIAGEM JOIN TRANSPORTE ON " +
                            "VIAGEM.ident = viagem) JOIN COMBOIO ON TRANSPORTE.ident = COMBOIO.transporte) JOIN " +
                            "COMBOIOTIPO ON COMBOIO.tipo = COMBOIOTIPO.id WHERE atrdiscriminante = 'C' AND " +
                            "VIAGEM.ident = ?");
            pstmt.setInt(1,idviagem);
            rs = pstmt.executeQuery();
            rs.next();
            numlug1 = rs.getInt(1);
            numlug2 = rs.getInt(2);
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return numlug1+numlug2;
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

    public static int getIdViagem(String estpart, String estcheg) {
        int idviagem = -1;
        try {
            connect();
            pstmt = con.prepareStatement("SELECT ident FROM VIAGEM WHERE estpartida = ? AND estchegada = ?");
            pstmt.setString(1, estpart);
            pstmt.setString(2, estcheg);
            rs = pstmt.executeQuery();
            rs.next();
            idviagem = rs.getInt(1);
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return idviagem;
    }

    public static int getVelMax(int viagem) throws SQLException{
        int velmax = -1;
        try {
            connect();
            pstmt = con.prepareStatement("SELECT velmaxima FROM VIAGEM JOIN TRANSPORTE ON VIAGEM.ident = viagem" +
                    " WHERE viagem = ?");
            pstmt.setInt(1,viagem);
            rs = pstmt.executeQuery();
            rs.next();
            velmax = rs.getInt(1);
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return velmax;
    }

    public static int getDist(int viagem) throws SQLException{
        int velmax = -1;
        try {
            connect();
            pstmt = con.prepareStatement("SELECT distancia FROM VIAGEM WHERE ident = ?");
            pstmt.setInt(1,viagem);
            rs = pstmt.executeQuery();
            rs.next();
            velmax = rs.getInt(1);
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return velmax;
    }

    public static String getHoraPart(int viagem) throws SQLException{
        Time horapart = null;
        String horapartida = "";
        try {
            connect();
            pstmt = con.prepareStatement("SELECT horapartida FROM VIAGEM WHERE ident = ?");
            pstmt.setInt(1,viagem);
            rs = pstmt.executeQuery();
            rs.next();
            horapart = rs.getTime(1);
            closeConnection();
            horapartida = "" + horapart;
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return horapartida;
    }

    public static String getHoraCheg(int viagem) throws SQLException{
        Time horacheg = null;
        String horachegada = "";
        try {
            connect();
            pstmt = con.prepareStatement("SELECT horachegada FROM VIAGEM WHERE ident = ?");
            pstmt.setInt(1,viagem);
            rs = pstmt.executeQuery();
            rs.next();
            horacheg = rs.getTime(1);
            closeConnection();
            horachegada = "" + horacheg;
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return horachegada;
    }

    public static void updateDataChegada(int viagem, String time){
        try {
            connect();
            pstmt = con.prepareStatement("UPDATE VIAGEM SET horachegada  = ? WHERE ident = ?");
            pstmt.setTime(1, Time.valueOf(time));
            pstmt.setInt(2, viagem);
            pstmt.executeUpdate();
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }
    }

    public static List<Integer> getReservasMBWAY(){
        List<Integer> list = new ArrayList<Integer>();
        try {
            connect();
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT ident FROM RESERVA WHERE modopagamento = 'MBWAY'");
            while(rs.next()) list.add(rs.getInt(1));
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return list;
    }

    public static boolean checkIfExistsinPagMBway(int reserva){
        boolean exists = false;
        try {
            connect();
            pstmt = con.prepareStatement("SELECT 1 FROM PAGMBWAY WHERE reserva = ?");
            pstmt.setInt(1,reserva);
            rs = pstmt.executeQuery();
            if(rs.next()) exists = true;
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return exists;
    }

    public static void insertIntoPagMBway(int reserva, String telefone){
        try {
            connect();
            pstmt = con.prepareStatement("INSERT INTO PAGMBWAY (reserva, telefone) VALUES (?, ?)");
            pstmt.setInt(1, reserva);
            pstmt.setString(2, telefone);
            pstmt.execute();
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }
    }

    public static void updatencarruagensAP(){
        try {
            connect();
            stmt = con.createStatement();
            stmt.executeUpdate("UPDATE COMBOIO " +
                    "SET ncarruagens=6 WHERE tipo = 'AP'");
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

    public static void createViagem(String data, String timepart, String timecheg, int dist, String estpart, String estcheg) throws SQLException{
        try {
            connect();
            pstmt = con.prepareStatement("INSERT INTO EMPLOYEE (ident, dataviagem, horapartida, horachegada, " +
                    "distancia, estpartida, estchegada) VALUES (?,?,?,?,?,?,?)");
            pstmt.setInt(1,getLastInt("ident","VIAGEM")+1);
            pstmt.setDate(2, Date.valueOf(data));
            pstmt.setTime(3, Time.valueOf(timepart));
            pstmt.setTime(4, Time.valueOf(timecheg));
            pstmt.setInt(5, dist);
            pstmt.setString(6, estpart);
            pstmt.setString(7, estcheg);
            pstmt.executeUpdate();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }
    }

    public static void alterViagem(int ident, String  data, String timepart, String timecheg, int dist, String estpart, String estcheg) throws SQLException{
        try {
            connect();
            pstmt = con.prepareStatement("UPDATE VIAGEM SET ident = ?, dataviagem = ?, horapartida = ?," +
                    " horachegada  = ?, distancia = ?, estpartida = ?, estchegada = ?");
            pstmt.setInt(1,ident);
            pstmt.setDate(2, Date.valueOf(data));
            pstmt.setTime(3, Time.valueOf(timepart));
            pstmt.setTime(4, Time.valueOf(timecheg));
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

    public static boolean checkIfCityOnPartida(String element) throws SQLException{
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

    public static List<String> printEstacoesFromLocalidade(String cidade){
        List<String> stations = new ArrayList<String>();
        try{
            connect();
            pstmt = con.prepareStatement("SELECT ESTACAO.nome FROM ESTACAO JOIN  LOCALIDADE ON " +
                    "ESTACAO.localidade = LOCALIDADE.codpostal WHERE LOCALIDADE.nome = ?");
            pstmt.setString(1, cidade);
            rs = pstmt.executeQuery();
            for(int n=0; rs.next(); n++) {
                stations.add(rs.getString(1));
                System.out.println(rs.getString(1));
            }
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }
        return stations;
    }

    public static int maxIdentReserva() throws SQLException{   //STATEMENT NOT PREPARED NOT PROTECTED FROM SQL INJECTION
        int maxInt = -1;
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT MAX(ident) FROM RESERVA");
        rs.next();
        maxInt = rs.getInt(1);
        return maxInt;
    }

    public static boolean checkViagem(String estpart, String estcheg){
        boolean exists = false;
        try{
            connect();
            pstmt = con.prepareStatement("SELECT 1 FROM VIAGEM WHERE estpartida = ? AND estchegada = ?");
            pstmt.setString(1, estpart);
            pstmt.setString(2, estcheg);
            rs = pstmt.executeQuery();
            if(rs.next()) exists=true;
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }return exists;
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

    public static boolean checkOutOfService(){
        String query = "SELECT matricula\n" +
                "FROM FORADESERVICO";

        Boolean exists = true;

        try{
            connect();
            pstmt = con.prepareStatement(query);
            rs = pstmt.executeQuery();
            closeConnection();

        }catch(SQLException sqlex1) {
            System.out.println("Não existe nenhuma tabela para os autcarros fora de serviço...");
            exists = false;
            System.out.println("Esta será agora adicionada á nossa base de dados!");

            query = "CREATE TABLE FORADESERVICO (\n" +
                    "    matricula nchar(8) check([matricula] like ('[A-Z][A-Z]-[0-9][0-9]-[A-Z][A-Z]')),\n" +
                    "\tdatarevisao date not null check (DATEDIFF(day, getdate(),datarevisao) > 0),\n" +
                    "\tmarca nchar(10),\n" +
                    "\tmodelo nchar(6),\n" +
                    "\tprimary key (matricula),\n" +
                    "    foreign key (marca,modelo) references AUTOCARROTIPO(marca, modelo)\n" +
                    "\n" +
                    ")";

            try{
                connect();
                pstmt = con.prepareStatement(query);
                pstmt.executeUpdate();
                closeConnection();
                System.out.println("A tabela terá sido gerada, por favor tente novamente");
            }catch(SQLException sqlex2) {
                System.out.println("Error: " + sqlex2.getMessage());
            }
        }
        return exists;
    }

    public static void busrelatedtuples(String matricula){
        String query = "SELECT DISTINCT BILHETE.transporte, V.ident, matricula, datarevisao, marca, modelo\n" +
                "FROM BILHETE join AUTOCARRO A on BILHETE.transporte = A.transporte join TRANSPORTE T on T.ident = A.transporte join VIAGEM V on V.ident = T.viagem\n" +
                "WHERE matricula = ?";

        try{
            connect();
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, matricula);
            rs = pstmt.executeQuery();
            rs.next();
            int transporte = rs.getInt(1);
            int viagem = rs.getInt(2);
            matricula = rs.getString(3);
            String datarevisao = rs.getString(4);
            String marca = rs.getString(5);
            String modelo = rs.getString(6);
            closeConnection();

            try {
                String query1 = "DELETE FROM BILHETE\n" +
                                "WHERE transporte = ?";

                connect();
                pstmt = con.prepareStatement(query1);
                pstmt.setInt(1, transporte);
                pstmt.executeUpdate();
                closeConnection();

            }catch(SQLException sqlex) {
                System.out.println("Erro: " + sqlex.getMessage());
            }

            try {
                String query1 = "DELETE FROM LUGAR\n" +
                        "WHERE transporte = ?";

                connect();
                pstmt = con.prepareStatement(query1);
                pstmt.setInt(1, transporte);
                pstmt.executeUpdate();
                closeConnection();

            }catch(SQLException sqlex) {
                System.out.println("Erro: " + sqlex.getMessage());
            }

            try {
                String query1 = "DELETE FROM VIAGEM\n" +
                        "WHERE ident = ?";

                connect();
                pstmt = con.prepareStatement(query1);
                pstmt.setInt(1, viagem);
                pstmt.executeUpdate();
                closeConnection();

            }catch(SQLException sqlex) {
                System.out.println("Erro: " + sqlex.getMessage());
            }

            try {
                String query1 = "insert into FORADESERVICO values\n" +
                        "            (?,?,?,?)";

                connect();
                pstmt = con.prepareStatement(query1);
                pstmt.setString(1, matricula);
                pstmt.setString(2, datarevisao);
                pstmt.setString(3, marca);
                pstmt.setString(4, modelo);
                pstmt.executeUpdate();
                closeConnection();

            }catch(SQLException sqlex) {
                System.out.println("Erro: " + sqlex.getMessage());
            }


        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }



    }

    public static String getsumofkilometers(String matricula){
        String query = "SELECT matricula, sum(distancia) as kilometragem\n" +
                "FROM AUTOCARRO JOIN TRANSPORTE T on T.ident = AUTOCARRO.transporte JOIN VIAGEM V on V.ident = T.viagem\n" +
                "WHERE (datediff(day ,CURRENT_TIMESTAMP,dataviagem) < 0 or (datediff(day ,CURRENT_TIMESTAMP,dataviagem) = 0 and datediff(second , cast(CURRENT_TIMESTAMP as time (0)), horachegada ) <= 0)) and matricula = ?\n" +
                "group by matricula";

        String soma = "";

        try{
            connect();
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, matricula);
            rs = pstmt.executeQuery();
            rs.next();
            soma = rs.getString(2);
            closeConnection();

        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }
        return soma;

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
                "                         GROUP BY (F.transporte)) as G on H.transporte = G.transporte";
        try{
            connect();
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, local);
            pstmt.setString(2, local);
            rs = pstmt.executeQuery();
            printTable(rs,2);
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }
    }

    public static String getsumofprice(String categoria){
        String soma = null;
        String query = "SELECT sum(preco) as soma\n" +
                "FROM LUGARTIPO\n" +
                "WHERE nome = ?\n" +
                "GROUP BY nome";
        try{
            connect();
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, categoria);
            rs = pstmt.executeQuery();
            rs.next();
            soma = rs.getString(1);
            closeConnection();

        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }
        return soma;
    }

    public static void avgbyreserve(){
        String query = "SELECT B.reserva, B.datareserva, AVG(B.idade) as average\n" +
                "FROM (SELECT reserva, datareserva, (YEAR(current_timestamp) - YEAR(dtnascimento)) as idade\n" +
                "     FROM PASSAGEIRO join\n" +
                "        (SELECT reserva, datareserva, passageiro\n" +
                "        FROM BILHETE join RESERVA R2 on BILHETE.reserva = R2.ident)\n" +
                "            as A on passageiro = nid) as B\n" +
                "GROUP BY B.reserva, B.datareserva";

        try{
            connect();
            pstmt = con.prepareStatement(query);
            rs = pstmt.executeQuery();

            printTable(rs,3);
            closeConnection();

        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }

    }

    public static void travelsbytimestamp(String localpartida, String localchegada, String horapartida, String horachegada){
        String query = "SELECT  estpartida , estchegada, Viagem.ident viagem \n" +
                "\tFROM VIAGEM JOIN ESTACAO ON estchegada = nome JOIN LOCALIDADE ON ESTACAO.localidade = LOCALIDADE.codpostal \n" +
                "\tJOIN ESTACAO a ON estpartida = a.nome JOIN LOCALIDADE b ON a.localidade = b.codpostal \n" +
                "\tWHERE LOCALIDADE.nome = ? AND b.nome = ? AND horapartida >= ? AND horachegada <= ?";

        try{
            connect();
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, localchegada);
            pstmt.setString(2, localpartida);
            pstmt.setString(3, horapartida);
            pstmt.setString(4, horachegada);
            rs = pstmt.executeQuery();
            printTable(rs,3);
            closeConnection();
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }

    }

    public static void avgbypayment(){
        String query = "SELECT modopagamento, AVG(datediff(YEAR , PASSAGEIRO.dtnascimento, CURRENT_TIMESTAMP)) AS avg_age_by_payment_method\n" +
                "\tFROM (PASSAGEIRO JOIN BILHETE ON nid = passageiro JOIN RESERVA ON reserva = ident)\n" +
                "\tGROUP BY modopagamento;";

        try{
            connect();
            pstmt = con.prepareStatement(query);
            rs = pstmt.executeQuery();

            printTable(rs,2);
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
                if(i==1) System.out.print(numRows + " >     ");
                System.out.print(rs.getString(i) + "      "); //Print one element of a row
            }System.out.println();//Move to the next line to print the next row.
            numRows++;
        }
        if(numRows == 1) System.out.println("Não existem valores para a interrogação feitas");
        return numRows-1;
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