import java.sql.*;

public class queries {
    private static final String URL = "jdbc:sqlserver://10.62.73.87:1433;user=L3NG_1;password=L3NG_1;databaseName=L3NG_1";
    private static Connection con = null;
    private static Statement stmt = null;
    private static PreparedStatement pstmt = null;

    private static ResultSet rs = null;
    

    public static void main(String[] args) throws SQLException {
        //test();
        //System.out.println(getLastInt("ident","RESERVA"));
    }

    public static void connect() throws SQLException {
        try {
            con = DriverManager.getConnection(URL); //Estabelecer a ligacao
        } catch (SQLException sqlex) {
            System.out.println("Erro : " + sqlex.getMessage());
        }
    }

    public static void test() throws SQLException {
        int test = -1;

        try {
            connect();
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT max(nid) FROM PASSAGEIRO");
            rs.next();
            test = rs.getInt(1);
            rs.close();
            stmt.close();
            System.out.println(test);
        }catch(SQLException sqlex) {
            System.out.println("Erro: " + sqlex.getMessage());
        }
    }

    public static void reserva(Date datares, String modopag, int idviagem) throws SQLException{
        try {
            connect();
            pstmt = con.prepareStatement("INSERT INTO RESERVA " +
                    "(ident, datareserva, modopagamento, viagem)" +
                    " VALUES (?,?,?,?)");
            pstmt.setInt(1, getLastInt("ident","RESERVA")+1);    //ident reserva
            pstmt.setDate(2, datares);    //data da reserva
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
            pstmt = con.prepareStatement("UPDATE VIAGEM" +
                    "SET ident = ?, dataviagem = ?, horapartida = ?, horachegada  = ?, " +
                    "distancia = ?, estpartida = ?, estchegada = ?");
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


    public static int getLastInt(String attribute, String table) throws SQLException{
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
        }
        return maxInt;
    }




    public static void closeConnection() throws SQLException {
        if (rs != null) rs.close(); //libertar os recursos do ResultSet
        if (stmt != null) stmt.close(); //libertar os recursos do Statement
        if (stmt != null) stmt.close(); //libertar os recursos do Prepared Statement
        if (con != null) con.close(); //fechar ligacao
    }

    public static void closeResources() throws SQLException {
        if (rs != null) rs.close(); //libertar os recursos do ResultSet
        if (stmt != null) stmt.close(); //libertar os recursos do Statement
        if (stmt != null) stmt.close(); //libertar os recursos do Prepared Statement
    }
}
