import java.sql.Statement;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DaoGenerico {
    private Connection conn;
    private Statement st;
    private final String TB_PREFIX = "tb_";
    private final String TB_SUFFIX = "s";


    private void conectar() {
        try {
            this.conn = GerenciarConexao.pegarConexao();
            st = conn.createStatement();
        } catch (ClassNotFoundException e1) {
            System.out.println(e1.getMessage());
        } catch (SQLException e2) {
            System.out.println(e2.getMessage());
        }
    }

    private void desconectar() {
        try {
            st.close();
            conn.close();
        } catch (SQLException e2) {
            System.out.println(e2.getMessage());
        }
    }


    public void inserir (Object o) throws Exception {
        Class c = o.getClass();
        Field fields[] = c.getDeclaredFields();

        // Montar o comando
        String comando = "INSERT INTO " + TB_PREFIX + c.getSimpleName().toLowerCase() 
            + TB_SUFFIX;

        String campos = "(";
        String valores = "VALUES(";
        boolean separar = false;
        
        for (Field f : fields) {
            if (separar) {
                campos = campos + ", ";
                valores = valores + ", ";
            }
            campos = campos + f.getName();
            valores = valores + "?";
            separar = true;

        }

        campos = campos + ") ";
        valores = valores + ") ";

        comando = comando + campos + valores;
        System.out.println(comando);

        // Criar statement e setar valores nos parametros
        this.conectar();
        PreparedStatement pst = conn.prepareStatement(comando, Statement.RETURN_GENERATED_KEYS);
        
        int numeroParametro = 0;
        for (Field f : fields) {
            numeroParametro++;

            f.setAccessible(true);

            if(f.getType().isAssignableFrom(String.class)){
                System.out.println("Tipo: " + f.getType());
                if (f.get(o) != null){
                    pst.setString(numeroParametro, f.get(o).toString()); 
                } else {
                    pst.setString(numeroParametro, "");
                }
            } 
            
            else if (f.getType().isAssignableFrom(Integer.class) || f.getType().isAssignableFrom(Integer.TYPE)) {
                System.out.println("Tipo: " + f.getType());
                if (f.get(o) != null){
                    pst.setInt(numeroParametro, Integer.parseInt(f.get(o).toString()));
                } else {
                    pst.setInt(numeroParametro, 0);
                }
            }
            
            else if (f.getType().isAssignableFrom(Double.class) || f.getType().isAssignableFrom(Double.TYPE)) {
                System.out.println("Tipo: " + f.getType());
                if (f.get(o) != null){
                    pst.setDouble(numeroParametro, Double.parseDouble(f.get(o).toString()));
                } else {
                    pst.setDouble(numeroParametro, 0);
                }
            }
            
        }
        pst.executeUpdate();
        this.desconectar();

        System.out.println("Inserido!");
    }
}
    



