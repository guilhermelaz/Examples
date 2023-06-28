package model;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class DaoGenerico {
    private Connection conn;
    private Statement st;
    private final String TB_PREFIX = "tb_";
    private final String TB_SUFFIX = "s";

    
    private void conectar(){
        try {
            this.conn = GerenciadorConexao.pegarConexao();
            st = conn.createStatement();
        } catch (ClassNotFoundException e1) {
            System.out.println(e1.getMessage());
        }catch (SQLException e2) {
            System.out.println(e2.getMessage());
        }
    }

    private void desconectar(){
        try {
            st.close();
            conn.close();
        }catch (SQLException e) {
            System.out.println("Erro ao fechar a conexao: " 
                + e.getMessage());
        }        
    }    

    public void inserir(Object o) throws Exception{
        Class c = o.getClass();
        Field fields[] = c.getDeclaredFields();

        //montar a query para o pst
        String query = "INSERT INTO " + TB_PREFIX 
            + c.getSimpleName().toLowerCase() + TB_SUFFIX;
        String campos = "(";
        String aliases = " VALUES(";
        boolean separar = false;
        for (Field f : fields) {
            if(separar){
                campos = campos + ", ";
                aliases = aliases + ", ";
            }
            campos = campos + f.getName();
            aliases = aliases + "?";
            separar = true;
        }
        campos = campos + ") ";
        aliases = aliases + ") ";
        query = query + campos + aliases;

        // System.out.println(campos);
        // System.out.println(aliases);
        System.out.println("- - - > " + query);


        //criar o pst e setar os valores
        this.conectar();
        PreparedStatement pst = conn.prepareStatement(
            query, Statement.RETURN_GENERATED_KEYS);        

        //seta os valores dos params
        int numParam = 0;
        for (Field f : fields) {
            numParam++;
            f.setAccessible(true);
            System.out.println("tipo: " + f.getType());
            if(f.getType().isAssignableFrom(String.class)){
                if(f.get(o) != null){
                    pst.setString(numParam, f.get(o).toString());
                }else{
                    pst.setString(numParam, "");
                }
            }else if(f.getType().isAssignableFrom(Integer.class)
                    || f.getType().isAssignableFrom(Integer.TYPE)){
                pst.setInt(numParam, 
                    Integer.parseInt(f.get(o).toString()));
            }else if(f.getType().isAssignableFrom(Float.class)
                    || f.getType().isAssignableFrom(Float.TYPE)){
                
                pst.setFloat(numParam, 
                    Float.parseFloat(f.get(o).toString()));
            }else if(f.getType().isAssignableFrom(LocalDate.class)){
                
                if(f.get(o) != null){
                    pst.setDate(numParam, 
                        java.sql.Date.valueOf(f.get(o).toString()));
                }else{
                    pst.setDate(numParam, null);
                }
            }
        }
        pst.executeUpdate();
        this.desconectar();
    }

    public Object consultar(Object o, String chave, int id) throws Exception{
        Class c = o.getClass();
        Field fields[] = c.getDeclaredFields();
        Object objRet = c.getConstructor().newInstance();
        try {
            this.conectar();
            String query = "SELECT * FROM " + TB_PREFIX 
                + c.getSimpleName().toLowerCase() + TB_SUFFIX
                + " WHERE " + chave + " = " + id + ";";
            // System.out.println(query);
            // System.exit(9);
            ResultSet rs = st.executeQuery(query);
            if(rs.next()){
                for (Field f : fields){
                    f.setAccessible(true); 
                    if(f.getType().isAssignableFrom(String.class)){
        
                        f.set(objRet, rs.getString(f.getName().toString()));
                        
                    }else if(f.getType().isAssignableFrom(Integer.class)
                            || f.getType().isAssignableFrom(Integer.TYPE)){
                        
                        f.set(objRet, rs.getInt(f.getName().toString()));
                        
                    }else if(f.getType().isAssignableFrom(Float.class)
                    || f.getType().isAssignableFrom(Float.TYPE)){
                        
                        f.set(objRet, rs.getFloat(f.getName().toString()));
                        
                    }else if(f.getType().isAssignableFrom(LocalDate.class)){
                        
                        f.set(objRet, rs.getDate(
                            f.getName().toString()).toLocalDate());
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
        } finally {
            desconectar();
        }
        return objRet;
    }   

    public ArrayList<Object> buscarTodos(Object o) throws Exception{
        Class c = o.getClass();
        Field fields[] = c.getDeclaredFields();
        ArrayList<Object> resultados = new ArrayList<Object>();
        try {
            this.conectar();
            String query = "SELECT * FROM " + TB_PREFIX 
                + c.getSimpleName().toLowerCase() + TB_SUFFIX + ";";
            // System.out.println(query);
            // System.exit(9);
            ResultSet rs = st.executeQuery(query);
            while(rs.next()){
                Object objRet = c.getConstructor().newInstance();
                for (Field f : fields){
                    f.setAccessible(true); 
                    if(f.getType().isAssignableFrom(String.class)){
        
                        f.set(objRet, rs.getString(f.getName().toString()));
                        
                    }else if(f.getType().isAssignableFrom(Integer.class)
                            || f.getType().isAssignableFrom(Integer.TYPE)){
                        
                        f.set(objRet, rs.getInt(f.getName().toString()));
                        
                    }else if(f.getType().isAssignableFrom(Float.class)
                    || f.getType().isAssignableFrom(Float.TYPE)){
                        
                        f.set(objRet, rs.getFloat(f.getName().toString()));
                        
                    }else if(f.getType().isAssignableFrom(LocalDate.class)){
                        
                        f.set(objRet, rs.getDate(
                            f.getName().toString()).toLocalDate());
                    }
                }
                resultados.add(objRet);
            }
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
        } finally {
            desconectar();
        }
        return resultados;
    }   

    public ArrayList<Object> buscarTodosFiltro(
            Object o, String campo, String filtro) throws Exception{
        Class c = o.getClass();
        Field fields[] = c.getDeclaredFields();
        ArrayList<Object> resultados = new ArrayList<Object>();
        try {
            this.conectar();
            String query = "SELECT * FROM " + TB_PREFIX 
                + c.getSimpleName().toLowerCase() + TB_SUFFIX 
                + " WHERE " + campo + " LIKE '%" + filtro 
                + "%' ORDER BY " + campo + ";";
            // System.out.println(query);
            // System.exit(9);
            ResultSet rs = st.executeQuery(query);
            while(rs.next()){
                Object objRet = c.getConstructor().newInstance();
                for (Field f : fields){
                    f.setAccessible(true); 
                    if(f.getType().isAssignableFrom(String.class)){
        
                        f.set(objRet, rs.getString(f.getName().toString()));
                        
                    }else if(f.getType().isAssignableFrom(Integer.class)
                            || f.getType().isAssignableFrom(Integer.TYPE)){
                        
                        f.set(objRet, rs.getInt(f.getName().toString()));
                        
                    }else if(f.getType().isAssignableFrom(Float.class)
                    || f.getType().isAssignableFrom(Float.TYPE)){
                        
                        f.set(objRet, rs.getFloat(f.getName().toString()));
                        
                    }else if(f.getType().isAssignableFrom(LocalDate.class)){
                        
                        f.set(objRet, rs.getDate(
                            f.getName().toString()).toLocalDate());
                    }
                }
                resultados.add(objRet);
            }
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
        } finally {
            desconectar();
        }
        return resultados;
    }   
    
    public int excluir(Object o, String chave, int valor){
        Class c = o.getClass();
        int qtde = 0;
        try {
            this.conectar();
            String comando = "DELETE FROM " + TB_PREFIX 
                + c.getSimpleName().toLowerCase() + TB_SUFFIX 
                + " WHERE " + chave + " = " + valor +";";
            st.executeUpdate(comando);
            qtde = st.getUpdateCount();
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }finally{
            this.desconectar();
        }
        return qtde;
    }
    
    public int alterar(Object o, String chave, int id) throws Exception{
        Class c = o.getClass();
        Field fields[] = c.getDeclaredFields();
        
        //monta a query para o pst
        String query = "UPDATE " + TB_PREFIX + c.getSimpleName().toLowerCase() + "s SET ";
        boolean separar = false;
        for (Field f : fields){

            if(!f.getName().equals(chave)){//não pode tentar atualizar o id

                if(separar){
                    query = query + ", ";
                }
                separar = true;
                query = query + f.getName() + " = ?";
            }
        }
        query = query + " WHERE " + chave + " = " + id + ";";
        System.out.println("- - - - - > " + query);
        // System.exit(9);

        //cria o pst
        int qtde = 0;
		try {
            this.conectar();
            PreparedStatement pst = conn.prepareStatement(
                query,Statement.RETURN_GENERATED_KEYS);

            //seta os valores dos params
            int numParam = 0;
            for (Field f : fields){
                if(!f.getName().equals(chave)){//não pode tentar atualizar o id

                    numParam++;
                    f.setAccessible(true); 
                    if(f.getType().isAssignableFrom(String.class)){

                        if(f.get(o) != null){
                            pst.setString(numParam, f.get(o).toString());
                        }else{
                            pst.setString(numParam, "");
                        }
                        
                    }else if(f.getType().isAssignableFrom(Integer.class)
                            || f.getType().isAssignableFrom(Integer.TYPE)){
                        
                        pst.setInt(numParam, Integer.parseInt(f.get(o).toString()));
                        
                    }else if(f.getType().isAssignableFrom(Float.class)
                            || f.getType().isAssignableFrom(Float.TYPE)){
                        
                        pst.setFloat(numParam, Float.parseFloat(f.get(o).toString()));

                    }else if(f.getType().isAssignableFrom(LocalDate.class)){
                        
                        if(f.get(o) != null){
                            pst.setDate(numParam, java.sql.Date.valueOf(f.get(o).toString()));
                        }else{
                            pst.setDate(numParam, null);
                        }
                    }
                }
            }
            pst.executeUpdate();
            qtde = st.getUpdateCount();

        } catch (SQLException e) {  
            System.out.println("Erro ao atualizar:" + e.getMessage());  
		} catch (Exception e) {  
            System.out.println("Erro ao atualizar:" + e.getMessage());  
		} finally {  
            this.desconectar();
		}  
        return qtde;   
	}
}
